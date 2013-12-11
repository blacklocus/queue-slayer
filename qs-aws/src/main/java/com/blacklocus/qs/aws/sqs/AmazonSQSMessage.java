/**
 * Copyright 2013 BlackLocus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blacklocus.qs.aws.sqs;

import com.blacklocus.qs.Message;

import java.util.Map;

/**
 * This is an implementation of a Message backed by an Amazon SQS Message.
 */
public class AmazonSQSMessage implements Message {

    private com.amazonaws.services.sqs.model.Message message;

    public AmazonSQSMessage(com.amazonaws.services.sqs.model.Message message) {
        this.message = message;
    }

    @Override
    public String getId() {
        return message.getMessageId();
    }

    @Override
    public String getReceipt() {
        return message.getReceiptHandle();
    }

    @Override
    public String getBody() {
        return message.getBody();
    }

    @Override
    public Map<String, String> getAttributes() {
        return message.getAttributes();
    }

    @Override
    public String toString() {
        return message.getBody();
    }
}
