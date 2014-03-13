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

import com.blacklocus.qs.worker.util.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSTaskModel {

    public String batchId;
    public String taskId;
    public String worker;
    public Integer remainingAttempts;
    public JsonNode params;

    public String workerId;
    public Long started;
    public Long finished;
    public Long elapsed;
    public Boolean finishedHappy;

    public QSTaskModel() {
    }

    /**
     * Constructor with fields important to queued jobs. Omits all tracking and status-oriented fields. Automatically
     * JSON-serializes `params`.
     */
    public QSTaskModel(String batchId, String taskId, String worker, Integer remainingAttempts, Object params) {
        this(batchId, taskId, worker, remainingAttempts, ObjectMappers.valueToTree(params));
    }

    /**
     * Constructor with fields important to queued jobs. Omits all tracking and status-oriented fields.
     */
    public QSTaskModel(String batchId, String taskId, String worker, Integer remainingAttempts, JsonNode params) {
        this(batchId, taskId, worker, remainingAttempts, params, null, null, null, null, null);
    }

    public QSTaskModel(String batchId, String taskId, String worker, Integer remainingAttempts, JsonNode params,
                       String workerId, Long started, Long finished, Long elapsed, Boolean finishedHappy) {
        this.batchId = batchId;
        this.taskId = taskId;
        this.worker = worker;
        this.remainingAttempts = remainingAttempts;
        this.params = params;
        this.workerId = workerId;
        this.started = started;
        this.finished = finished;
        this.elapsed = elapsed;
        this.finishedHappy = finishedHappy;
    }



    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("batchId", batchId)
                .add("taskId", taskId)
                .add("handler", worker)
                .add("remainingAttempts", remainingAttempts)
                .add("params", params)
                .add("workerId", workerId)
                .add("started", started)
                .add("finished", finished)
                .add("elapsed", elapsed)
                .add("finishedHappy", finishedHappy)
                .toString();
    }
}
