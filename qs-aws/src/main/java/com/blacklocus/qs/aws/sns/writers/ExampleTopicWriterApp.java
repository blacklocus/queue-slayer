package com.blacklocus.qs.aws.sns.writers;

import com.blacklocus.qs.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * An example application that periodically writes sample records onto an SNS topic
 * using a {@link TopicWriter}.
 */
public class ExampleTopicWriterApp {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleTopicWriterApp.class);

    public static void main(String[] args) {
        Writer<String> writer = new TopicWriterFactory().createWriter();

        int count = 0;

        while(true) {
            // create a message
            String msg = "msg " + count;

            System.out.println(String.format("Writing '%s' to '%s'...", msg, TopicWriterConfig.OUTPUT_TOPIC_ARN));

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
