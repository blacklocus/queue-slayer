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
package com.blacklocus.qs.worker.model;

import java.util.Map;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSLogTaskModel {

    public String batchId;
    public String taskId;
    public String handler;
    public Map<?, ?> params;

    public String workerId;
    public Long started;
    public Long finished;
    public Long elapsed;
    public Boolean finishedHappy;

    public QSLogTaskModel() {
    }

    public QSLogTaskModel(String batchId, String taskId, String handler, Map<?, ?> params, String workerId,
                          Long started, Long finished, Long elapsed, Boolean finishedHappy) {
        this.batchId = batchId;
        this.taskId = taskId;
        this.handler = handler;
        this.params = params;
        this.workerId = workerId;
        this.started = started;
        this.finished = finished;
        this.elapsed = elapsed;
        this.finishedHappy = finishedHappy;
    }
}
