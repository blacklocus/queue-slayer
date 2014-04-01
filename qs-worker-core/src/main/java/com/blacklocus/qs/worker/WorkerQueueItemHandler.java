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

import com.blacklocus.qs.QueueItemHandler;
import com.blacklocus.qs.worker.api.QSLogService;
import com.blacklocus.qs.worker.api.QSTaskService;
import com.blacklocus.qs.worker.api.QSWorker;
import com.blacklocus.qs.worker.api.QSWorkerIdService;
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.github.rholder.moar.concurrent.QueueingStrategy;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
class WorkerQueueItemHandler implements QueueItemHandler<QSTaskModel, TaskKit, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerQueueItemHandler.class);

    private final QueueingStrategy<QSTaskModel> queueingStrategy;
    private final QSTaskService taskService;
    private final QSLogService logService;
    private final QSWorkerIdService workerIdService;
    private final Map<String, QSWorker> workers;

    WorkerQueueItemHandler(QueueingStrategy<QSTaskModel> queueingStrategy, QSTaskService taskService,
                           QSLogService logService, QSWorkerIdService workerIdService,
                           Map<String, QSWorker> workers) {
        this.queueingStrategy = queueingStrategy;
        this.taskService = taskService;
        this.logService = logService;
        this.workerIdService = workerIdService;
        this.workers = workers;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void withFuture(QSTaskModel task, final Future<Pair<QSTaskModel, Object>> future) {
        // I don't know if this is useful or not.

        QSWorker worker = workers.get(task.handler);
        if (worker == null) {
            throw new RuntimeException("No worker available for worker identifier: " + task.handler);
        }

        final TaskKitFactory factory = new TaskKitFactory(task, worker, logService, workerIdService);
        worker.withFuture(factory, new Future<Pair<TaskKitFactory, Object>>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }

            @Override
            public Pair<TaskKitFactory, Object> get() throws InterruptedException, ExecutionException {
                Pair<QSTaskModel, Object> theFuture = future.get();
                return Pair.of(factory, theFuture.getRight());
            }

            @Override
            public Pair<TaskKitFactory, Object> get(long timeout, @SuppressWarnings("NullableProblems") TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {
                Pair<QSTaskModel, Object> theFuture = future.get(timeout, unit);
                return Pair.of(factory, theFuture.getRight());
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public TaskKit convert(QSTaskModel task) throws Exception {
        task.started = System.currentTimeMillis();
        task.workerId = workerIdService.getWorkerId();
        LOG.info("Task started: {}", task);
        logService.startedTask(task);

        QSWorker handler = workers.get(task.handler);
        if (handler == null) {
            throw new RuntimeException("No worker available for handler identifier: " + task.handler);
        }

        // The worker only needs to specify how it wants to deserialize its params.
        return handler.convert(new TaskKitFactory(task, handler, logService, workerIdService));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object process(TaskKit kit) throws Exception {
        return kit.worker.process(kit);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(QSTaskModel task, TaskKit kit, Object result) {
        kit.worker.onSuccess(kit.factory, kit, result);

        queueingStrategy.onBeforeRemove();

        taskService.closeTask(task);
        LOG.info("Task succeeded: {}", task);

        task.finishedHappy = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onError(QSTaskModel task, TaskKit kit, Throwable throwable) {
        try {
            kit.worker.onError(kit.factory, kit, throwable);
        } catch (Throwable anotherThrowable) {
            LOG.warn("There was an error in worker.onError It will replace the current throwable.", anotherThrowable);
            throwable = anotherThrowable;
        }

        queueingStrategy.onBeforeRemove();

        ImmutableMap<String, ImmutableMap<String, String>> exceptionDetails = ImmutableMap.of("exception", ImmutableMap.of(
                "class", throwable.getClass().getName(),
                "message", throwable.getMessage(),
                "stackTrace", ExceptionUtils.getStackTrace(throwable)
        ));
        QSLogModel logTick = new QSLogModel(task.taskId, workerIdService.getWorkerId(), task.handler, System.currentTimeMillis(), exceptionDetails);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Task erred: {}", logTick, throwable);
        } else {
            LOG.info("Task erred: {}", logTick);
        }
        logService.log(logTick);

        if (--task.remainingAttempts > 0) {
            taskService.resetTask(task);
        } else {
            taskService.closeTask(task);
        }

        task.finishedHappy = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onComplete(QSTaskModel task, TaskKit kit, Object result) {
        kit.worker.onComplete(kit.factory, kit, result);

        task.finished = System.currentTimeMillis();
        task.elapsed = task.finished - task.started;
        logService.completedTask(task);

        queueingStrategy.onAfterRemove(task);
    }

}