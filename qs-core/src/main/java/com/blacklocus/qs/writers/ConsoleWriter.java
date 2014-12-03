package com.blacklocus.qs.writers;

import com.blacklocus.qs.AbstractWriter;

/**
 * A {@link com.blacklocus.qs.Writer} that prints the records it receives to the console (standard out).
 */
public class ConsoleWriter extends AbstractWriter<String> {
    public boolean write(String record) {
        System.out.println(record);
        return true;
    }
}
