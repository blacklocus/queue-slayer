package com.blacklocus.config;

import com.blacklocus.qs.MessageReader;
import com.blacklocus.qs.MessageReaderFactory;
import com.blacklocus.qs.MessageWriter;
import com.blacklocus.qs.MessageWriterFactory;
import com.blacklocus.qs.TransformFactory;
import com.google.common.base.Function;

/**
 * Consumer configuration data.
 */
public class ConfigUtils extends SystemPropertyConfig {
    /**
     * The default {@link com.blacklocus.qs.MessageReaderFactory}.
     */
    private static class DefaultMessageReaderFactory<T1, T2> implements MessageReaderFactory<T1, T2> {
        private final String className;

        public DefaultMessageReaderFactory(String className) {
            this.className = className;
        }

        @SuppressWarnings("unchecked")
        public MessageReader<T1, T2> createReader() {
            return (MessageReader<T1, T2>) createObjectFrom(className);
        }
    }

    /**
     * Returns a {@link com.blacklocus.qs.MessageReaderFactory} used to create {@link com.blacklocus.qs.MessageReader}s
     * capable of reading records from a data source and feeding them to a {@link com.blacklocus.qs.MessageWriter}.
     */
    @SuppressWarnings("unchecked")
    public static <T1, T2> MessageReaderFactory<T1, T2> createReaderFactory(String factoryPropKey, String objectPropKey) {
        String factoryClassName = optional(factoryPropKey, null);

        if (factoryClassName == null) {
            return new DefaultMessageReaderFactory(required(objectPropKey));
        } else {
            return (MessageReaderFactory<T1, T2>) createObjectFrom(factoryClassName);
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
     * The default {@link com.blacklocus.qs.MessageWriterFactory}.
     */
    private static class DefaultMessageWriterFactory<T> implements MessageWriterFactory<T> {
        private final String className;

        public DefaultMessageWriterFactory(String className) {
            this.className = className;
        }

        @SuppressWarnings("unchecked")
        public MessageWriter<T> createWriter() {
            return (MessageWriter<T>) createObjectFrom(className);
        }
    }

    /**
     * Returns a {@link com.blacklocus.qs.MessageWriterFactory} used to create {@link com.blacklocus.qs.MessageWriter}s
     * capable of consuming records of type T.
     */
    @SuppressWarnings("unchecked")
    public static <T> MessageWriterFactory<T> createWriterFactory(String factoryPropKey, String objectPropKey) {
        String factoryClassName = optional(factoryPropKey, null);

        if (factoryClassName == null) {
            return new DefaultMessageWriterFactory(required(objectPropKey));
        } else {
            return (MessageWriterFactory<T>) createObjectFrom(factoryClassName);
        }
    }
}
