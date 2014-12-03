package com.blacklocus.qs;

/**
 * Interface to an object that can create and configure a {@link com.blacklocus.qs.Writer}.
 */
public interface WriterFactory<T> {
    public Writer<T> createWriter();
}
