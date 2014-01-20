package com.blacklocus.qs.worker.es;

import com.blacklocus.jres.Jres;
import com.blacklocus.jres.request.search.JresSearch;
import com.blacklocus.jres.request.search.JresSearchBody;
import com.blacklocus.jres.response.search.JresSearchReply;
import com.blacklocus.qs.realm.FindLogs;
import com.blacklocus.qs.realm.FindTasks;
import com.blacklocus.qs.realm.FindWorkers;
import com.blacklocus.qs.realm.QSInfoService;
import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.blacklocus.qs.worker.model.QSLogWorkerModel;
import com.google.common.annotations.Beta;

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
    public List<QSLogTaskModel> findTasks(FindTasks findTasks) {
        JresSearchBody search = new JresSearchBody().size(100);
        JresSearchReply reply = jres.quest(new JresSearch(index, ElasticSearchQSLogService.INDEX_TYPE_TASK, search));
        return reply.getHitsAsType(QSLogTaskModel.class);
    }

    @Override
    public List<QSLogTickModel> findLogs(FindLogs findLogs) {
        JresSearchBody search = new JresSearchBody().size(100);
        JresSearchReply reply = jres.quest(new JresSearch(index, ElasticSearchQSLogService.INDEX_TYPE_TASK_LOG, search));
        return reply.getHitsAsType(QSLogTickModel.class);
    }

    @Override
    public List<QSLogWorkerModel> findWorkers(FindWorkers findWorkers) {
        JresSearchBody search = new JresSearchBody().size(100);
        JresSearchReply reply = jres.quest(new JresSearch(index, ElasticSearchQSLogService.INDEX_TYPE_WORKER, search));
        return reply.getHitsAsType(QSLogWorkerModel.class);
    }

}
