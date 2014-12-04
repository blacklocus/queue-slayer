package com.blacklocus.qs.aws.kinesis.writers;

import com.blacklocus.config.SystemPropertyConfig;

/**
 * Configuration info for a Kinesis {@link KinesisMessageWriter}.
 */
public class KinesisMessageWriterConfig {
    private static final String PROP_OUTPUT_STREAM_NAME = "bl.output.stream.name";

    public static final String OUTPUT_STREAM_NAME = SystemPropertyConfig.required(PROP_OUTPUT_STREAM_NAME);
}
