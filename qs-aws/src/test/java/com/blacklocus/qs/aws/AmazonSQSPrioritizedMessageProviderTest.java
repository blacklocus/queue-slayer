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
package com.blacklocus.qs.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.blacklocus.qs.Message;
import com.blacklocus.qs.aws.sqs.AmazonSQSPrioritizedMessageProvider;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AmazonSQSPrioritizedMessageProviderTest {

    @Test
    public void noQueues() {
        AmazonSQS amazonSQS = mock(AmazonSQS.class);

        // return an empty list
        when(amazonSQS.listQueues(any(ListQueuesRequest.class))).thenReturn(new ListQueuesResult());

        AmazonSQSPrioritizedMessageProvider provider = new AmazonSQSPrioritizedMessageProvider(amazonSQS, "test", 10 * 1000);
        List<Message> empty = provider.next();
        Assert.assertEquals(0, empty.size());

        verify(amazonSQS).listQueues(any(ListQueuesRequest.class));
    }

    @Test
    public void oneQueue() {
        AmazonSQS amazonSQS = mock(AmazonSQS.class);

        // return one queue
        when(amazonSQS.listQueues(any(ListQueuesRequest.class)))
                .thenReturn(new ListQueuesResult().withQueueUrls("test-foo"));

        // return 3 messages from the queue
        when(amazonSQS.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(new ReceiveMessageResult().withMessages(newMessage("foo"), newMessage("foo"), newMessage("foo")));

        AmazonSQSPrioritizedMessageProvider provider = new AmazonSQSPrioritizedMessageProvider(amazonSQS, "test", 60 * 1000);
        List<Message> messages = provider.next();
        assertMessages(messages, 3, "foo");

        verify(amazonSQS).listQueues(any(ListQueuesRequest.class));
        verify(amazonSQS).receiveMessage(any(ReceiveMessageRequest.class));
    }

    @Test
    public void drainThreeQueues() {
        AmazonSQS amazonSQS = mock(AmazonSQS.class);

        // return three queues
        when(amazonSQS.listQueues(any(ListQueuesRequest.class)))
                .thenReturn(new ListQueuesResult().withQueueUrls("test-A", "test-C", "test-B", "test-D"));

        // each queue has N messages to return and then 0 messages
        when(amazonSQS.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenAnswer(new Answer<Object>() {

                    // fake that a queue has no messages left with flags
                    boolean aDone = false;
                    boolean bDone = false;
                    boolean cDone = false;

                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        ReceiveMessageRequest receiveMessageRequest = (ReceiveMessageRequest) invocation.getArguments()[0];
                        if (receiveMessageRequest.getQueueUrl().equals("test-A")) {
                            // return 3 messages for A, then no more messages
                            if (!aDone) {
                                aDone = true;
                                return new ReceiveMessageResult().withMessages(newMessage("A"), newMessage("A"), newMessage("A"));
                            }
                        } else if (receiveMessageRequest.getQueueUrl().equals("test-B")) {
                            // return 4 messages for B, then no more messages
                            if (!bDone) {
                                bDone = true;
                                return new ReceiveMessageResult().withMessages(newMessage("B"), newMessage("B"), newMessage("B"), newMessage("B"));
                            }
                        } else if (receiveMessageRequest.getQueueUrl().equals("test-C")) {
                            // return 1 message for C, then no more messages
                            if (!cDone) {
                                cDone = true;
                                return new ReceiveMessageResult().withMessages(newMessage("C"));
                            }
                        }
                        // fall through to return 0 messages
                        return new ReceiveMessageResult().withMessages();
                    }
                });

        // verify the order of next objects by counting the number of messages we return
        AmazonSQSPrioritizedMessageProvider provider = new AmazonSQSPrioritizedMessageProvider(amazonSQS, "test", TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES));

        // for queues with messages, 1 receive returns messages, 1 receive returns none and removes the provider
        assertMessages(provider.next(), 3, "A");
        assertMessages(provider.next(), 4, "B");
        assertMessages(provider.next(), 1, "C");

        // for queues with no messages, 1 receive returns none and removes the provider
        assertMessages(provider.next(), 0, "D-but-nothing-really");

        // when we have no queues left, there are no messages
        assertMessages(provider.next(), 0, "nothing");
        assertMessages(provider.next(), 0, "nothing");
        assertMessages(provider.next(), 0, "nothing");

        // we only checked for queues once here
        verify(amazonSQS, times(1)).listQueues(any(ListQueuesRequest.class));

        // the total number of receives sent to AWS
        verify(amazonSQS, times(7)).receiveMessage(any(ReceiveMessageRequest.class));
    }

    @Test
    public void updatingQueues() {
        AmazonSQS amazonSQS = mock(AmazonSQS.class);

        // return queues
        when(amazonSQS.listQueues(any(ListQueuesRequest.class)))
                .thenReturn(
                        new ListQueuesResult().withQueueUrls("test-A", "test-C", "test-B", "test-D"),
                        new ListQueuesResult().withQueueUrls("test-C", "test-B", "test-D"));

        // always return messages from these queues
        when(amazonSQS.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        ReceiveMessageRequest receiveMessageRequest = (ReceiveMessageRequest) invocation.getArguments()[0];
                        return new ReceiveMessageResult().withMessages(newMessage(receiveMessageRequest.getQueueUrl()), newMessage(receiveMessageRequest.getQueueUrl()));
                    }
                });

        // check for new queues every 30 minutes, should be way longer than this test runs so we can simulate elapsed time
        final long intervalNs = TimeUnit.NANOSECONDS.convert(30, TimeUnit.MINUTES);

        // simulate the passage of time
        final Iterator<Long> time = Lists.newArrayList(
                0L,                                // start at 0, always an initial update
                1 * intervalNs / 2,                // update
                1 * intervalNs,                    // no update
                1 * intervalNs + (intervalNs / 2), // update
                2 * intervalNs,                    // no update
                3 * intervalNs + 1,                // should update
                3 * intervalNs + 2                 // no update
        ).iterator();

        AmazonSQSPrioritizedMessageProvider provider = new AmazonSQSPrioritizedMessageProvider(amazonSQS, "test", TimeUnit.MILLISECONDS.convert(intervalNs, TimeUnit.NANOSECONDS)) {
            @Override
            public long currentNanoTime() {
                return time.next();
            }
        };

        // no update on constructor
        verify(amazonSQS, times(0)).listQueues(any(ListQueuesRequest.class));

        // update on first next
        provider.next();
        verify(amazonSQS, times(1)).listQueues(any(ListQueuesRequest.class));

        // no update
        provider.next();
        verify(amazonSQS, times(1)).listQueues(any(ListQueuesRequest.class));

        // update
        provider.next();
        verify(amazonSQS, times(2)).listQueues(any(ListQueuesRequest.class));

        // no update
        provider.next();
        verify(amazonSQS, times(2)).listQueues(any(ListQueuesRequest.class));

        // update
        provider.next();
        verify(amazonSQS, times(3)).listQueues(any(ListQueuesRequest.class));

        // no update
        provider.next();
        verify(amazonSQS, times(3)).listQueues(any(ListQueuesRequest.class));
    }

    /**
     * Assert all the things about the given messages.
     *
     * @param messages        some messages to check
     * @param expectedSize    expected size of the message list
     * @param expectedContent expected content for every message in the list
     */
    private void assertMessages(List<Message> messages, int expectedSize, String expectedContent) {
        Assert.assertEquals(expectedSize, messages.size());
        for (Message m : messages) {
            Assert.assertEquals(expectedContent, m.getBody());
        }
    }

    public com.amazonaws.services.sqs.model.Message newMessage(String content) {
        return new com.amazonaws.services.sqs.model.Message().withBody(content);
    }
}
