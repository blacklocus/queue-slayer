package com.blacklocus.qs.aws.sqs.readers;

import com.blacklocus.qs.MessageReader;
import com.blacklocus.qs.MessageReaderFactory;

/**
 * A factory that creates and configures an SQS {@link SQSMessageReader}.
 */
public class SQSMessageReaderFactory implements MessageReaderFactory<String, String> {
    @SuppressWarnings("unchecked")
    public MessageReader<String, String> createReader() {
        return new SQSMessageReader(
                SQSMessageReaderConfig.MESSAGE_PROVIDER,
                SQSMessageReaderConfig.DELETE_MESSAGE_ON_FAILURE,
                SQSMessageReaderConfig.MESSAGE_VISIBILITY_TIMEOUT_SECONDS
        );
    }
}
