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

import com.blacklocus.misc.ExceptingRunnable;
import com.blacklocus.qs.worker.model.QSWorkerModel;

class QSWorkerHeartbeater extends ExceptingRunnable {

    public static final long HEARTBEAT_INTERVAL_MS = 60 * 1000;

    private final QSWorkerIdService workerIdService;
    private final QSLogService logService;

    public QSWorkerHeartbeater(QSWorkerIdService workerIdService, QSLogService logService) {
        this.workerIdService = workerIdService;
        this.logService = logService;
    }

    @Override
    protected void go() throws Exception {
        while (!Thread.interrupted()) {
            logService.workerHeartbeat(new QSWorkerModel(workerIdService.getWorkerId(), System.currentTimeMillis()));
            Thread.sleep(HEARTBEAT_INTERVAL_MS);
        }
    }

}
