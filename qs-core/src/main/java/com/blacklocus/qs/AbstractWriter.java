package com.blacklocus.qs;

/**
 * An abstract implementation of a {@link com.blacklocus.qs.Writer} that writes one record at a time.
 */
public abstract class AbstractWriter<T> implements Writer<T> {
    public Boolean apply(Iterable<T> records) {
        boolean result = true;

        for (T record : records) {
            result &= write(record);
        }

        return result;
    }

    protected abstract boolean write(T record);
}
