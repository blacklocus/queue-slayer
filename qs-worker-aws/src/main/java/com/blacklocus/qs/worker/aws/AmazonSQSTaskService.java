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
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.blacklocus.qs.worker.QSTaskService;
import com.blacklocus.qs.worker.model.QSTaskModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AmazonSQSTaskService implements QSTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSTaskService.class);

    private final String queueUrl;
    private final Long pollingIntervalMs;
    private final AmazonSQS sqs;
    private final ObjectMapper objectMapper;

    private final Map<QSTaskModel, String> receiptHandles = new ConcurrentHashMap<QSTaskModel, String>();

    public AmazonSQSTaskService(String queueUrl) {
        this(queueUrl, new AmazonSQSClient());
    }

    public AmazonSQSTaskService(String queueUrl, AmazonSQS sqs) {
        this(queueUrl, 60 * 1000L, sqs);
    }

    public AmazonSQSTaskService(String queueUrl, Long pollingIntervalMs, AmazonSQS sqs) {
        this.queueUrl = queueUrl;
        this.pollingIntervalMs = pollingIntervalMs;
        this.sqs = sqs;
        this.objectMapper = new ObjectMapper();
    }

    @Override
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

    @Override
    public QSTaskModel getAvailableTask() {
        QSTaskModel task = null;
        while (task == null) {
            ReceiveMessageResult result = sqs.receiveMessage(new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(1));
            assert result.getMessages().size() <= 1;

            if (result.getMessages().size() == 1) {
                Message message = result.getMessages().get(0);

                try {
                    task = objectMapper.readValue(message.getBody(), QSTaskModel.class);
                    receiptHandles.put(task, message.getReceiptHandle());
                } catch (IOException e) {
                    LOG.error("Failed to parse message from " + queueUrl + "\n\t" + message);
                }

            } else {
                sleep();
            }
        }
        return task;
    }

    @Override
    public void resetTask(QSTaskModel task) {
        try {

            // Re-queue the task in its current state - remainingAttempts has been changed by the WorkerQueueItemHandler.
            String message = objectMapper.writeValueAsString(task);
            sqs.sendMessage(new SendMessageRequest(queueUrl, message));

            // Remove the currently in-flight task.
            closeTask(task);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void closeTask(QSTaskModel task) {
        String receiptHandle = receiptHandles.remove(task);
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, receiptHandle));
    }

    private boolean sleep() {
        try {
            Thread.sleep(pollingIntervalMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
