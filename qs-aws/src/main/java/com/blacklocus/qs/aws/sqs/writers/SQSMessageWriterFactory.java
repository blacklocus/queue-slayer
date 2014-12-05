package com.blacklocus.qs.aws.sqs.writers;

import com.blacklocus.qs.MessageWriter;
import com.blacklocus.qs.MessageWriterFactory;
import com.blacklocus.qs.aws.sqs.config.SQSAsyncConfig;

/**
 * A factory that creates and configures {@link SQSMessageWriter}s.
 */
public class SQSMessageWriterFactory implements MessageWriterFactory<String> {
    public MessageWriter<String> createWriter() {
        return new SQSMessageWriter(SQSAsyncConfig.SQS_ASYNC_CLIENT, SQSMessageWriterConfig.OUTPUT_QUEUE_URL);
    }
}
