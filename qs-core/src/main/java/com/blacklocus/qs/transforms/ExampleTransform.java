package com.blacklocus.qs.transforms;

import com.blacklocus.qs.AbstractTransform;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * An example string-to-string transform.
 */
public class ExampleTransform extends AbstractTransform<String, String> {
    protected String transform(String record) {
        return record.toUpperCase();
    }
}
