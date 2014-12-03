package com.blacklocus.qs.drivers;

import com.blacklocus.qs.Writer;
import com.blacklocus.qs.Reader;
import com.google.common.base.Function;

/**
 * An object that combines a {@link com.blacklocus.qs.Reader},
 * transform {@link com.google.common.base.Function},
 * and {@link com.blacklocus.qs.Writer} into a runnable
 * application.
 */
public class DriverApp {
    public static void main(String[] args) {
        // create the writer
        final Writer<String> writer = DriverConfig.WRITER_FACTORY.createWriter();

        // create the transform
        final Function<Iterable<String>, Iterable<String>> transform = DriverConfig.TRANSFORM_FACTORY.createTransform();

        // create the reader
        Reader<String, String> reader = DriverConfig.READER_FACTORY.createReader();

        // set the transform on the reader
        reader.setTransform(transform);

        // set the writer on the reader
        reader.setWriter(writer);

        // start the reader
        reader.start();
    }
}
