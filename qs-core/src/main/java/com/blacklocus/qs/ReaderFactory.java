package com.blacklocus.qs;

/**
 * Inerface to a factory that creates and configures a {@link com.blacklocus.qs.Reader}.
 */
public interface ReaderFactory<T1, T2> {
    public Reader<T1, T2> createReader();
}
