package com.blacklocus.qs.aws.kinesis.readers;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason;
import com.amazonaws.services.kinesis.model.Record;
import com.blacklocus.qs.MessageReader;
import com.blacklocus.qs.MessageWriter;
import com.blacklocus.qs.aws.kinesis.CheckpointStrategy;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A {@link KinesisMessageReader} feeds records from a Kinesis stream to one or more consumers.
 * The class creates the consumers on demand using the specified WriterFactory.
 *
 * Once created, a reader must be started by calling the {@link #start()} method (or optionally the {@link #run()} method).
 * The {@link #start()} will run indefinitely on the thread that called it;
 * therefore, if your application has other processing to do,
 * such as reading from other Kinesis streams, you should start the reader on its own dedicated thread.
 * To stop a reader, call its {@link #stop()} method. Readers that have been stopped can not be restarted.
 */
public class KinesisMessageReader implements MessageReader<String, String> {
    private static final Logger LOG = LoggerFactory.getLogger(KinesisMessageReader.class);

    /**
     * The {@link com.google.common.base.Function} used to transform records before passing them to this reader's
     * {@link com.blacklocus.qs.MessageWriter}.
     */
    private Function<Iterable<String>, Iterable<String>> transform;

    /**
     * The {@link com.blacklocus.qs.MessageWriter} used by this {@link com.blacklocus.qs.MessageReader} to write transformed
     * records.
     */
    private MessageWriter<String> writer;

    /**
     * The Kinesis Worker. It creates and destroys RecordProcessors as needed to service the
     * records flowing on a Kinesis stream's shards.
     */
    private final Worker worker;

    /**
     * A Kinesis record processor that converts the data contained in a Kinesis
     * {@link com.amazonaws.services.kinesis.model.Record} to a String and pushes
     * the String to the specified Consumer.
     */
    private class RecordProcessor implements IRecordProcessor {
        private final CheckpointStrategy checkpointStrategy;

        public RecordProcessor(CheckpointStrategy checkpointStrategy) {
            this.checkpointStrategy = checkpointStrategy;
        }

        public void initialize(String shardId) {
            LOG.info("Initializing a record processor for shard '%s'.", shardId);
        }

        public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {
            try {
                List<String> messages = new ArrayList<String>();

                for (Record record : records) {
                    messages.add(new String(record.getData().array()));
                }

                Boolean result = writer.apply(transform.apply(messages));

                if (result == null || !result) {
                    LOG.error("Failed to process a record! Skipping!");
                }
            } catch (Exception ex) {
                LOG.error("Failed to process a record! Skipping!", ex);
            }

            // delegate check-pointing to the checkpoint strategy
            checkpointStrategy.checkpoint(checkpointer);
        }

        public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
            LOG.info("Shutting down a record processor. Reason '%s'.", reason.toString());

            if (reason == ShutdownReason.TERMINATE) {
                // delegate check-pointing to the checkpoint strategy
                checkpointStrategy.checkpoint(checkpointer);
            }
        }
    }

    /**
     * A Kinesis record processor factory called by the Kinesis Worker to create {@link RecordProcessor}s as needed.
     */
    private class RecordProcessorFactory implements IRecordProcessorFactory {
        private final CheckpointStrategy checkpointStrategy;

        public RecordProcessorFactory(
                CheckpointStrategy checkpointStrategy) {
            this.checkpointStrategy = checkpointStrategy;
        }

        public IRecordProcessor createProcessor() {
            return new RecordProcessor(checkpointStrategy);
        }
    }

    /**
     * Creates a Kinesis {@link KinesisMessageReader} to process a specified Kinesis stream.
     *
     * @param credentialsProvider an {@link com.amazonaws.auth.AWSCredentialsProvider} containing the
     *                            credentials required to access the specified Kinesis stream.
     * @param appName the name of the Kinesis application
     * @param streamName the name of the Kinesis stream
     * @param initialPosition where to start reading from the stream
     * @param checkpointStrategy the strategy used to perform Kinesis check-points
     */
    public KinesisMessageReader(
            AWSCredentialsProvider credentialsProvider,
            String appName,
            String streamName,
            InitialPositionInStream initialPosition,
            CheckpointStrategy checkpointStrategy) {
        try {
            IRecordProcessorFactory recordProcessorFactory =
                    new RecordProcessorFactory(checkpointStrategy);

            String workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + UUID.randomUUID();

            KinesisClientLibConfiguration config = new KinesisClientLibConfiguration(
                    appName,
                    streamName,
                    credentialsProvider,
                    workerId
            ).withInitialPositionInStream(initialPosition);

            this.worker = new Worker(recordProcessorFactory, config);
        } catch (Exception ex) {
            String msg = "Failed to create a Kinesis worker.";
            LOG.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    public void setTransform(Function<Iterable<String>, Iterable<String>> transform) {
        this.transform = transform;
    }

    public void setWriter(MessageWriter<String> writer) {
        this.writer = writer;
    }

    public void run() {
        start();
    }

    public void start() {
        assert(transform != null);
        assert(writer != null);

        try {
            worker.run();
        } catch (Exception ex) {
            LOG.error("Failed to run a Kinesis worker.", ex);
        }
    }

    public void stop() {
        worker.shutdown();
    }
}
