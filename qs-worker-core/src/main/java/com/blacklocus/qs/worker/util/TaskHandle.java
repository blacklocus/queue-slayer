package com.blacklocus.qs.worker.util;

import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSTaskModel;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
class TaskHandle {
    public final QSTaskModel task;
    public final QSLogTaskModel logTask;

    TaskHandle(QSTaskModel task, QSLogTaskModel logTask) {
        this.task = task;
        this.logTask = logTask;
    }
}
