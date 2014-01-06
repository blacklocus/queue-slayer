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
import com.blacklocus.qs.worker.model.QSLogTaskModel;
import com.blacklocus.qs.worker.model.QSLogTickModel;
import com.blacklocus.qs.worker.model.QSLogWorkerModel;
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
    public void startedTask(QSLogTaskModel logTask) {
        // nothing
    }

    @Override
    public void logTask(QSLogTickModel logTick) {
        sqs.sendMessage(new SendMessageRequest(queueUrl, thing(logTick)));
    }

    @Override
    public void finishedTask(QSLogTaskModel logTask) {
        //nothing
    }

    @Override
    public void workerHeartbeat(QSLogWorkerModel logWorker) {
        // nothing
    }

    private String thing(QSLogTickModel logTick) {
        try {
            return objectMapper.writeValueAsString(logTick);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
