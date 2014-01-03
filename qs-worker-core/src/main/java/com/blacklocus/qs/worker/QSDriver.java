package com.blacklocus.qs.worker;

import com.blacklocus.misc.ExceptingRunnable;
import com.blacklocus.qs.QueueItemHandler;
import com.blacklocus.qs.QueueReader;
import com.blacklocus.qs.worker.config.QSConfig;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.github.rholder.moar.concurrent.QueueingStrategies;
import com.github.rholder.moar.concurrent.QueueingStrategy;
import com.github.rholder.moar.concurrent.StrategicExecutors;
import com.github.rholder.moar.concurrent.thread.CallerBlocksPolicy;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collection;
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
public class QSDriver extends ExceptingRunnable implements Iterable<Collection<QSTaskModel>>, Iterator<Collection<QSTaskModel>>,
        QueueItemHandler<QSTaskModel, QSTaskModel, Object> {

    private final QSTaskService taskService;
    private final QSLogService logService;
    private final ExecutorService workerExecutorService;
    private final QueueingStrategy<QSTaskModel> heapBasedDelayStrategy;

    private final Map<String, QSWorker> workers = new HashMap<String, QSWorker>();

    public QSDriver(QSTaskService taskService, QSLogService logService) {
        this.taskService = taskService;
        this.logService = logService;

        this.workerExecutorService = StrategicExecutors.newBalancingThreadPoolExecutor(
                new ThreadPoolExecutor(QSConfig.WORKER_POOL_CORE, QSConfig.WORKER_POOL_MAX, 1, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(), new CallerBlocksPolicy()),
                QSConfig.WORKER_POOL_UTILIZATION, DEFAULT_SMOOTHING_WEIGHT, DEFAULT_BALANCE_AFTER
        );
        this.heapBasedDelayStrategy = QueueingStrategies.newHeapQueueingStrategy(
                QSConfig.HEAP_STRATEGY_TRIGGER, QSConfig.HEAP_STRATEGY_MAX_DELAY, QSConfig.HEAP_STRATEGY_HINT);
    }

    public void register(String handlerName, QSWorker worker) {
        this.workers.put(handlerName, worker);
    }

    @Override
    protected void go() throws Exception {
        // This class is careful to minimize tasks sitting around in queues. If we have received a task from the
        // taskService, then we should intend to begin work on it.
        new QueueReader<QSTaskModel, QSTaskModel, Object>(this, this, workerExecutorService, 0).run();
    }

    // interface Iterable<Collection<QSTaskModel>>

    @Override
    public Iterator<Collection<QSTaskModel>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Collection<QSTaskModel> next() {
        // Incur delay before taking a task. Once we have taken a task, we should try our best to not idle with it
        // (actually work on it).
        heapBasedDelayStrategy.onBeforeAdd(null);
        return Arrays.asList(taskService.getAvailableTask());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    // interface QueueItemHandler<QSTaskModel, QSTaskModel, Object>

    @Override
    public QSTaskModel convert(QSTaskModel task) throws Exception {
        return task;
    }

    @Override
    public Object process(QSTaskModel task) throws Exception {
        QSWorker worker = workers.get(task.handler);
        if (worker == null) {
            throw new RuntimeException("No worker available for handler identifier: " + task.handler);
        }
        logService.startedTask(task);
        return worker.undertake(new MapConfiguration(task.params), new QSTaskLoggerDelegate(task));
    }

    @Override
    public void onSuccess(QSTaskModel task, Object result) {
        taskService.commitTask(task);
        logService.finishedTask(task);
    }

    @Override
    public void onError(QSTaskModel task, Throwable throwable) {
        taskService.resetTask(task);
        logService.logTask(task, ImmutableMap.of("exception", ImmutableMap.of(
                "class", throwable.getClass().getName(),
                "message", throwable.getMessage(),
                "stackTrace", ExceptionUtils.getStackTrace(throwable)
        )));
    }

    @Override
    public void onComplete(QSTaskModel task) {
        heapBasedDelayStrategy.onAfterRemove(task);
    }

    @Override
    public void withFuture(QSTaskModel task, Future<Pair<QSTaskModel, Object>> future) {
    }

    class QSTaskLoggerDelegate implements QSTaskLogger {
        final QSTaskModel task;

        QSTaskLoggerDelegate(QSTaskModel task) {
            this.task = task;
        }

        @Override
        public void log(Object content) {
            logService.logTask(task, content);
        }
    }
}

