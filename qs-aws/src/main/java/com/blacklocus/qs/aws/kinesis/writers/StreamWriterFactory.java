package com.blacklocus.qs.aws.kinesis.writers;

import com.blacklocus.qs.Writer;
import com.blacklocus.qs.WriterFactory;
import com.blacklocus.qs.aws.kinesis.config.KinesisAsyncConfig;

/**
 * A factory that creates and configures a Kinesis {@link com.blacklocus.qs.aws.kinesis.writers.StreamWriter}s.
 * The configuration data is obtained from the {@link com.blacklocus.qs.aws.kinesis.writers.StreamWriterConfig}
 * class.
 */
public class StreamWriterFactory implements WriterFactory<String> {
    public Writer<String> createWriter() {
        return new StreamWriter(KinesisAsyncConfig.KINESIS_ASYNC_CLIENT, StreamWriterConfig.OUTPUT_STREAM_NAME);
    }
}
