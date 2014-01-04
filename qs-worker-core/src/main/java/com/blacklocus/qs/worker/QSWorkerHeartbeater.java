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
import com.blacklocus.qs.worker.model.QSLogWorkerModel;
import com.github.rholder.fauxflake.util.PidUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class QSWorkerHeartbeater extends ExceptingRunnable {

    public static final long HEARTBEAT_INTERVAL_MS = 60 * 1000;

    private final QSLogService logService;
    private final QSLogWorkerModel logWorker;

    public QSWorkerHeartbeater(QSLogService logService) {
        this.logService = logService;
        this.logWorker = new QSLogWorkerModel(getWorkerId(), null);
    }

    public String getWorkerId() {
        try {
            return InetAddress.getLocalHost().getHostName() + "-" + PidUtils.pid();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void go() throws Exception {
        while (!Thread.interrupted()) {
            logWorker.tick = System.currentTimeMillis();
            logService.workerHeartbeat(logWorker);
            Thread.sleep(HEARTBEAT_INTERVAL_MS);
        }
    }

}
