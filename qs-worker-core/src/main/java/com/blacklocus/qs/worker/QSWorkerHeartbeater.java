package com.blacklocus.qs.worker;

import com.blacklocus.misc.ExceptingRunnable;
import com.blacklocus.qs.worker.model.QSLogWorkerModel;
import com.github.rholder.fauxflake.util.PidUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class QSWorkerHeartbeater extends ExceptingRunnable {

    public static final long HEARTBEAT_INTERVAL_MS = 60 * 1000;

    private final QSLogService logService;
    private final QSLogWorkerModel logWorker;

    public QSWorkerHeartbeater(QSLogService logService) {
        this.logService = logService;
        this.logWorker = new QSLogWorkerModel(getWorkerId(), null);
    }

    public String getWorkerId() {
        try {
            return InetAddress.getLocalHost().getHostName() + "-" + PidUtils.pid();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void go() throws Exception {
        while (!Thread.interrupted()) {
            logWorker.tick = System.currentTimeMillis();
            logService.workerHeartbeat(logWorker);
            Thread.sleep(HEARTBEAT_INTERVAL_MS);
        }
    }

}
