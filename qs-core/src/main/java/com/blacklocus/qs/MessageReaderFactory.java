package com.blacklocus.qs;

/**
 * Inerface to a factory that creates and configures a {@link MessageReader}.
 */
public interface MessageReaderFactory<T1, T2> {
    public MessageReader<T1, T2> createReader();
}
