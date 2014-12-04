package com.blacklocus.qs.aws.sqs.readers;

import com.blacklocus.qs.MessageReader;
import com.blacklocus.qs.MessageWriter;
import com.blacklocus.qs.MessageProvider;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reads messages from an SQS queue (using a MessageProvider) and pushes them to a specified string Consumer.
 *
 * To start a QueueReader call its {@link #run()} or {@link #start()} method.
 */
public class SQSMessageReader implements MessageReader<String, String> {
    private static final Logger LOG = LoggerFactory.getLogger(SQSMessageReader.class);

    private Function<Iterable<String>, Iterable<String>> transform;
    private MessageWriter<String> writer;

    private final MessageProvider messageProvider;
    private final boolean deleteOnFailure;
    private final int visibilityTimeoutSeconds;

    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Creates a MessageDriver for the specified MessageProvider and Consumer.
     *
     * @param messageProvider the {@link com.blacklocus.qs.MessageProvider}
     * @param deleteOnFailure whether or not to delete messages that the consumer fails to consume
     * @param visibilityTimeoutSeconds how long to wait before a failed message should be eligible to be processed again
     */
    public SQSMessageReader(
            MessageProvider messageProvider,
            boolean deleteOnFailure,
            int visibilityTimeoutSeconds) {
        this.messageProvider = messageProvider;
        this.deleteOnFailure = deleteOnFailure;
        this.visibilityTimeoutSeconds = visibilityTimeoutSeconds;
    }

    public void setTransform(Function<Iterable<String>, Iterable<String>> transform) {
        this.transform = transform;
    }

    public void setWriter(MessageWriter<String> writer) {
        this.writer = writer;
    }

    /**
     * Starts this driver. Same as calling the {@link #start()} method.
     */
    public void run() {
        start();
    }

    /**
     * Starts this driver, meaning that it starts pulling Messages from the MessageProvider and pushing
     * them to the Consumer.
     */
    public void start() {
        assert(transform != null);
        assert(writer != null);

        Iterator<Collection<com.blacklocus.qs.Message>> iterator = messageProvider.iterator();

        while(iterator.hasNext() && !running.get()) {
            Collection<com.blacklocus.qs.Message> messages = iterator.next();

            try {
                Collection<String> records = new ArrayList<String>();

                for (com.blacklocus.qs.Message message : messages) {
                    records.add(message.getBody());
                }

                Iterable<String> transformedRecords = transform.apply(records);

                Boolean result = writer.apply(transformedRecords);

                if (result != null && result) {
                    handleSuccess(messages);
                } else {
                    handleFailure(messages);
                }
            } catch(Throwable ex) {
                LOG.error("Failed to process a message. Skipping.", ex);
                handleFailure(messages);
            }
        }

        stop();
    }

    private void handleSuccess(Collection<com.blacklocus.qs.Message> messages) {
        for (com.blacklocus.qs.Message message : messages) {
            messageProvider.delete(message);
        }
    }

    private void handleFailure(Collection<com.blacklocus.qs.Message> messages) {
        if (deleteOnFailure) {
           handleSuccess(messages);
        } else {
            for (com.blacklocus.qs.Message message : messages) {
                messageProvider.setVisibilityTimeout(message, visibilityTimeoutSeconds);
            }
        }
    }

    /**
     * Call this method to stop this driver.
     */
    public void stop() {
        running.set(false);
    }
}
