package com.blacklocus.qs.aws.sqs;

import com.blacklocus.qs.Message;

import java.util.Map;

/**
 * This is an implementation of a Message backed by an Amazon SQS Message.
 */
public class AmazonSQSMessage implements Message {

    private com.amazonaws.services.sqs.model.Message message;

    public AmazonSQSMessage(com.amazonaws.services.sqs.model.Message message) {
        this.message = message;
    }

    @Override
    public String getId() {
        return message.getMessageId();
    }

    @Override
    public String getReceipt() {
        return message.getReceiptHandle();
    }

    @Override
    public String getBody() {
        return message.getBody();
    }

    @Override
    public Map<String, String> getAttributes() {
        return message.getAttributes();
    }

    @Override
    public String toString() {
        return message.getBody();
    }
}
