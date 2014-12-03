package com.blacklocus.qs.aws.sns.config;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;

/**
 * author: steve
 */
public class SNSAsyncConfig {
    public static final AmazonSNSAsync SNS_ASYNC_CLIENT = new AmazonSNSAsyncClient();
}
