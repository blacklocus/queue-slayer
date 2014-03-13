/**
 * Copyright 2013 BlackLocus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blacklocus.qs.worker.simple;

import com.blacklocus.qs.worker.api.QSWorkerIdService;
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
