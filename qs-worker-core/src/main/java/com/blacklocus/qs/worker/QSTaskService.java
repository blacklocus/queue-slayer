package com.blacklocus.qs.worker;

import com.blacklocus.qs.worker.model.QSTaskModel;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public interface QSTaskService {

    QSTaskModel getAvailableTask();

    void resetTask(QSTaskModel task);

    void commitTask(QSTaskModel task);

}
