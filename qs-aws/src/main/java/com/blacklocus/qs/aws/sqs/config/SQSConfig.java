package com.blacklocus.qs.aws.sqs.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.blacklocus.qs.aws.config.AWSConfig;

/**
 * Amazon SQS configuration.
 */
public class SQSConfig {
    public static final AmazonSQS SQS_CLIENT = new AmazonSQSClient(AWSConfig.AWS_CREDENTIALS_PROVIDER);
}
