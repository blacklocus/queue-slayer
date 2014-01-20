package com.blacklocus.qs.worker.es;

import com.blacklocus.jres.BaseJresTest;
import com.blacklocus.jres.request.index.JresRefresh;
import com.blacklocus.qs.realm.FindTasks;
import com.blacklocus.qs.realm.QSInfoService;
import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class ElasticSearchQSInfoServiceTest extends BaseJresTest {

    @Test
    public void testFindTasks() {
        String index = "ElasticSearchQSInfoServiceTest.testFindTasks".toLowerCase();
        QSLogService logService = new ElasticSearchQSLogService(index, jres);

        QSLogTaskModel t1 = new QSLogTaskModel();
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

        QSLogTaskModel t2 = new QSLogTaskModel();
        t2.batchId = "bogus batch";
        t2.taskId = "bogus task1";
        t2.handler = "bogus handler";
        t2.params = ImmutableMap.of("bogus", "42");
        t2.workerId = "bogus worker";
        t2.started = System.currentTimeMillis();
        logService.startedTask(t2);

        jres.quest(new JresRefresh(index));

        QSInfoService infoService = new ElasticSearchQSInfoService(index, jres);
        List<QSLogTaskModel> logTasks = infoService.findTasks(new FindTasks());
        Assert.assertEquals(2, logTasks.size());
        Assert.assertEquals(t1.toString(), logTasks.get(0).toString());
        Assert.assertEquals(t2.toString(), logTasks.get(1).toString());
    }
}
