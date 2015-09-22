/**
 * Copyright 2013 BlackLocus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blacklocus.qs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class ExternalQueueItemProvider<Q> implements QueueItemProvider<Q>, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(com.blacklocus.qs.ExternalQueueItemProvider.class);

    private final BlockingQueue<Q> q;

    private final AtomicBoolean alive = new AtomicBoolean(true);

    private final Long pollTimeout;
    private final TimeUnit pollTimeUnit;

    public ExternalQueueItemProvider() {
        this(null, null);
    }

    public ExternalQueueItemProvider(Long pollTimeout, TimeUnit pollTimeUnit) {
        this(new SynchronousQueue<Q>(true), pollTimeout, pollTimeUnit);
    }

    public ExternalQueueItemProvider(BlockingQueue<Q> queue, Long pollTimeout, TimeUnit pollTimeUnit) {
        this.q = queue;
        this.pollTimeout = pollTimeout;
        this.pollTimeUnit = pollTimeUnit;
    }

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
        Q q = null;
        try {
            q = pollTimeout == null ? this.q.poll() : this.q.poll(pollTimeout, pollTimeUnit);
        } catch (InterruptedException e) {
            LOG.info("Provider interrupted. Closing immediately.", e);
            try {
                close();
            } catch (IOException e1) {
                throw new RuntimeException(e);
            }
        }
        return q == null ? Collections.<Q>emptyList() : Collections.singletonList(q); // meh, no batching
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
