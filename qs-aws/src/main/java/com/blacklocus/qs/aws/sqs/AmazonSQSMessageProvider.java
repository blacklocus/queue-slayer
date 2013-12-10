package com.blacklocus.qs.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
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

    /**
     * Create a new MessageProvider instance.
     *
     * @param sqs      AmazonSQS instance to use for communicating with AWS
     * @param queueUrl target queue to pull Message instances from
     */
    public AmazonSQSMessageProvider(AmazonSQS sqs, String queueUrl) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
    }

    @Override
    public List<Message> next() {
        try {
            // receive messages from SQS
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                    .withAttributeNames("SentTimestamp")
                    .withMaxNumberOfMessages(10);
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
}
