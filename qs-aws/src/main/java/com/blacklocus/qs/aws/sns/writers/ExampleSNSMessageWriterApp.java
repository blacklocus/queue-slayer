package com.blacklocus.qs.aws.sns.writers;

import com.blacklocus.qs.MessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * An example application that periodically writes sample records onto an SNS topic
 * using a {@link SNSMessageWriter}.
 */
public class ExampleSNSMessageWriterApp {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleSNSMessageWriterApp.class);

    public static void main(String[] args) {
        MessageWriter<String> writer = new SNSMessageWriterFactory().createWriter();

        int count = 0;

        while(true) {
            // create a message
            String msg = "msg " + count;

            System.out.println(String.format("Writing '%s' to '%s'...", msg, SNSMessageWriterConfig.OUTPUT_TOPIC_ARN));

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
