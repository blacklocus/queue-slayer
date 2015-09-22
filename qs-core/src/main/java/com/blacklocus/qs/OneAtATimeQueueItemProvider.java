package com.blacklocus.qs;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A simple {@link QueueItemProvider} which accepts any iterator and provides messages one at a time to a
 * {@link QueueReader}.
 *
 * @param <Q> type of queue item provided
 */
public class OneAtATimeQueueItemProvider<Q> implements QueueItemProvider<Q> {

    final Iterator<Collection<Q>> items;

    public OneAtATimeQueueItemProvider(Iterator<Q> items) {
        this.items = Iterators.transform(items, new Function<Q, Collection<Q>>() {
            @Override
            public Collection<Q> apply(Q input) {
                return Collections.singleton(input);
            }
        });
    }

    @Override
    public Iterator<Collection<Q>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return items.hasNext();
    }

    @Override
    public Collection<Q> next() {
        return items.next();
    }

    @Override
    public void remove() {
        items.remove();
    }
}
