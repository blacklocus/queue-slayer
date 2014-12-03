package com.blacklocus.qs;

import com.google.common.base.Function;

/**
 * Interface to a function that writes records of type T to an output sink and returns
 * <code>true></code> if successful; otherwise <code>false</code>.
 */
public interface Writer<T> extends Function<Iterable<T>, Boolean> {
}
