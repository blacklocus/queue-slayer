package com.blacklocus.qs.aws.kinesis.readers;

import com.blacklocus.qs.aws.config.AWSConfig;
import com.blacklocus.qs.Reader;
import com.blacklocus.qs.ReaderFactory;

/**
 * A factory that creates and configures a Kinesis {@link com.blacklocus.qs.aws.kinesis.readers.StreamReader}.
 * The factory obtains the configuration data from the {@link com.blacklocus.qs.aws.kinesis.readers.StreamReaderConfig}
 * class.
 */
public class StreamReaderFactory implements ReaderFactory<String, String> {
    @SuppressWarnings("unchecked")
    public Reader<String, String> createReader() {
        return new StreamReader(
                AWSConfig.AWS_CREDETIALS_PROVIDER,
                StreamReaderConfig.APP_NAME,
                StreamReaderConfig.INPUT_STREAM_NAME,
                StreamReaderConfig.INITIAL_POSITION,
                StreamReaderConfig.CHECKPOINT_STRATEGY
        );
    }
}
