package com.blacklocus.qs.aws.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.blacklocus.config.SystemPropertyConfig;

/**
 * AWS configuration data.
 */
public class AWSConfig {
    private static final String PROP_CREDENTIALS_PROVIDER_CLASS_NAME = "bl.credentials.provider.class.name";

    private static final String DEFAULT_CREDENTIALS_PROVIDER_CLASS_NAME = "com.amazonaws.auth.DefaultAWSCredentialsProviderChain";

    public static final AWSCredentialsProvider AWS_CREDETIALS_PROVIDER =
        (AWSCredentialsProvider) SystemPropertyConfig.createObjectFromKey(
            PROP_CREDENTIALS_PROVIDER_CLASS_NAME,
            DEFAULT_CREDENTIALS_PROVIDER_CLASS_NAME);
}
