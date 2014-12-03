package com.blacklocus.qs.aws.sqs.readers;

import com.blacklocus.config.SystemPropertyConfig;
import com.blacklocus.qs.MessageProvider;
import com.blacklocus.qs.aws.sqs.AmazonSQSMessageProvider;
import com.blacklocus.qs.aws.sqs.config.SQSConfig;

/**
 * Configuration for an SQS {@link com.blacklocus.qs.aws.sqs.readers.QueueReader}.
 */
public class QueueReaderConfig {
    private static final String PROP_INPUT_QUEUE_URL = "bl.input.queue.url";
    private static final String PROP_DELETE_MESSAGE_ON_FAILURE = "bl.delete.message.on.failure";
    private static final String PROP_MESSAGE_VISIBILITY_TIMEOUT_SECONDS = "bl.message.visibility.timeout.seconds";

    private static final int DEFAULT_MESSAGE_VISIBILITY_TIMEOUT_SECONDS = 30;

    private static final String INPUT_QUEUE_URL = SystemPropertyConfig.required(PROP_INPUT_QUEUE_URL);

    public static final MessageProvider MESSAGE_PROVIDER =
            new AmazonSQSMessageProvider(SQSConfig.SQS_CLIENT, INPUT_QUEUE_URL);

    public static final boolean DELETE_MESSAGE_ON_FAILURE = Boolean.getBoolean(PROP_DELETE_MESSAGE_ON_FAILURE);

    public static final int MESSAGE_VISIBILITY_TIMEOUT_SECONDS =
            Integer.getInteger(PROP_MESSAGE_VISIBILITY_TIMEOUT_SECONDS, DEFAULT_MESSAGE_VISIBILITY_TIMEOUT_SECONDS);
}
