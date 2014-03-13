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
        return new TaskKit<P>(task, params, worker, logService, workerIdService);
    }

    /**
     * @param klass to decode {@link #paramsJson()} with the bundled {@link ObjectMappers}
     */
    public TaskKit<P> newTaskKit(Class<P> klass) {
        return newTaskKit(ObjectMappers.treeToValue(paramsJson(), klass));
    }
}
