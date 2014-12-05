package com.blacklocus.qs;

import com.google.common.base.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of a transform that applies it's transform one record at a time.
 */
public abstract class AbstractTransform<T1, T2> implements Function<Iterable<T1>, Iterable<T2>> {
    public Iterable<T2> apply(Iterable<T1> records) {
        List<T2> transformedRecords = new ArrayList<T2>();

        for (T1 record: records) {
            transformedRecords.add(transform(record));
        }

        return transformedRecords;
    }

    protected abstract T2 transform(T1 record);
}
