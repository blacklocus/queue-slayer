package com.blacklocus.qs.aws.kinesis.config;

import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClient;

/**
 * Amazon Kinesis config.
 */
public class KinesisAsyncConfig {
    public static final AmazonKinesisAsync KINESIS_ASYNC_CLIENT = new AmazonKinesisAsyncClient();
}
