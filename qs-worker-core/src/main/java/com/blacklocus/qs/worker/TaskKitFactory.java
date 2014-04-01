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
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.util.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Passed to the {@link QueueItemHandler#convert(Object)} method for conversion in a {@link QSAssembly}.
 */
public class TaskKitFactory<P> {

    private final QSTaskModel task;

    private final QSWorker<P> worker;
    private final QSLogService logService;
    private final QSWorkerIdService workerIdService;

    public TaskKitFactory(QSTaskModel task, QSWorker<P> worker, QSLogService logService, QSWorkerIdService workerIdService) {
        this.worker = worker;
        this.task = task;
        this.logService = logService;
        this.workerIdService = workerIdService;
    }

    public JsonNode paramsJson() {
        return task.params;
    }

    /**
     * @param params parameters decoded from {@link #paramsJson()}
     */
    public TaskKit<P> newTaskKit(P params) {
        return new TaskKit<P>(task, params, this, worker, logService, workerIdService);
    }

    /**
     * @param klass to decode {@link #paramsJson()} with the bundled {@link ObjectMappers}
     */
    public TaskKit<P> newTaskKit(Class<P> klass) {
        return newTaskKit(ObjectMappers.treeToValue(paramsJson(), klass));
    }
}
