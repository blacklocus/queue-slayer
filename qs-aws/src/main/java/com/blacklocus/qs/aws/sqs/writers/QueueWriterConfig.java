package com.blacklocus.qs.aws.sqs.writers;

import com.blacklocus.config.SystemPropertyConfig;
import com.blacklocus.qs.aws.sqs.config.SQSAsyncConfig;

/**
 * Config info for an SQS {@link com.blacklocus.qs.aws.sqs.writers.QueueWriter}.
 */
public class QueueWriterConfig {
    private static final String PROP_OUTPUT_QUEUE_NAME = "bl.output.queue.name";

    public static final String OUTPUT_QUEUE_NAME =
            SystemPropertyConfig.required(PROP_OUTPUT_QUEUE_NAME);

    public static final String OUTPUT_QUEUE_URL =
            SQSAsyncConfig.SQS_ASYNC_CLIENT.getQueueUrl(OUTPUT_QUEUE_NAME).getQueueUrl();
}
