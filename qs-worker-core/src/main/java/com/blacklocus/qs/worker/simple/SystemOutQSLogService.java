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
package com.blacklocus.qs.worker.simple;

import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.model.QSLogModel;
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
    public void log(QSLogModel log) {
        System.out.println("Log: " + log);
    }

    @Override
    public void completedTask(QSTaskModel task) {
        System.out.println("Finished: " + task);
    }

    @Override
    public void workerHeartbeat(QSWorkerModel worker) {
        System.out.println("Worker: " + worker);
    }
}
