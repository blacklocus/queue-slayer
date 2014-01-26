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

import com.blacklocus.qs.worker.model.QSTaskModel;

import java.util.Map;

/**
 * Because ElasticSearch dynamically generates type mappings, and parameters between different types of tasks will
 * differ, this is a corollary to {@link QSTaskModel}: It should be identical except that params are nested one
 * level deeper underneath the handler name. If tasks of the same handler have different parameter schemas, then
 * the task.mapping.json will have to be augmented with lenient type mappings (e.g. every field is a string) to avoid
 * type conflicts.
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSTaskElasticSearchModel {

    public String batchId;
    public String taskId;
    public String handler;
    public Integer remainingAttempts;
    public HandlerWrapper params;

    public String workerId;
    public Long started;
    public Long finished;
    public Long elapsed;
    public Boolean finishedHappy;

    public QSTaskElasticSearchModel() {
    }

    public QSTaskElasticSearchModel(QSTaskModel normalModel) {
        this(normalModel.batchId, normalModel.taskId, normalModel.handler, normalModel.remainingAttempts, normalModel.params,
                normalModel.workerId, normalModel.started, normalModel.finished, normalModel.elapsed, normalModel.finishedHappy);
    }

    public QSTaskElasticSearchModel(String batchId, String taskId, String handler, Integer remainingAttempts, Map<String, ?> params,
                                    String workerId, Long started, Long finished, Long elapsed, Boolean finishedHappy) {
        this.batchId = batchId;
        this.taskId = taskId;
        this.handler = handler;
        this.remainingAttempts = remainingAttempts;
        // namespace params to handler name
        this.params = new HandlerWrapper(handler, params);
        this.workerId = workerId;
        this.started = started;
        this.finished = finished;
        this.elapsed = elapsed;
        this.finishedHappy = finishedHappy;
    }

    public QSTaskModel toNormalModel() {
        return new QSTaskModel(batchId, taskId, handler, remainingAttempts, params.get(handler),
                workerId, started, finished, elapsed, finishedHappy);
    }

}
