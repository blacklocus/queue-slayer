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
import com.blacklocus.qs.worker.api.QSWorker;
import com.blacklocus.qs.worker.api.QSWorkerIdService;
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Passed to the {@link QueueItemHandler#process(Object)} method for processing in {@link QSAssembly}.
 */
public class TaskKit<P> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskKit.class);


    private final QSTaskModel task;
    private final P params;

    final TaskKitFactory<P> factory;
    final QSWorker<P> worker;
    private final QSLogService logService;
    private final QSWorkerIdService workerIdService;

    TaskKit(QSTaskModel task, P params, TaskKitFactory<P> factory, QSWorker<P> worker, QSLogService logService, QSWorkerIdService workerIdService) {
        this.task = task;
        this.params = params;
        this.factory = factory;
        this.worker = worker;
        this.logService = logService;
        this.workerIdService = workerIdService;
    }

    public P params() {
        return params;
    }

    public void log(Object content) {
        QSLogModel logTick = new QSLogModel(task.taskId, workerIdService.getWorkerId(), task.handler, System.currentTimeMillis(), content);
        LOG.debug("{}", logTick); // prevents logTick.toString invocation unless debug-enabled
        logService.log(logTick);
    }
}
