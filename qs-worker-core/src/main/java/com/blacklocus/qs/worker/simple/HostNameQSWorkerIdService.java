package com.blacklocus.qs.worker.simple;

import com.blacklocus.qs.worker.QSWorkerIdService;
import com.github.rholder.fauxflake.util.PidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class HostNameQSWorkerIdService implements QSWorkerIdService {

    private static final Logger LOG = LoggerFactory.getLogger(HostNameQSWorkerIdService.class);

    public static final String MACHINE_ID;

    static {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.error("Failed to resolve hostname. This worker id will start with \"unknown\"");
            hostname = "unknown";
        }
        MACHINE_ID = hostname + '-' + PidUtils.pid();
    }

    @Override
    public String getWorkerId() {
        return MACHINE_ID;
    }
}
