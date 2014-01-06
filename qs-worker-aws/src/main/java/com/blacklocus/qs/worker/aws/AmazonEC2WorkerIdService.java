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
import com.blacklocus.qs.worker.QSWorkerIdService;
import com.blacklocus.qs.worker.simple.HostNameQSWorkerIdService;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class AmazonEC2WorkerIdService implements QSWorkerIdService {

    public static final String PROP_SKIP_EC2_META = "com.blacklocus.qs.aws.disableEC2Meta";

    private final QSWorkerIdService fallback = new HostNameQSWorkerIdService();

    @Override
    public String getWorkerId() {
        String id = null;

        if (!Boolean.valueOf(System.getProperty(PROP_SKIP_EC2_META, "false"))) {
            id = EC2MetadataUtils.getInstanceId();
        }

        if (StringUtils.isBlank(id)) {
            id = fallback.getWorkerId();
        }

        return id;
    }

}
