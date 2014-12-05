package com.blacklocus.qs.aws.kinesis.writers;

import com.google.common.base.Function;

/**
 * A function that generates random Kinesis partition keys.
 */
public class MessageToRandomPartitionKey<T> implements Function<T, String> {
    public String apply(T message) {
        return Double.toString(Math.random());
    }
}
