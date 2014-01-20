/**
 * Copyright 2013 BlackLocus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blacklocus.qs.worker;

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

    private final QSWorkerIdService workerIdService;

    TaskControlFunction(QSWorkerIdService workerIdService) {
        this.workerIdService = workerIdService;
    }

    @Override
    public Collection<TaskHandle> apply(Collection<QSTaskModel> tasks) {
        List<TaskHandle> taskHandles = new ArrayList<TaskHandle>(tasks.size());
        for (QSTaskModel task : tasks) {
            taskHandles.add(new TaskHandle(task, new QSLogTaskModel(task.batchId, task.taskId, task.handler,
                    task.params, workerIdService.getWorkerId(), System.currentTimeMillis(), null, null, false)));
        }
        return taskHandles;
    }
}
