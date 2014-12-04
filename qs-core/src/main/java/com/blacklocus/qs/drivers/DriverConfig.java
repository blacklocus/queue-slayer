package com.blacklocus.qs.drivers;

import com.blacklocus.config.ConfigUtils;
import com.blacklocus.qs.MessageReaderFactory;
import com.blacklocus.qs.MessageWriterFactory;
import com.blacklocus.qs.TransformFactory;

/**
 * System property configuration information for the {@link com.blacklocus.qs.drivers.DriverApp}.
 */
public class DriverConfig {
    private static final String PROP_READER_FACTORY_CLASS_NAME = "bl.reader.factory.class.name";
    private static final String PROP_READER_CLASS_NAME = "bl.reader.class.name";

    private static final String PROP_WRITER_FACTORY_CLASS_NAME = "bl.writer.factory.class.name";
    private static final String PROP_WRITER_CLASS_NAME = "bl.writer.class.name";

    private static final String PROP_TRANSFORM_FACTORY_CLASS_NAME = "bl.transform.factory.class.name";
    private static final String PROP_TRANSFORM_CLASS_NAME = "bl.transform.class.name";

    @SuppressWarnings("unchecked")
    public static final MessageReaderFactory<String, String> READER_FACTORY =
            ConfigUtils.createReaderFactory(PROP_READER_FACTORY_CLASS_NAME, PROP_READER_CLASS_NAME);

    @SuppressWarnings("unchecked")
    public static final TransformFactory<String, String> TRANSFORM_FACTORY =
            ConfigUtils.createTransformFactory(PROP_TRANSFORM_FACTORY_CLASS_NAME, PROP_TRANSFORM_CLASS_NAME);

    @SuppressWarnings("unchecked")
    public static final MessageWriterFactory<String> WRITER_FACTORY =
            ConfigUtils.createWriterFactory(PROP_WRITER_FACTORY_CLASS_NAME, PROP_WRITER_CLASS_NAME);
}
