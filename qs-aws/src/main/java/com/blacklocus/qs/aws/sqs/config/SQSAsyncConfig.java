package com.blacklocus.qs.aws.sqs.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.blacklocus.qs.aws.config.AWSConfig;

/**
 * Amazon SQS configuration.
 */
public class SQSAsyncConfig {
    public static final AmazonSQSAsync SQS_ASYNC_CLIENT = new AmazonSQSAsyncClient(AWSConfig.AWS_CREDENTIALS_PROVIDER);
}
