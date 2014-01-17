package com.blacklocus.qs.worker.aws;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.blacklocus.qs.worker.model.QSTaskModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class AmazonSQSAsyncPutTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSAsyncPutTaskService.class);

    private final String queueUrl;
    private final ObjectMapper objectMapper;
    private final AmazonSQSAsync sqs;

    public AmazonSQSAsyncPutTaskService(String queueUrl) {
        this(queueUrl, new ObjectMapper(), new AmazonSQSAsyncClient());
    }

    public AmazonSQSAsyncPutTaskService(String queueUrl, ObjectMapper objectMapper, AmazonSQSAsync sqs) {
        this.queueUrl = queueUrl;
        this.objectMapper = objectMapper;
        this.sqs = sqs;
    }

    public void putTask(QSTaskModel task) {
        try {
            LOG.info("Queueing task: {}", task);
            String messageBody = objectMapper.writeValueAsString(task);
            sqs.sendMessage(new SendMessageRequest(queueUrl, messageBody));
            LOG.debug("Queued to {}, message\n\t{}", queueUrl, messageBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
