package com.blacklocus.qs.worker.simple;

import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.model.QSWorkerModel;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class SystemOutQSLogService implements QSLogService {

    @Override
    public void startedTask(QSTaskModel task) {
        System.out.println("Started: " + task);
    }

    @Override
    public void logTask(QSTaskModel task, Object contents) {
        System.out.println("Log: " + task + "\n\t" + contents);
    }

    @Override
    public void finishedTask(QSTaskModel task) {
        System.out.println("Finished: " + task);
    }

    @Override
    public void workerHeartbeat(QSWorkerModel worker) {
        System.out.println("Worker: " + worker);
    }
}
