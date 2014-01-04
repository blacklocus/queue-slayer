package com.blacklocus.qs.worker.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.blacklocus.qs.worker.QSTaskService;
import com.blacklocus.qs.worker.model.QSTaskModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazonSQSTaskService implements QSTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSTaskService.class);

    private final String queueUrl;
    private final Long pollingIntervalMs;
    private final AmazonSQS sqs;
    private final ObjectMapper objectMapper;

    public AmazonSQSTaskService(String queueUrl, AmazonSQS sqs) {
        this(queueUrl, 60 * 1000L, sqs);
    }

    public AmazonSQSTaskService(String queueUrl, Long pollingIntervalMs, AmazonSQS sqs) {
        this.queueUrl = queueUrl;
        this.pollingIntervalMs = pollingIntervalMs;
        this.sqs = sqs;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public QSTaskModel getAvailableTask() {
        QSTaskModel task = null;
        while (task == null) {
            ReceiveMessageResult result = sqs.receiveMessage(new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(1));
            assert result.getMessages().size() == 1;
            Message message = result.getMessages().get(0);
            message.get
        }
        return new QSTaskModel();
    }

    @Override
    public void resetTask(QSTaskModel task) {
        //TODO jason
    }

    @Override
    public void commitTask(QSTaskModel task) {
        //TODO jason
    }

    private boolean sleep() {
        try {
            Thread.sleep(pollingIntervalMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
