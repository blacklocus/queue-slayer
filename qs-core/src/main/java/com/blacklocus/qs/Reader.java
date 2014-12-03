package com.blacklocus.qs;

import com.google.common.base.Function;

/**
 * Interface to an object that reads records from a source, transforms them, and sends the transformed records to a
 * {@link com.blacklocus.qs.Writer} for further processing.
 */
public interface Reader<T1, T2> extends Runnable {
    public void setTransform(Function<Iterable<T1>, Iterable<T2>> transform);
    /**
     * Sets this Reader's {@link com.blacklocus.qs.Writer}. This Reader will send transformed records
     * to the {@link com.blacklocus.qs.Writer}.
     */
    public void setWriter(Writer<T2> writer);

    /**
     * Called to start the reader. Same as {@link Runnable#run()}.
     */
    public void start();

    /**
     * Called to stop the reader.
     */
    public void stop();
}
