package com.blacklocus.qs;

import com.google.common.base.Function;

/**
 * Interface to a factory that produces transforms, which are functions that transform zero or more
 * records of type T1 into zero or more records of type T2.
 */
public interface TransformFactory<T1, T2> {
    public Function<Iterable<T1>, Iterable<T2>> createTransform();
}
