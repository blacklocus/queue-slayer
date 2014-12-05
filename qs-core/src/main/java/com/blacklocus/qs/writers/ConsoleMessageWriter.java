package com.blacklocus.qs.writers;

import com.blacklocus.qs.AbstractMessageWriter;

/**
 * A {@link com.blacklocus.qs.MessageWriter} that prints the records it receives to the console (standard out).
 */
public class ConsoleMessageWriter extends AbstractMessageWriter<String> {
    public boolean write(String record) {
        System.out.println(record);
        return true;
    }
}
