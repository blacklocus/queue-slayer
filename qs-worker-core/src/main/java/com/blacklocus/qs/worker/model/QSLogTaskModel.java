package com.blacklocus.qs.worker.model;

import java.util.Map;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSLogTaskModel {

    public String batchId;

    public String taskId;

    public String workerId;

    public Long started;

    public Long finished;

    public Map<?, ?> params;

}
