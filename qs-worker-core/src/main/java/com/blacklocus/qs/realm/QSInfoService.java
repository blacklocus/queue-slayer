package com.blacklocus.qs.realm;

import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.blacklocus.qs.worker.model.QSLogWorkerModel;
import com.google.common.annotations.Beta;

import java.util.List;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
@Beta
public interface QSInfoService {

    List<QSLogTaskModel> findTasks(FindTasks findTasks);

    List<QSLogTickModel> findLogs(FindLogs findLogs);

    List<QSLogWorkerModel> findWorkers(FindWorkers findWorkers);

}
