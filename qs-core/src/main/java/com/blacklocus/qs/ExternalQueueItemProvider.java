package com.blacklocus.qs;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class ExternalQueueItemProvider<Q> implements QueueItemProvider<Q>, Closeable {

    private final SynchronousQueue<Q> q = new SynchronousQueue<Q>(true);
    private final List<Q> empty = Collections.emptyList();

    private final AtomicBoolean alive = new AtomicBoolean(true);

    @Override
    public Iterator<Collection<Q>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return alive.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Q> next() {
        Q q = this.q.poll();
        return q == null ? empty : Arrays.asList(q); // meh, no batching
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void put(Q q) throws InterruptedException {
        this.q.put(q);
    }

    @Override
    public void close() throws IOException {
        alive.set(false);
    }
}
