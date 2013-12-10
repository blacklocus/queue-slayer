package com.blacklocus.qs;

import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.Future;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public abstract class AbstractQueueItemHandler<Q, T, R> implements QueueItemHandler<Q, T, R> {

    @Override
    public void onSuccess(Q queueItem, R result) {
        // do nothing
    }

    @Override
    public void onError(Q queueItem, Throwable throwable) {
        // do nothing
    }

    @Override
    public void onComplete(Q queueItem) {
        // do nothing
    }

    @Override
    public void withFuture(Q queueItem, Future<Pair<Q, R>> future) {
        // do nothing
    }
}
