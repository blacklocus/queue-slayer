package com.blacklocus.qs.aws.sqs.writers;

import com.blacklocus.qs.Writer;
import com.blacklocus.qs.WriterFactory;
import com.blacklocus.qs.aws.sqs.config.SQSAsyncConfig;

/**
 * A factory for SQS {@link com.blacklocus.qs.aws.sqs.writers.QueueWriter}s.
 */
public class QueueWriterFactory implements WriterFactory<String> {
    public Writer<String> createWriter() {
        return new QueueWriter(SQSAsyncConfig.SQS_ASYNC_CLIENT, QueueWriterConfig.OUTPUT_QUEUE_URL);
    }
}
