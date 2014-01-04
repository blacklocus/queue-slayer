package com.blacklocus.qs.worker.util;

import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.google.common.base.Function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
class TaskControlFunction implements Function<Collection<QSTaskModel>, Collection<TaskHandle>> {
    @Override
    public Collection<TaskHandle> apply(Collection<QSTaskModel> tasks) {
        List<TaskHandle> taskHandles = new ArrayList<TaskHandle>(tasks.size());
        for (QSTaskModel task : tasks) {
            taskHandles.add(new TaskHandle(task, new QSLogTaskModel(task.batchId, task.taskId, task.handler,
                    task.params, null, System.currentTimeMillis(), null, null, false)));
        }
        return taskHandles;
    }
}
