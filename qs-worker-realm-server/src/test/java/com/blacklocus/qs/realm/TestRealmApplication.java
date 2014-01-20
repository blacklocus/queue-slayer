package com.blacklocus.qs.realm;

import com.blacklocus.jres.BaseJresTest;
import com.blacklocus.qs.worker.es.ElasticSearchQSInfoService;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class TestRealmApplication extends BaseJresTest {

    public static void main(String[] args) throws Exception {
        BaseJresTest.startLocalElasticSearch();
        new TestRealmApplication().run();
    }

    public void run() throws Exception {
        String testIndex = "TestRealmApplication".toLowerCase();
        QSInfoService infoService = new ElasticSearchQSInfoService(testIndex, jres);
        new QSRealmBuilder().infoService(infoService).run();
    }
}
