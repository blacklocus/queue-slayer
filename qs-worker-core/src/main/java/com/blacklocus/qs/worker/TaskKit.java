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
 * Passed to the {@link QueueItemHandler#process(Object)} method for processing in {@link QSAssemblies}.
 */
public class TaskKit<P> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskKit.class);


    private final QSTaskModel task;
    private final P params;

    final QSWorker<P> worker;
    private final QSLogService logService;
    private final QSWorkerIdService workerIdService;

    TaskKit(QSTaskModel task, P params, QSWorker<P> worker, QSLogService logService, QSWorkerIdService workerIdService) {
        this.task = task;
        this.params = params;
        this.worker = worker;
        this.logService = logService;
        this.workerIdService = workerIdService;
    }

    public P params() {
        return params;
    }

    public void log(Object content) {
        QSLogModel logTick = new QSLogModel(task.taskId, workerIdService.getWorkerId(), task.worker, System.currentTimeMillis(), content);
        LOG.debug("{}", logTick); // prevents logTick.toString invocation unless debug-enabled
        logService.log(logTick);
    }
}
