package com.blacklocus.qs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Decorator around any {@link QueueItemProvider} that batches entries together.
 */
public class BatchingQuIP<T> implements QueueItemProvider<List<T>> {

    final Iterator<T> items;
    final Integer batchSize;

    /**
     * @param items     provider
     * @param batchSize (optional) size of each aggregated batch returned by {@link #next()}. Defaults to 1000.
     */
    public BatchingQuIP(QueueItemProvider<T> items, Integer batchSize) {
        this.items = Iterables.concat(items).iterator();
        this.batchSize = batchSize == null ? 1000 : batchSize;
    }

    @Override
    public Iterator<Collection<List<T>>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return items.hasNext();
    }

    @Override
    public Collection<List<T>> next() {
        List<T> batch = new ArrayList<T>(batchSize);
        while (items.hasNext() && batch.size() < batchSize) {
            batch.add(items.next());
        }
        return ImmutableList.of(batch);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
