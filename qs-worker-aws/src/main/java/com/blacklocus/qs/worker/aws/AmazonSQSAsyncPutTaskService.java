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

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.blacklocus.qs.worker.model.QSTaskModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class AmazonSQSAsyncPutTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSAsyncPutTaskService.class);

    private final String queueUrl;
    private final ObjectMapper objectMapper;
    private final AmazonSQSAsync sqs;

    public AmazonSQSAsyncPutTaskService(String queueUrl) {
        this(queueUrl, new ObjectMapper(), new AmazonSQSAsyncClient());
    }

    public AmazonSQSAsyncPutTaskService(String queueUrl, ObjectMapper objectMapper, AmazonSQSAsync sqs) {
        this.queueUrl = queueUrl;
        this.objectMapper = objectMapper;
        this.sqs = sqs;
    }

    public void putTask(QSTaskModel task) {
        try {
            LOG.info("Queueing task: {}", task);
            String messageBody = objectMapper.writeValueAsString(task);
            sqs.sendMessage(new SendMessageRequest(queueUrl, messageBody));
            LOG.debug("Queued to {}, message\n\t{}", queueUrl, messageBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
