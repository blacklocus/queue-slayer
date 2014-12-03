package com.blacklocus.qs.aws.sns.writers;

import com.blacklocus.qs.Writer;
import com.blacklocus.qs.WriterFactory;
import com.blacklocus.qs.aws.sns.config.SNSAsyncConfig;

/**
 * A factory for SNS {@link com.blacklocus.qs.aws.sns.writers.TopicWriter}s.
 */
public class TopicWriterFactory implements WriterFactory<String> {
    public Writer<String> createWriter() {
        return new TopicWriter(SNSAsyncConfig.SNS_ASYNC_CLIENT, TopicWriterConfig.OUTPUT_TOPIC_ARN);
    }
}
