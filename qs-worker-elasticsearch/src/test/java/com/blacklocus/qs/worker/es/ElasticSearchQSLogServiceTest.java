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
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.model.QSWorkerModel;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

public class ElasticSearchQSLogServiceTest extends BaseJresTest {

    @Test
    public void testLifecycle() {
        String index = "ElasticSearchQSLogServiceTest.testLifecycle".toLowerCase();
        QSLogService logService = new ElasticSearchQSLogService(index, jres);

        QSWorkerModel logWorker = new QSWorkerModel("test_worker", 0L);

        QSTaskModel logTask = new QSTaskModel("test_batch", "test_task", "test_handler", 1,
                ImmutableMap.of("whatwhat?", "watch southpark"), "test_worker", 0L, null, null, false);
        logService.startedTask(logTask);

        logWorker.tick = 123L;
        logService.workerHeartbeat(logWorker);

        logService.log(new QSLogModel(logTask.taskId, "test worker", "test_handler", System.currentTimeMillis(), "log message 1"));
        logService.log(new QSLogModel(logTask.taskId, "test worker", "test_handler", System.currentTimeMillis(), "log message 2"));

        logTask.finishedHappy = true;
        logTask.finished = 5L;
        logTask.elapsed = logTask.finished - logTask.started;
        logService.completedTask(logTask);

        logWorker.tick = 456L;
        logService.workerHeartbeat(logWorker);


        jres.quest(new JresRefresh(index));
        JresSearchReply reply = jres.quest(new JresSearch(index, null));
        Assert.assertEquals(new Integer(4), reply.getHits().getTotal()); // weak test
    }
}
