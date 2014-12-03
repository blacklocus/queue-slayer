package com.blacklocus.qs.aws.kinesis.writers;

import com.blacklocus.qs.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * An example application that periodically writes sample records to a Kinesis stream using a {@link StreamWriter}.
 */
public class ExampleStreamWriterApp {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleStreamWriterApp.class);

    public static void main(String[] args) {
        Writer<String> writer = new StreamWriterFactory().createWriter();

        int count = 0;

        while(true) {
            // create a message
            String msg = "msg " + count;

            System.out.println(String.format("Writing '%s' to '%s'...", msg, StreamWriterConfig.OUTPUT_STREAM_NAME));

            // write the message
            writer.apply(Arrays.asList(msg));

            // sleep a while
            try {
                Thread.sleep(200);
            } catch (Exception ex) {
                LOG.warn("Failed to sleep.", ex);
            }

            count++;
        }
    }
}
