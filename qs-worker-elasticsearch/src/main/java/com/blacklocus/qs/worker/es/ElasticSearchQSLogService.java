package com.blacklocus.qs.worker.es;

import com.blacklocus.jres.Jres;
import com.blacklocus.jres.request.index.JresCreateIndex;
import com.blacklocus.jres.request.index.JresIndexDocument;
import com.blacklocus.jres.request.index.JresIndexExists;
import com.blacklocus.jres.request.mapping.JresPutMapping;
import com.blacklocus.jres.request.mapping.JresTypeExists;
import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.blacklocus.qs.worker.model.QSLogWorkerModel;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class ElasticSearchQSLogService implements QSLogService {

    public static final String INDEX_TYPE_TASK = "task";
    public static final String INDEX_TYPE_TASK_LOG = "taskLog";
    public static final String INDEX_TYPE_WORKER = "worker";

    private final String index;
    private final Jres jres;

    public ElasticSearchQSLogService(String index, Jres jres) {
        this.index = index;
        this.jres = jres;

        verifyElasticSearchMappings();
    }

    @Override
    public void startedTask(QSLogTaskModel logTask) {
        // true - createOnly besides its literal assurance, add that if for some reason the finishedTask submission gets
        // there first, we won't overwrite it with the startedTask which would be missing 'finished' information (say,
        // due to parallelized batching submissions or some such).
        jres.quest(new JresIndexDocument(index, INDEX_TYPE_TASK, logTask.taskId, logTask, true));
    }

    @Override
    public void logTask(QSLogTickModel logTick) {
        // Append-only, hence generated ID
        jres.quest(new JresIndexDocument(index, INDEX_TYPE_TASK_LOG, null, logTick));
    }

    @Override
    public void finishedTask(QSLogTaskModel logTask) {
        // Possibly updates the document submitted by startedTask, if it has been received by ElasticSearch
        jres.quest(new JresIndexDocument(index, INDEX_TYPE_TASK, logTask.taskId, logTask));
    }

    @Override
    public void workerHeartbeat(QSLogWorkerModel logWorker) {
        jres.quest(new JresIndexDocument(index, INDEX_TYPE_WORKER, logWorker.workerId, logWorker));
    }

    private void verifyElasticSearchMappings() {
        if (!jres.bool(new JresIndexExists(index)).verity()) {
            jres.quest(new JresCreateIndex(index));
        }
        if (!jres.bool(new JresTypeExists(index, INDEX_TYPE_TASK)).verity()) {
            jres.quest(new JresPutMapping(index, INDEX_TYPE_TASK, getElasticSearchJson("/task.mapping.json")));
        }
        if (!jres.bool(new JresTypeExists(index, INDEX_TYPE_TASK_LOG)).verity()) {
            jres.quest(new JresPutMapping(index, INDEX_TYPE_TASK_LOG, getElasticSearchJson("/taskLog.mapping.json")));
        }
        if (!jres.bool(new JresTypeExists(index, INDEX_TYPE_WORKER)).verity()) {
            jres.quest(new JresPutMapping(index, INDEX_TYPE_WORKER, getElasticSearchJson("/worker.mapping.json")));
        }
    }

    private static String getElasticSearchJson(String file) {
        try {
            return IOUtils.toString(ElasticSearchQSLogService.class.getResource(file).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
