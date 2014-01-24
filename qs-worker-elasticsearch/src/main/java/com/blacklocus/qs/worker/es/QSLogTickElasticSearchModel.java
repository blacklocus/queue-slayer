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
package com.blacklocus.qs.worker.es;

import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * See rationale of {@link QSLogTaskElasticSearchModel}
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSLogTickElasticSearchModel {

    public String taskId;
    public String workerId;
    public String handler;
    public Long tick;
    public HandlerWrapper contents;

    public QSLogTickElasticSearchModel() {
    }

    public QSLogTickElasticSearchModel(QSLogTickModel normalModel) {
        this(normalModel.taskId, normalModel.workerId, normalModel.handler, normalModel.tick,
                normalModel.contents instanceof Map ? (Map<?, ?>) normalModel.contents : ImmutableMap.of("value", normalModel.contents));
    }

    public QSLogTickElasticSearchModel(String taskId, String workerId, String handler, Long tick, Map<?, ?> contents) {
        this.taskId = taskId;
        this.workerId = workerId;
        this.handler = handler;
        this.tick = tick;
        this.contents = new HandlerWrapper(handler, contents);
    }

    public QSLogTickModel toNormalModel() {
        return new QSLogTickModel(taskId, workerId, handler, tick, contents.get(handler));
    }
}
