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

import com.blacklocus.jres.BaseJresTest;
import com.blacklocus.jres.request.index.JresRefresh;
import com.blacklocus.jres.request.search.JresSearch;
import com.blacklocus.jres.response.search.JresSearchReply;
import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.blacklocus.qs.worker.model.QSLogWorkerModel;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

public class ElasticSearchQSLogServiceTest extends BaseJresTest {

    @Test
    public void testLifecycle() {
        QSLogService logService = new ElasticSearchQSLogService("test", jres);

        QSLogWorkerModel logWorker = new QSLogWorkerModel("test_worker", 0L);

        QSLogTaskModel logTask = new QSLogTaskModel("test_batch", "test_task", "test_worker", 0L,
                null, null, false, ImmutableMap.of("whatwhat?", "watch southpark"));
        logService.startedTask(logTask);

        logWorker.tick = 123L;
        logService.workerHeartbeat(logWorker);

        logService.logTask(new QSLogTickModel(logTask.taskId, System.currentTimeMillis(), "log message 1"));
        logService.logTask(new QSLogTickModel(logTask.taskId, System.currentTimeMillis(), "log message 2"));

        logTask.finishedHappy = true;
        logTask.finished = 5L;
        logTask.elapsed = logTask.finished - logTask.started;
        logService.finishedTask(logTask);

        logWorker.tick = 456L;
        logService.workerHeartbeat(logWorker);


        jres.quest(new JresRefresh("test"));
        JresSearchReply reply = jres.quest(new JresSearch());
        Assert.assertEquals(new Integer(4), reply.getHits().getTotal()); // weak test
    }
}
