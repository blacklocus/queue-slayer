package com.blacklocus.qs.aws.sns.writers;

import com.blacklocus.config.SystemPropertyConfig;

/**
 * author: steve
 */
public class TopicWriterConfig {
    private static final String PROP_OUTPUT_TOPIC_ARN = "bl.output.topic.arn";

    private static final String DEFAULT_OUTPUT_TOPIC_ARN = "arn:aws:sns:us-east-1:493847008801:TestTopic1";

    public static final String OUTPUT_TOPIC_ARN =
            SystemPropertyConfig.optional(PROP_OUTPUT_TOPIC_ARN, DEFAULT_OUTPUT_TOPIC_ARN);
}
