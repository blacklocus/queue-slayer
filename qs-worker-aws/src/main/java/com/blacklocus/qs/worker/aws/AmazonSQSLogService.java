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

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.model.QSWorkerModel;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class AmazonSQSLogService implements QSLogService {

    private final String queueUrl;
    private final AmazonSQS sqs;
    private final ObjectMapper objectMapper;

    public AmazonSQSLogService(String queueUrl, AmazonSQS sqs) {
        this.queueUrl = queueUrl;
        this.sqs = sqs;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void startedTask(QSTaskModel task) {
        // nothing
    }

    @Override
    public void log(QSLogModel log) {
        sqs.sendMessage(new SendMessageRequest(queueUrl, thing(log)));
    }

    @Override
    public void completedTask(QSTaskModel task) {
        //nothing
    }

    @Override
    public void workerHeartbeat(QSWorkerModel worker) {
        // nothing
    }

    private String thing(QSLogModel logTick) {
        try {
            return objectMapper.writeValueAsString(logTick);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
