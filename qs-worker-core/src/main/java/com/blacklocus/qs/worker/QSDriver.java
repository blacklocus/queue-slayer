/**
 * Copyright 2013 BlackLocus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blacklocus.qs.worker;

import com.blacklocus.misc.ExceptingRunnable;
import com.blacklocus.qs.QueueItemHandler;
import com.blacklocus.qs.QueueReader;
import com.blacklocus.qs.worker.QSDriver.TaskSet;
import com.blacklocus.qs.worker.config.QSConfig;
import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.github.rholder.moar.concurrent.QueueingStrategies;
import com.github.rholder.moar.concurrent.QueueingStrategy;
import com.github.rholder.moar.concurrent.StrategicExecutors;
import com.github.rholder.moar.concurrent.thread.CallerBlocksPolicy;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.github.rholder.moar.concurrent.StrategicExecutors.DEFAULT_BALANCE_AFTER;
import static com.github.rholder.moar.concurrent.StrategicExecutors.DEFAULT_SMOOTHING_WEIGHT;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSDriver extends ExceptingRunnable implements Iterable<Collection<TaskSet>>, Iterator<Collection<TaskSet>>,
        QueueItemHandler<TaskSet, TaskSet, Object> {

    private final QSTaskService taskService;
    private final QSLogService logService;
    private final ExecutorService workerExecutorService;
    private final QueueingStrategy<QSTaskModel> heapBasedDelayStrategy;

    private final Map<String, QSWorker> workers = new HashMap<String, QSWorker>();

    public QSDriver(QSTaskService taskService, QSLogService logService) {
        this(taskService, logService, new MapConfiguration(Collections.<String, Object>emptyMap()));
    }

    public QSDriver(QSTaskService taskService, QSLogService logService, Configuration configuration) {
        this.taskService = taskService;
        this.logService = logService;
        CompositeConfiguration cfg = new CompositeConfiguration();
        cfg.addConfiguration(configuration);
        cfg.addConfiguration(QSConfig.DEFAULTS);

        this.workerExecutorService = StrategicExecutors.newBalancingThreadPoolExecutor(
                new ThreadPoolExecutor(
                        cfg.getInt(QSConfig.PROP_WORKER_POOL_CORE),
                        cfg.getInt(QSConfig.PROP_WORKER_POOL_MAX),
                        1, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(), new CallerBlocksPolicy()
                ),
                cfg.getFloat(QSConfig.PROP_WORKER_POOL_UTILIZATION), DEFAULT_SMOOTHING_WEIGHT, DEFAULT_BALANCE_AFTER
        );
        this.heapBasedDelayStrategy = QueueingStrategies.newHeapQueueingStrategy(
                cfg.getDouble(QSConfig.PROP_HEAP_STRATEGY_TRIGGER),
                cfg.getLong(QSConfig.PROP_HEAP_STRATEGY_MAX_DELAY),
                cfg.getLong(QSConfig.PROP_HEAP_STRATEGY_HINT)
        );
    }

    public QSDriver register(QSWorker... workers) {
        for (QSWorker worker : workers) {
            this.workers.put(worker.getHandlerName(), worker);
        }
        return this;
    }

    @Override
    protected void go() throws Exception {
        // This class is careful to minimize tasks sitting around in queues. If we have received a task from the
        // taskService, then we should intend to begin work on it.
        new QueueReader<TaskSet, TaskSet, Object>(this, this, workerExecutorService, 0).run();
    }

    // interface Iterable<Collection<QSTaskModel>>

    @Override
    public Iterator<Collection<TaskSet>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Collection<TaskSet> next() {
        // Incur delay before taking a task. Once we have taken a task, we should try our best to not idle with it
        // (actually work on it).
        heapBasedDelayStrategy.onBeforeAdd(null);
        QSTaskModel task = taskService.getAvailableTask();
        QSLogTaskModel logTask = new QSLogTaskModel(task.batchId, task.taskId, null, System.currentTimeMillis(), null, null,
                false, task.params);
        logService.startedTask(logTask);
        return Arrays.asList(new TaskSet(task, logTask));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    // interface QueueItemHandler<QSTaskModel, QSTaskModel, Object>

    @Override
    public TaskSet convert(TaskSet taskSet) throws Exception {
        return taskSet;
    }

    @Override
    public Object process(TaskSet taskSet) throws Exception {
        QSTaskModel task = taskSet.task;
        QSWorker worker = workers.get(task.handler);
        if (worker == null) {
            throw new RuntimeException("No worker available for handler identifier: " + task.handler);
        }

        return worker.undertake(new MapConfiguration(task.params), new QSTaskLoggerDelegate(task));
    }

    @Override
    public void onSuccess(TaskSet taskSet, Object result) {
        taskService.commitTask(taskSet.task);
        taskSet.logTask.finishedHappy = true;
    }

    @Override
    public void onError(TaskSet taskSet, Throwable throwable) {
        QSTaskModel task = taskSet.task;
        taskService.resetTask(task);
        
        ImmutableMap<String, ImmutableMap<String, String>> exceptionDetails = ImmutableMap.of("exception", ImmutableMap.of(
                "class", throwable.getClass().getName(),
                "message", throwable.getMessage(),
                "stackTrace", ExceptionUtils.getStackTrace(throwable)
        ));
        logService.logTask(createLogTickModel(task, exceptionDetails));
        
        taskSet.logTask.finishedHappy = false;
    }

    @Override
    public void onComplete(TaskSet taskSet) {
        QSLogTaskModel logTask = taskSet.logTask;
        logTask.finished = System.currentTimeMillis();
        logTask.elapsed = logTask.finished - logTask.started;
        logService.finishedTask(logTask);

        heapBasedDelayStrategy.onAfterRemove(taskSet.task);
    }

    @Override
    public void withFuture(TaskSet queueItem, Future<Pair<TaskSet, Object>> future) {
    }


    private QSLogTickModel createLogTickModel(QSTaskModel task, Object contents) {
        return new QSLogTickModel(task.taskId, System.currentTimeMillis(), contents);
    }

    class QSTaskLoggerDelegate implements QSTaskLogger {
        final QSTaskModel task;

        QSTaskLoggerDelegate(QSTaskModel task) {
            this.task = task;
        }

        @Override
        public void log(Object contents) {
            logService.logTask(createLogTickModel(task, contents));
        }
    }
    
    static class TaskSet {
        final QSTaskModel task;
        final QSLogTaskModel logTask;

        TaskSet(QSTaskModel task, QSLogTaskModel logTask) {
            this.task = task;
            this.logTask = logTask;
        }
    }
}

