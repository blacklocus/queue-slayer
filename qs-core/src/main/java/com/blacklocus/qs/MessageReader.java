package com.blacklocus.qs;

import com.google.common.base.Function;

/**
 * Interface to an object that reads records from a source, transforms them, and sends the transformed records to a
 * {@link MessageWriter} for further processing.
 */
public interface MessageReader<T1, T2> extends Runnable {
    public void setTransform(Function<Iterable<T1>, Iterable<T2>> transform);
    /**
     * Sets this Reader's {@link MessageWriter}. This Reader will send transformed records
     * to the {@link MessageWriter}.
     */
    public void setWriter(MessageWriter<T2> writer);

    /**
     * Called to start the reader. Same as {@link Runnable#run()}.
     */
    public void start();

    /**
     * Called to stop the reader.
     */
    public void stop();
}
