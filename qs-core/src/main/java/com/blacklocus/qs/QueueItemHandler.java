package com.blacklocus.qs;


import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.Future;

/**
 * More generalized form of {@link MessageHandler}
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public interface QueueItemHandler<Q, T, R> {
    /**
     * Convert the given queue item on the queue item processing thread.
     *
     * @param queueItem a queue item to convert
     */
    T convert(Q queueItem) throws Exception;

    /**
     * Return the result of processing a queue item of the given converted type on
     * the queue item processing thread.
     *
     * @param convertedQueueItem the converted target queue item
     */
    R process(T convertedQueueItem) throws Exception;

    /**
     * Called when processing completes successfully on the queue item processing
     * thread.
     *
     * @param queueItem the original queue item that was successfully processed
     * @param result    the result returned from successful queue item handling
     */
    void onSuccess(Q queueItem, R result);

    /**
     * Called when an Exception occurs while processing the given queue item on
     * the queue item processing thread.
     *
     * @param queueItem the queue item received
     * @param throwable the error that occurred while trying to process the queue item
     */
    void onError(Q queueItem, Throwable throwable);

    /**
     * Called whether the processing results in success or failure on the
     * queue item processing thread.
     *
     * @param queueItem the original queue item being handled
     */
    void onComplete(Q queueItem);

    /**
     * Called after the queue item is retrieved off of the queue and handed off to
     * an Executor for processing.  This method will be called on the queue
     * reading thread so if it blocks then it will block all subsequent queue
     * reading. Thus, use caution when your implementation of process() might
     * block for a while.
     *
     * @param queueItem the queue item to be processed
     * @param future    the Future created after submitting the handling task to the executor
     */
    void withFuture(Q queueItem, Future<Pair<Q, R>> future);
}
