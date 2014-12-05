package com.blacklocus.qs.aws.kinesis.readers;

import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.blacklocus.config.SystemPropertyConfig;
import com.blacklocus.qs.aws.kinesis.CheckpointStrategy;

/**
 * Configuration for a Kinesis {@link KinesisMessageReader}.
 */
public class KinesisMessageReaderConfig {
    private static final String PROP_APP_NAME = "bl.app.name";
    private static final String PROP_INPUT_STREAM_NAME = "bl.input.stream.name";
    private static final String PROP_INITIAL_POSITION = "bl.initial.position";
    private static final String PROP_CHECKPOINT_STRATEGY_CLASS_NAME = "bl.checkpoint.strategy.class.name";

    private static final String DEFAULT_INITIAL_POSITION = "LATEST";

    private static final String DEFAULT_CHECKPOINT_STRATEGY_CLASS_NAME = "com.blacklocus.qs.aws.kinesis.checkpoint.strategies.ElapsedTimeCheckpointStrategy";

    public static final String APP_NAME = SystemPropertyConfig.required(PROP_APP_NAME);

    public static final String INPUT_STREAM_NAME =  SystemPropertyConfig.required(PROP_INPUT_STREAM_NAME);

    private static final String INITIAL_POSITION_STR =
            SystemPropertyConfig.optional(PROP_INITIAL_POSITION, DEFAULT_INITIAL_POSITION);

    public static final InitialPositionInStream INITIAL_POSITION =
            InitialPositionInStream.valueOf(INITIAL_POSITION_STR);

    public static final CheckpointStrategy CHECKPOINT_STRATEGY =
            (CheckpointStrategy) SystemPropertyConfig.createObjectFromKey(
                    PROP_CHECKPOINT_STRATEGY_CLASS_NAME,
                    DEFAULT_CHECKPOINT_STRATEGY_CLASS_NAME);
}
