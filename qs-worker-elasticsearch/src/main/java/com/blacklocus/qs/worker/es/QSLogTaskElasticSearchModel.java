package com.blacklocus.qs.worker.es;

import com.blacklocus.qs.worker.model.QSLogTaskModel;

import java.util.Map;

/**
 * Because ElasticSearch dynamically generates type mappings, and parameters between different types of tasks will
 * differ, this is a corollary to {@link QSLogTaskModel}: It should be identical except that params are nested one
 * level deeper underneath the handler name. If tasks of the same handler have different parameter schemas, then
 * the task.mapping.json will have to be augmented with lenient type mappings (e.g. every field is a string) to avoid
 * type conflicts.
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSLogTaskElasticSearchModel {


    public String batchId;
    public String taskId;
    public String handler;
    public HandlerWrapper params;

    public String workerId;
    public Long started;
    public Long finished;
    public Long elapsed;
    public Boolean finishedHappy;

    public QSLogTaskElasticSearchModel() {
    }

    public QSLogTaskElasticSearchModel(QSLogTaskModel normalModel) {
        this(normalModel.batchId, normalModel.taskId, normalModel.handler, normalModel.params, normalModel.workerId,
                normalModel.started, normalModel.finished, normalModel.elapsed, normalModel.finishedHappy);
    }

    public QSLogTaskElasticSearchModel(String batchId, String taskId, String handler, Map<?, ?> params, String workerId,
                          Long started, Long finished, Long elapsed, Boolean finishedHappy) {
        this.batchId = batchId;
        this.taskId = taskId;
        this.handler = handler;
        // namespace params to handler name
        this.params = new HandlerWrapper(handler, params);
        this.workerId = workerId;
        this.started = started;
        this.finished = finished;
        this.elapsed = elapsed;
        this.finishedHappy = finishedHappy;
    }

    public QSLogTaskModel toNormalModel() {
        return new QSLogTaskModel(batchId, taskId, handler, params.get(handler), workerId, started, finished, elapsed, finishedHappy);
    }

}
