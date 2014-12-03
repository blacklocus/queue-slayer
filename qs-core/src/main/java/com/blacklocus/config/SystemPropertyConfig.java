package com.blacklocus.config;

/**
 * Base class for obtaining system-property configuration data.
 */
public class SystemPropertyConfig {
    public static String required(String key) {
        String value = System.getProperty(key, null);

        if (value == null) {
            throw new RuntimeException(String.format("You must specify the '%s' property.", key));
        }

        return value;
    }

    public static String optional(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    public static Object createObjectFrom(String className) {
        try {
            return createObjectFrom(Class.forName(className));
        } catch (Exception ex) {
            String msg = String.format("Failed to create an object for class '%s'!", className);
            throw new RuntimeException(msg, ex);
        }
    }

    public static Object createObjectFrom(Class<?> cls) {
        try {
            return cls.newInstance();
        } catch (Exception ex) {
            String msg = String.format("Failed to create an object for class '%s'!", cls.getName());
            throw new RuntimeException(msg, ex);
        }
    }

    public static Object createObjectFromKey(String classNameKey) {
        return createObjectFrom(SystemPropertyConfig.required(classNameKey));
    }

    public static Object createObjectFromKey(String classNameKey, String defaultClassName) {
        return createObjectFrom(SystemPropertyConfig.optional(classNameKey, defaultClassName));
    }
}
