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
import com.blacklocus.qs.realm.FindTasks;
import com.blacklocus.qs.realm.QSInfoService;
import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class ElasticSearchQSInfoServiceTest extends BaseJresTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFindTasks() {
        String index = "ElasticSearchQSInfoServiceTest.testFindTasks".toLowerCase();
        QSLogService logService = new ElasticSearchQSLogService(index, jres);

        QSTaskModel t1 = new QSTaskModel();
        t1.batchId = "bogus batch";
        t1.taskId = "bogus task1";
        t1.handler = "bogus handler";
        t1.params = ImmutableMap.of("bogus", "42");
        t1.workerId = "bogus worker";
        t1.started = System.currentTimeMillis();
        t1.finished = t1.started + 5L;
        t1.elapsed = 5L;
        t1.finishedHappy = true;
        logService.startedTask(t1);

        QSTaskModel t2 = new QSTaskModel();
        t2.batchId = "bogus batch";
        t2.taskId = "bogus task1";
        t2.handler = "bogus handler";
        t2.params = ImmutableMap.of("bogus", "42");
        t2.workerId = "bogus worker";
        t2.started = System.currentTimeMillis();
        logService.startedTask(t2);

        jres.quest(new JresRefresh(index));

        QSInfoService infoService = new ElasticSearchQSInfoService(index, jres);
        List<QSTaskModel> logTasks = infoService.findTasks(new FindTasks());
        Assert.assertEquals(2, logTasks.size());
        Assert.assertEquals(t1.toString(), logTasks.get(0).toString());
        Assert.assertEquals(t2.toString(), logTasks.get(1).toString());
    }
}
