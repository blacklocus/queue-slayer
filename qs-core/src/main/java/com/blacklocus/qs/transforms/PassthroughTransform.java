package com.blacklocus.qs.transforms;

import com.google.common.base.Function;

/**
 * A transform that passes through the records it receives unchanged.
 */
public class PassthroughTransform implements Function<Iterable<String>, Iterable<String>> {
    public Iterable<String> apply(Iterable<String> records) {
        return records;
    }
}
