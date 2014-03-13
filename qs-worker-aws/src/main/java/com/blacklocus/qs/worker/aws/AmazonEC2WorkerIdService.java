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
package com.blacklocus.qs.worker.aws;

import com.amazonaws.util.EC2MetadataUtils;
import com.blacklocus.qs.worker.api.QSWorkerIdService;
import com.blacklocus.qs.worker.simple.HostNameQSWorkerIdService;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Identifies this worker by its Amazon EC2 instance id {@link EC2MetadataUtils#getInstanceId()}. Caches the value
 * after first lookup forever. If the read fails, defers to a fallback QSWorkerIdService -- The fallback value is not
 * cached and consulted every invocation.
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public class AmazonEC2WorkerIdService implements QSWorkerIdService {

    public static final String PROP_SKIP_EC2_META = "com.blacklocus.qs.aws.disableEC2Meta";


    private final QSWorkerIdService fallback;

    private final AtomicReference<String> ec2id = new AtomicReference<String>();


    /**
     * Defaults fallback to {@link HostNameQSWorkerIdService}
     */
    public AmazonEC2WorkerIdService() {
        this(new HostNameQSWorkerIdService());
    }

    /**
     * @param fallback in case the EC2 metadata lookup fails, most likely because this is not actually an EC2 instance.
     *                 Disable EC2 checking and go straight to the fallback by setting {@link #PROP_SKIP_EC2_META} to
     *                 <code>true</code>.
     */
    public AmazonEC2WorkerIdService(QSWorkerIdService fallback) {
        this.fallback = fallback;
    }

    @Override
    public String getWorkerId() {
        String id = ec2id.get();

        if (null == id) {
            if (!Boolean.valueOf(System.getProperty(PROP_SKIP_EC2_META, "false"))) {
                id = EC2MetadataUtils.getInstanceId();
                ec2id.set(id);
            }
        }

        // Maybe ec2 meta was disabled, maybe it failed. Whatever: fall back.
        if (StringUtils.isBlank(id)) {
            id = fallback.getWorkerId();
        }

        return id;
    }

}
