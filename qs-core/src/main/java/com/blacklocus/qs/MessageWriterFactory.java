package com.blacklocus.qs;

/**
 * Interface to an object that can create and configure a {@link MessageWriter}.
 */
public interface MessageWriterFactory<T> {
    public MessageWriter<T> createWriter();
}
