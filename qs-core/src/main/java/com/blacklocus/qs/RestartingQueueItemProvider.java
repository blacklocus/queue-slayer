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

import com.google.common.base.Supplier;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

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
