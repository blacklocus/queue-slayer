package com.blacklocus.qs.realm;

import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.blacklocus.qs.worker.model.QSTaskModel;

import java.util.List;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public interface QSInfoService {

    List<QSTaskModel> findTasks(FindTasks findTasks);

    List<QSLogTaskModel> findTaskLogs(FindTaskLogs findTaskLogs);

    List<QSLogTickModel> findLogTicks(FindLogTicks findLogTicks);

    List<QSLogTickModel> findWorkers(FindWorkers findWorkers);

}
