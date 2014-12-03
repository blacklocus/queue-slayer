package com.blacklocus.qs.aws.sqs.writers;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.blacklocus.qs.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link com.blacklocus.qs.Writer} that writes string messages to an SQS queue.
 */
public class QueueWriter implements Writer<String> {
    private static final Logger LOG = LoggerFactory.getLogger(QueueWriter.class);

    private final AmazonSQSAsync sqs;
    private final String queueUrl;

    /**
     * Creates an AmazonSQSWriter for the specified queue.
     *
     * @param sqs the AmazonSQS client.
     * @param queueUrl the SQS queue's URL
     */
    public QueueWriter(
            AmazonSQSAsync sqs,
            String queueUrl) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
    }

    /**
     * Consumes the specified messages by sending them to this writer's SQS queue. Returns true if the messages were
     * successfully sent.
     *
     * @param messages the messages to send
     */
    public Boolean apply(Iterable<String> messages) {
        try {
            List<SendMessageBatchRequestEntry> entries = new ArrayList<SendMessageBatchRequestEntry>();

            int id = 0;

            for (String message : messages) {
                SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry(Integer.toString(id), message);
                entries.add(entry);
                id++;
            }

            SendMessageBatchRequest request = new SendMessageBatchRequest(queueUrl, entries);

            sqs.sendMessageBatchAsync(request);

            return true;
        } catch (Exception ex) {
            LOG.error(String.format("Failed to send a message to the '%s' SQS queue.", queueUrl), ex);

            return false;
        }
    }
}
