package com.blacklocus.qs;

import java.util.Map;

/**
 * This is a simple message abstraction.
 */
public interface Message {

    /**
     * Return the unique identifier for this message.
     */
    public String getId();

    /**
     * Return the unique message receipt used to signal an instance of this
     * message was received and can be acknowledged or deleted.
     */
    public String getReceipt();

    /**
     * Return the message body content.
     */
    public String getBody();

    /**
     * Return the message attributes as a map.
     */
    public Map<String, String> getAttributes();
}
