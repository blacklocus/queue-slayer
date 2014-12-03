package com.blacklocus.qs.aws.sns.transforms;

import com.blacklocus.qs.AbstractTransform;
import com.jayway.jsonpath.JsonPath;

/**
 * A function that extracts the message from an SNS notification.
 */
public class    NotificationToMessage extends AbstractTransform<String, String> {
    private static final JsonPath path = JsonPath.compile("$.Message");

    public String transform(String notification) {
       return JsonPath.parse(notification).read(path).toString();
    }
}
