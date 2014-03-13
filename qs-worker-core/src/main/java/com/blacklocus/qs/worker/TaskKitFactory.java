package com.blacklocus.qs.worker;

import com.blacklocus.qs.QueueItemHandler;
import com.blacklocus.qs.worker.api.QSLogService;
import com.blacklocus.qs.worker.api.QSWorker;
import com.blacklocus.qs.worker.api.QSWorkerIdService;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Passed to the {@link QueueItemHandler#convert(Object)} method for conversion in {@link QSAssemblies}.
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

    public JsonNode params() {
        return task.params;
    }

    public TaskKit<P> newTaskHandle(P params) {
        return new TaskKit<P>(task, params, worker, logService, workerIdService);
    }
}
