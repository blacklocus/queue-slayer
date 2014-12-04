package com.blacklocus.qs.aws.sns.writers;

import com.blacklocus.qs.MessageWriter;
import com.blacklocus.qs.MessageWriterFactory;
import com.blacklocus.qs.aws.sns.config.SNSAsyncConfig;

/**
 * A factory for SNS {@link SNSMessageWriter}s.
 */
public class SNSMessageWriterFactory implements MessageWriterFactory<String> {
    public MessageWriter<String> createWriter() {
        return new SNSMessageWriter(SNSAsyncConfig.SNS_ASYNC_CLIENT, SNSMessageWriterConfig.OUTPUT_TOPIC_ARN);
    }
}
