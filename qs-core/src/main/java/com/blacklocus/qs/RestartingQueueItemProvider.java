package com.blacklocus.qs;

import com.google.common.base.Supplier;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

// TODO migrate to queue-slayer project

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class RestartingQueueItemProvider<Q> implements QueueItemProvider<Q> {

    private final long restartDelayMs;
    private final Supplier<QueueItemProvider<Q>> quipSupplier;
    private QueueItemProvider<Q> currentProvider;

    /**
     * @param restartDelayMs When a received QueueItemProvider ceases producing elements, how long the next call to
     *                       {@link #next()} will block in milliseconds before grabbing a new one from the supplier.
     * @param quipSupplier   that returns a working QueueItemProvider for each {@link Supplier#get()}.
     */
    public RestartingQueueItemProvider(long restartDelayMs, Supplier<QueueItemProvider<Q>> quipSupplier) {
        this.restartDelayMs = restartDelayMs;
        this.quipSupplier = quipSupplier;
        this.currentProvider = quipSupplier.get();
    }

    @Override
    public Iterator<Collection<Q>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Collection<Q> next() {
        if (currentProvider == null) {
            try {
                Thread.sleep(restartDelayMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            currentProvider = quipSupplier.get();
        }

        if (currentProvider.hasNext()) {
            return currentProvider.next();
        } else {
            currentProvider = null;
            return Collections.emptyList();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
