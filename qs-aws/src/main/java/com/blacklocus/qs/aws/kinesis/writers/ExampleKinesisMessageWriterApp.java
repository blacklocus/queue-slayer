package com.blacklocus.qs.aws.kinesis.writers;

import com.blacklocus.qs.MessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * An example application that periodically writes sample records to a Kinesis stream using a {@link KinesisMessageWriter}.
 */
public class ExampleKinesisMessageWriterApp {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleKinesisMessageWriterApp.class);

    public static void main(String[] args) {
        MessageWriter<String> writer = new KinesisMessageWriterFactory().createWriter();

        int count = 0;

        while(true) {
            // create a message
            String msg = "msg " + count;

            System.out.println(String.format("Writing '%s' to '%s'...", msg, KinesisMessageWriterConfig.OUTPUT_STREAM_NAME));

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
