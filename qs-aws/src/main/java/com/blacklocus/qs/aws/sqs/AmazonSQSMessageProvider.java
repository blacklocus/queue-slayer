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
package com.blacklocus.qs.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.blacklocus.qs.Message;
import com.blacklocus.qs.MessageProvider;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Provide Message instances from an SQS queue.
 */
public class AmazonSQSMessageProvider implements MessageProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSMessageProvider.class);

    private AmazonSQS sqs;
    private String queueUrl;
    private int waitTimeSeconds;

    /**
     * Create a new MessageProvider instance.
     *
     * @param sqs             AmazonSQS instance to use for communicating with AWS
     * @param queueUrl        target queue to pull Message instances from
     * @param waitTimeSeconds to pass along to {@link ReceiveMessageRequest#withWaitTimeSeconds(Integer)}
     */
    public AmazonSQSMessageProvider(AmazonSQS sqs, String queueUrl, int waitTimeSeconds) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
        this.waitTimeSeconds = waitTimeSeconds;
    }

    /**
     * Returns the queue url for this provider.
     *
     * @return the queue url for this provider
     */
    public String getQueueUrl() {
        return queueUrl;
    }

    @Override
    public List<Message> next() {
        try {
            // receive messages from SQS
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                    .withAttributeNames("SentTimestamp", "ApproximateReceiveCount")
                    .withMaxNumberOfMessages(10)
                    .withWaitTimeSeconds(waitTimeSeconds);
            List<com.amazonaws.services.sqs.model.Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            return Lists.transform(sqsMessages, new Function<com.amazonaws.services.sqs.model.Message, Message>() {
                @Override
                public Message apply(com.amazonaws.services.sqs.model.Message input) {
                    return input != null ? new AmazonSQSMessage(input) : null;
                }
            });
        } catch (Throwable t) {
            LOG.error("An error occurred while receiving an SQS message: {}", t);
            // sleep to avoid busy wait loop on a receive error
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.warn("Thread interrupted while sleeping: {}", e);
                Thread.currentThread().interrupt();
            }
            return Collections.emptyList();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported");
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Iterator<Collection<Message>> iterator() {
        return this;
    }

    @Override
    public void delete(Message message) {
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, message.getReceipt()));
    }

    @Override
    public void setVisibilityTimeout(Message message, Integer visibilityTimeoutSeconds) {
        sqs.changeMessageVisibility(new ChangeMessageVisibilityRequest(queueUrl, message.getReceipt(), visibilityTimeoutSeconds));
    }
}
