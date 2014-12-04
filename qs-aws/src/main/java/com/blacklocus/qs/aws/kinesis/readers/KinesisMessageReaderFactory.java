package com.blacklocus.qs.aws.kinesis.readers;

import com.blacklocus.qs.MessageReader;
import com.blacklocus.qs.MessageReaderFactory;
import com.blacklocus.qs.aws.config.AWSConfig;

/**
 * A factory that creates and configures a Kinesis {@link KinesisMessageReader}.
 * The factory obtains the configuration data from the {@link KinesisMessageReaderConfig}
 * class.
 */
public class KinesisMessageReaderFactory implements MessageReaderFactory<String, String> {
    @SuppressWarnings("unchecked")
    public MessageReader<String, String> createReader() {
        return new KinesisMessageReader(
                AWSConfig.AWS_CREDENTIALS_PROVIDER,
                KinesisMessageReaderConfig.APP_NAME,
                KinesisMessageReaderConfig.INPUT_STREAM_NAME,
                KinesisMessageReaderConfig.INITIAL_POSITION,
                KinesisMessageReaderConfig.CHECKPOINT_STRATEGY
        );
    }
}
