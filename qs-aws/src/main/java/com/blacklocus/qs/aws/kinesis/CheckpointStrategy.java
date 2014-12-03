package com.blacklocus.qs.aws.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;

/**
 * Interface to a Kinesis check-point strategy that's responsible for deciding when and how to perform Kinesis
 * check-points.
 */
public interface CheckpointStrategy {
    /**
     * Call this method to perform a check-point. It typically should be called each time an
     * {@link com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor} implementation processes
     * a set of Kinesis records.
     *
     * Implementations of this method can choose to differ the check-point operation.
     *
     * @param checkpointer a Kinesis IRecordProcessorCheckpointer.
     */
    public void checkpoint(IRecordProcessorCheckpointer checkpointer);
}
