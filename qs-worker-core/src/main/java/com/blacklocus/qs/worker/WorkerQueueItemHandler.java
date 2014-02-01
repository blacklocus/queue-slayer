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
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.github.rholder.moar.concurrent.QueueingStrategy;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
class WorkerQueueItemHandler implements QueueItemHandler<TaskHandle, TaskHandle, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerQueueItemHandler.class);

    private final QueueingStrategy<QSTaskModel> queueingStrategy;
    private final QSTaskService taskService;
    private final QSLogService logService;
    private final QSWorkerIdService workerIdService;
    private final Map<String, QSWorker<?>> workers;
    private final ObjectMapper objectMapper;

    WorkerQueueItemHandler(QueueingStrategy<QSTaskModel> queueingStrategy, QSTaskService taskService,
                           QSLogService logService, QSWorkerIdService workerIdService, Map<String, QSWorker<?>> workers,
                           ObjectMapper objectMapper) {
        this.queueingStrategy = queueingStrategy;
        this.taskService = taskService;
        this.logService = logService;
        this.workerIdService = workerIdService;
        this.workers = workers;
        this.objectMapper = objectMapper;
    }

    @Override
    public TaskHandle convert(TaskHandle task) throws Exception {
        LOG.info("Task started: {}", task.task);
        logService.startedTask(task.logTask);
        return task;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object process(TaskHandle taskHandle) throws Exception {
        QSTaskModel task = taskHandle.task;
        QSWorker worker = workers.get(task.handler);
        if (worker == null) {
            throw new RuntimeException("No worker available for handler identifier: " + task.handler);
        }

        final Object params;
        if (task.params instanceof JsonNode) {
            params = objectMapper.readValue((JsonNode) task.params, worker.getTypeReference());
        } else if (((Class<?>) worker.getTypeReference().getType()).isAssignableFrom(task.params.getClass())) {
            params = task.params;
        } else {
            throw new RuntimeException("Worker handles " + worker.getTypeReference().getType() +
                    " but params was " + task.params.getClass());
        }
        worker.undertake(params, new QSTaskLoggerDelegate(task));
        return null;
    }

    @Override
    public void onSuccess(TaskHandle taskHandle, Object result) {
        queueingStrategy.onBeforeRemove();

        QSTaskModel task = taskHandle.task;
        taskService.closeTask(task);
        LOG.debug("Task succeeded: {}", task);

        taskHandle.logTask.finishedHappy = true;
    }

    @Override
    public void onError(TaskHandle taskHandle, Throwable throwable) {
        queueingStrategy.onBeforeRemove();

        QSTaskModel task = taskHandle.task;

        ImmutableMap<String, ImmutableMap<String, String>> exceptionDetails = ImmutableMap.of("exception", ImmutableMap.of(
                "class", throwable.getClass().getName(),
                "message", throwable.getMessage(),
                "stackTrace", ExceptionUtils.getStackTrace(throwable)
        ));
        QSLogModel logTick = createLogTickModel(task, exceptionDetails);
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

        taskHandle.logTask.finishedHappy = false;
    }

    @Override
    public void onComplete(TaskHandle taskHandle) {
        QSTaskModel task = taskHandle.task;
        QSTaskModel logTask = taskHandle.logTask;

        logTask.finished = System.currentTimeMillis();
        logTask.elapsed = logTask.finished - logTask.started;
        logService.completedTask(logTask);

        queueingStrategy.onAfterRemove(task);
    }

    @Override
    public void withFuture(TaskHandle taskHandle, Future<Pair<TaskHandle, Object>> future) {
    }

    private QSLogModel createLogTickModel(QSTaskModel task, Object contents) {
        JsonNode logContent = objectMapper.valueToTree(contents);
        return new QSLogModel(task.taskId, workerIdService.getWorkerId(), task.handler, System.currentTimeMillis(), logContent);
    }

    class QSTaskLoggerDelegate implements QSTaskLogger {
        final QSTaskModel task;

        QSTaskLoggerDelegate(QSTaskModel task) {
            this.task = task;
        }

        @Override
        public void log(Object contents) {
            QSLogModel logTick = createLogTickModel(task, contents);
            LOG.debug("{}", logTick); // prevents logTick.toString invocation unless debug-enabled
            logService.log(logTick);
        }
    }

}