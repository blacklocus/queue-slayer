package com.blacklocus.config;

import com.blacklocus.qs.WriterFactory;
import com.blacklocus.qs.Writer;
import com.blacklocus.qs.Reader;
import com.blacklocus.qs.ReaderFactory;
import com.blacklocus.qs.TransformFactory;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * Consumer configuration data.
 */
public class ConfigUtils extends SystemPropertyConfig {
    /**
     * The default {@link com.blacklocus.qs.ReaderFactory}.
     */
    private static class DefaultReaderFactory<T1, T2> implements ReaderFactory<T1, T2> {
        private final String className;

        public DefaultReaderFactory(String className) {
            this.className = className;
        }

        @SuppressWarnings("unchecked")
        public Reader<T1, T2> createReader() {
            return (Reader<T1, T2>) createObjectFrom(className);
        }
    }

    /**
     * Returns a {@link com.blacklocus.qs.ReaderFactory} used to create {@link com.blacklocus.qs.Reader}s
     * capable of reading records from a data source and feeding them to a {@link com.blacklocus.qs.Writer}.
     */
    @SuppressWarnings("unchecked")
    public static <T1, T2> ReaderFactory<T1, T2> createReaderFactory(String factoryPropKey, String objectPropKey) {
        String factoryClassName = optional(factoryPropKey, null);

        if (factoryClassName == null) {
            return new DefaultReaderFactory(required(objectPropKey));
        } else {
            return (ReaderFactory<T1, T2>) createObjectFrom(factoryClassName);
        }
    }

    /**
     * The default {@link com.blacklocus.qs.TransformFactory}.
     */
    private static class DefaultTransformFactory<T1, T2> implements TransformFactory<T1, T2> {
        private final String className;

        public DefaultTransformFactory(String className) {
            this.className = className;
        }

        @SuppressWarnings("unchecked")
        public Function<Iterable<T1>, Iterable<T2>> createTransform() {
            return (Function<Iterable<T1>, Iterable<T2>>) createObjectFrom(className);
        }
    }

    /**
     * Returns a {@link com.blacklocus.qs.TransformFactory} used to create a transformation {@link com.google.common.base.Function}
     * capable of transforming an object of type T2 to an object of type T2.
     */
    @SuppressWarnings("unchecked")
    public static <T1, T2> TransformFactory<T1, T2> createTransformFactory(String factoryPropKey, String objectPropKey) {
        String factoryClassName = optional(factoryPropKey, null);

        if (factoryClassName == null) {
            return new DefaultTransformFactory(required(objectPropKey));
        } else {
            return (TransformFactory<T1, T2>) createObjectFrom(factoryClassName);
        }
    }

    /**
     * The default {@link com.blacklocus.qs.WriterFactory}.
     */
    private static class DefaultWriterFactory<T> implements WriterFactory<T> {
        private final String className;

        public DefaultWriterFactory(String className) {
            this.className = className;
        }

        @SuppressWarnings("unchecked")
        public Writer<T> createWriter() {
            return (Writer<T>) createObjectFrom(className);
        }
    }

    /**
     * Returns a {@link com.blacklocus.qs.WriterFactory} used to create {@link com.blacklocus.qs.Writer}s
     * capable of consuming records of type T.
     */
    @SuppressWarnings("unchecked")
    public static <T> WriterFactory<T> createWriterFactory(String factoryPropKey, String objectPropKey) {
        String factoryClassName = optional(factoryPropKey, null);

        if (factoryClassName == null) {
            return new DefaultWriterFactory(required(objectPropKey));
        } else {
            return (WriterFactory<T>) createObjectFrom(factoryClassName);
        }
    }
}
