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

import com.blacklocus.jres.Jres;
import com.blacklocus.jres.request.search.JresSearch;
import com.blacklocus.jres.request.search.JresSearchBody;
import com.blacklocus.jres.response.search.JresSearchReply;
import com.blacklocus.qs.realm.FindLogs;
import com.blacklocus.qs.realm.FindTasks;
import com.blacklocus.qs.realm.FindWorkers;
import com.blacklocus.qs.realm.QSInfoService;
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.model.QSWorkerModel;
import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
@Beta
public class ElasticSearchQSInfoService implements QSInfoService {

    private final String index;
    private final Jres jres;

    public ElasticSearchQSInfoService(String index, Jres jres) {
        this(index, new ElasticSearchInitializer(index, jres), jres);
    }

    public ElasticSearchQSInfoService(String index, ElasticSearchInitializer initializer, Jres jres) {
        this.index = index;
        this.jres = jres;
        initializer.verifyElasticSearchMappings();
    }

    @Override
    public List<QSTaskModel> findTasks(FindTasks findTasks) {
        JresSearchBody search = new JresSearchBody().size(100);
        JresSearchReply reply = jres.quest(new JresSearch(index, ElasticSearchQSLogService.INDEX_TYPE_TASK, search));
        return Lists.transform(reply.getHitsAsType(QSTaskElasticSearchModel.class), new Function<QSTaskElasticSearchModel, QSTaskModel>() {
            @Override
            public QSTaskModel apply(QSTaskElasticSearchModel input) {
                return input.toNormalModel();
            }
        });
    }

    @Override
    public List<QSLogModel> findLogs(FindLogs findLogs) {
        JresSearchBody search = new JresSearchBody().size(100);
        JresSearchReply reply = jres.quest(new JresSearch(index, ElasticSearchQSLogService.INDEX_TYPE_TASK_LOG, search));
        return Lists.transform(reply.getHitsAsType(QSLogElasticSearchModel.class), new Function<QSLogElasticSearchModel, QSLogModel>() {
            @Override
            public QSLogModel apply(QSLogElasticSearchModel input) {
                return input.toNormalModel();
            }
        });
    }

    @Override
    public List<QSWorkerModel> findWorkers(FindWorkers findWorkers) {
        JresSearchBody search = new JresSearchBody().size(100);
        JresSearchReply reply = jres.quest(new JresSearch(index, ElasticSearchQSLogService.INDEX_TYPE_WORKER, search));
        return reply.getHitsAsType(QSWorkerModel.class);
    }

}
