package com.blacklocus.qs.aws.sqs.readers;

import com.blacklocus.qs.Reader;
import com.blacklocus.qs.ReaderFactory;

/**
 * A factory that creates and configures an SQS {@link com.blacklocus.qs.aws.sqs.readers.QueueReader}.
 */
public class QueueReaderFactory implements ReaderFactory<String, String> {
    @SuppressWarnings("unchecked")
    public Reader<String, String> createReader() {
        return new QueueReader(
                QueueReaderConfig.MESSAGE_PROVIDER,
                QueueReaderConfig.DELETE_MESSAGE_ON_FAILURE,
                QueueReaderConfig.MESSAGE_VISIBILITY_TIMEOUT_SECONDS
        );
    }
}
