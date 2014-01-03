package com.blacklocus.qs.worker.model;

import com.google.common.base.Objects;

import java.util.Map;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSTaskModel {

    public String batchId;

    public String taskId;

    public String handler;

    public Map<String, ?> params;

    public QSTaskModel() {
    }

    public QSTaskModel(String batchId, String taskId, String handler, Map<String, ?> params) {
        this.batchId = batchId;
        this.taskId = taskId;
        this.handler = handler;
        this.params = params;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("batchId", batchId)
                .add("taskId", taskId)
                .add("handler", handler)
                .add("params", params)
                .toString();
    }
}
