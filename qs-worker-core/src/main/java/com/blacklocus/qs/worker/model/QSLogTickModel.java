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

import com.google.common.base.Objects;

/**
 * Any free-form log message related to some task in processing.
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSLogTickModel {

    public String taskId;
    public String workerId;
    public Long tick;
    public Object contents;

    public QSLogTickModel(String taskId, String workerId, Long tick, Object contents) {
        this.taskId = taskId;
        this.workerId = workerId;
        this.tick = tick;
        this.contents = contents;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("taskId", taskId)
                .add("workerId", workerId)
                .add("tick", tick)
                .add("contents", contents)
                .toString();
    }
}
