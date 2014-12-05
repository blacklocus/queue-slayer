package com.blacklocus.qs.aws.kinesis.checkpoint.strategies;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.blacklocus.qs.aws.kinesis.CheckpointStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Kinesis check-point strategy that performs a check-point, on the current thread,
 * if a specified amount of time has expired since the lask check-point.
 */
public class ElapsedTimeCheckpointStrategy implements CheckpointStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(ElapsedTimeCheckpointStrategy.class);

    private static class Config {
        private static String PROP_CHECKPOINT_INTERVAL_MILLIS = "bl.checkpoint.interval.millis";

        private static long DEFAULT_CHECKPOINT_INTERVAL_MILLIS = 10000;

        /**
         * How often to do a Kinesis check-point.
         */
        public static Long CHECKPOINT_INTERVAL_MILLIS =
                Long.getLong(PROP_CHECKPOINT_INTERVAL_MILLIS, DEFAULT_CHECKPOINT_INTERVAL_MILLIS);
    }


    private final long checkpointIntervalMillis;
    private long lastCheckpointTimeMillies = 0L;

    public ElapsedTimeCheckpointStrategy() {
        this(Config.CHECKPOINT_INTERVAL_MILLIS);
    }

    public ElapsedTimeCheckpointStrategy(long checkpointIntervalMillis) {
        this.checkpointIntervalMillis = checkpointIntervalMillis;
    }

    public void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        if (System.currentTimeMillis() - lastCheckpointTimeMillies >= checkpointIntervalMillis) {
            checkpointImpl(checkpointer);
            lastCheckpointTimeMillies = System.currentTimeMillis();
        }
    }

    private void checkpointImpl(IRecordProcessorCheckpointer checkpointer) {
        LOG.info("Performing checkpoint ...");

        try {
            checkpointer.checkpoint();
        } catch (Exception ex) {
            LOG.error("Failed to perform a checkpoint.", ex);
        }
    }
}
