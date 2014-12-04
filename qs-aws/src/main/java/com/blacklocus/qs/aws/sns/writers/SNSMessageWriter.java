package com.blacklocus.qs.aws.sns.writers;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.PublishRequest;
import com.blacklocus.qs.AbstractMessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link com.blacklocus.qs.MessageWriter} that writes messages to an SNS topic.
 */
public class SNSMessageWriter extends AbstractMessageWriter<String> {
    private static final Logger LOG = LoggerFactory.getLogger(SNSMessageWriter.class);

    private final AmazonSNSAsync sns;
    private final String topicArn;

    /**
     * Creates an AmazonSQSWriter for the specified queue.
     *
     * @param sns the AmazonSNSAsync client.
     * @param topicArn the SNS topic's ARN
     */
    public SNSMessageWriter(AmazonSNSAsync sns, String topicArn) {
        this.sns = sns;
        this.topicArn = topicArn;
    }

    /**
     * Write the specified message to this writer's SNS topic. Returns true if the message was
     * successfully sent.
     *
     * @param message the message to send
     */
    protected boolean write(String message) {
        try {
            System.out.println("Writing '" + message + "' to " + topicArn);

            PublishRequest publishRequest = new PublishRequest(topicArn, message);

            sns.publishAsync(publishRequest);

            return true;
        } catch (Exception ex) {
            LOG.error(String.format("Failed to publish a message to the '%s' SNS topic.", topicArn), ex);

            return false;
        }
    }
}
