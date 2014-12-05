package com.blacklocus.qs.aws.kinesis.writers;

import com.blacklocus.qs.MessageWriter;
import com.blacklocus.qs.MessageWriterFactory;
import com.blacklocus.qs.aws.kinesis.config.KinesisAsyncConfig;

/**
 * A factory that creates and configures a {@link KinesisMessageWriter}.
 * The configuration data is obtained from the {@link KinesisMessageWriterConfig}
 * class.
 */
public class KinesisMessageWriterFactory implements MessageWriterFactory<String> {
    public MessageWriter<String> createWriter() {
        return new KinesisMessageWriter(KinesisAsyncConfig.KINESIS_ASYNC_CLIENT, KinesisMessageWriterConfig.OUTPUT_STREAM_NAME);
    }
}
