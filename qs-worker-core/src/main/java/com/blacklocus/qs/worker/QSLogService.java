package com.blacklocus.qs.worker;

import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.model.QSWorkerModel;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public interface QSLogService {

    void startedTask(QSTaskModel task);

    void logTask(QSTaskModel task, Object contents);

    void finishedTask(QSTaskModel task);

    void workerHeartbeat(QSWorkerModel worker);
}
