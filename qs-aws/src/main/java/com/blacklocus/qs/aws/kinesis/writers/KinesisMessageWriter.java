package com.blacklocus.qs.aws.kinesis.writers;

import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.blacklocus.qs.AbstractMessageWriter;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * A {@link com.blacklocus.qs.MessageWriter} that writes messages to a Kinesis stream.
 * See {@link ExampleKinesisMessageWriterApp} for an example application that uses a
 * KinesisStreamWriter.
 *
 * This class uses a function to convert a message to a Kinesis partition key (a string).
 * That function can typically be a {@link MessageToRandomPartitionKey} that generates
 * random Kinesis partition keys independent of the actual message.
 */
public class KinesisMessageWriter extends AbstractMessageWriter<String> {
    private static final Logger LOG = LoggerFactory.getLogger(KinesisMessageWriter.class);

    private final AmazonKinesisAsync kinesis;
    private final String streamName;
    private final Function<String, String> messageToPartitionKey;

    /**
     * Creates an AmazonKinesisWriter for the specified Kinesis stream.
     *
     * @param kinesis the AmazonKinesis client.
     * @param streamName the name of the target Kinesis stream
     */
    public KinesisMessageWriter(
            AmazonKinesisAsync kinesis,
            String streamName) {
        this(kinesis, streamName, new MessageToRandomPartitionKey<String>());
    }

    /**
     * Creates an AmazonKinesisWriter for the specified Kinesis stream.
     *
     * @param kinesis the AmazonKinesis client.
     * @param streamName the name of the target Kinesis stream
     * @param messageToPartitionKey a function that takes a message and returns a partition key
     */
    public KinesisMessageWriter(
            AmazonKinesisAsync kinesis,
            String streamName,
            Function<String, String> messageToPartitionKey) {
        this.kinesis = kinesis;
        this.streamName = streamName;
        this.messageToPartitionKey = messageToPartitionKey;
    }

    /**
     * Consumes the specified message by sending it to this writer's Kinesis stream. Returns true if the message was
     * successfully sent.
     *
     * @param message the message to send
     *
     * TODO: Change this to stuff as many messages into a record as possible (up to 50Kb) by implementing the apply(Iterable)
     *                method directly, perhaps using a json array to make it easy for the StreamReader to parse.
     */
    public boolean write(String message) {
        try {
            System.out.println("Writing '" + message + "' to " + streamName);

            PutRecordRequest request = new PutRecordRequest()
                    .withStreamName(streamName)
                    .withPartitionKey(messageToPartitionKey.apply(message))
                    .withData(ByteBuffer.wrap(message.getBytes("UTF-8")));

            kinesis.putRecordAsync(request);

            return true;
        } catch (Exception ex) {
            LOG.error(String.format("Failed to put a record on the '%s' Kinesis stream.", streamName), ex);
            return false;
        }
    }
}
