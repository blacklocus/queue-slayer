package com.blacklocus.qs.worker.es;

import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * See rationale of {@link QSLogTaskElasticSearchModel}
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSLogTickElasticSearchModel {

    public String taskId;
    public String workerId;
    public String handler;
    public Long tick;
    public HandlerWrapper contents;

    public QSLogTickElasticSearchModel() {
    }

    public QSLogTickElasticSearchModel(QSLogTickModel normalModel) {
        this(normalModel.taskId, normalModel.workerId, normalModel.handler, normalModel.tick,
                normalModel.contents instanceof Map ? (Map<?, ?>) normalModel.contents : ImmutableMap.of("value", normalModel.contents));
    }

    public QSLogTickElasticSearchModel(String taskId, String workerId, String handler, Long tick, Map<?, ?> contents) {
        this.taskId = taskId;
        this.workerId = workerId;
        this.handler = handler;
        this.tick = tick;
        this.contents = new HandlerWrapper(handler, contents);
    }

    public QSLogTickModel toNormalModel() {
        return new QSLogTickModel(taskId, workerId, handler, tick, contents.get(handler));
    }
}
