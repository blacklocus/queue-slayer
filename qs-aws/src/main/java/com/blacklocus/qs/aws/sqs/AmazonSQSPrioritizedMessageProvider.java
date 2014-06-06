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

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.blacklocus.qs.Message;
import com.blacklocus.qs.MessageProvider;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provide a prefix-based SQS priority queue. Each queue matching a prefix is
 * drained in lexicographically sorted order. New queues that are created that
 * match the given prefix are automatically picked up and processed in the same
 * priority order based on a configurable interval. Note that this
 * MessageProvider iterator implementation is not thread-safe. Calls to next()
 * are not intended to be called outside of a single queue reading thread.
 * You will need to externally synchronize calls to next() if you intend to
 * access it in more than one thread. The delete() method is thread-safe.
 */
public class AmazonSQSPrioritizedMessageProvider implements MessageProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSPrioritizedMessageProvider.class);

    public static final Comparator<String> DEFAULT_QUEUE_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    };

    private AmazonSQS sqs;
    private String queuePrefix;
    private long updateIntervalNs;

    private Predicate<String> include;
    private Comparator<String> queueComparator;

    private List<AmazonSQSMessageProvider> messageProviders;
    private long lastNanoTime;

    /**
     * Create a new MessageProvider instance.
     *
     * @param sqs              AmazonSQS instance to use for communicating with AWS
     * @param queuePrefix      the prefix to use when searching for queues
     * @param updateIntervalMs time in milliseconds between when new queues should be checked
     */
    public AmazonSQSPrioritizedMessageProvider(AmazonSQS sqs, String queuePrefix, long updateIntervalMs) {
        this.sqs = sqs;
        this.queuePrefix = queuePrefix;
        this.updateIntervalNs = TimeUnit.MILLISECONDS.toNanos(updateIntervalMs);

        this.include = Predicates.alwaysTrue();
        this.queueComparator = DEFAULT_QUEUE_COMPARATOR;

        this.messageProviders = Lists.newArrayList();

        // ensure first next() causes an updateAvailableQueues()
        this.lastNanoTime = currentNanoTime() - updateIntervalNs - 1;
    }

    /**
     * Queue <b>URLs</b> that return true for this predicate are included in the list
     * of available queues to be prioritized. It defaults to including all
     * available queues pulled for a prefix.
     *
     * @param include the predicate to satisfy
     * @return this instance for chaining
     */
    public AmazonSQSPrioritizedMessageProvider withInclude(Predicate<String> include) {
        this.include = include;
        return this;
    }

    /**
     * This comparator controls the sort order of the returned queues. It
     * defaults to generic String.compareTo().
     *
     * @param queueComparator the custom sorting comparator
     * @return this instance for chaining
     */
    public AmazonSQSPrioritizedMessageProvider withQueueComparator(Comparator<String> queueComparator) {
        this.queueComparator = queueComparator;
        return this;
    }

    /**
     * Returns true if an update to the queues should occur based on the time
     * since this method was last called.
     *
     * @return true if an update to the queues should occur
     */
    public boolean shouldUpdateQueues() {
        long currentNanoTime = currentNanoTime();
        if (currentNanoTime - lastNanoTime >= updateIntervalNs) {
            lastNanoTime = currentNanoTime;
            return true;
        } else {
            return false;
        }
    }

    /**
     * A wrapper around System.nanoTime() to encapsulate elapsed time handling
     * in one place.
     *
     * @return the current value of the running Java Virtual Machine's
     *         high-resolution time source, in nanoseconds
     */
    public long currentNanoTime() {
        return System.nanoTime();
    }

    @Override
    public List<Message> next() {

        if (shouldUpdateQueues()) {
            updateAvailableQueues();
        }

        // iterate until we find a queue with messages
        List<Message> messages = null;
        for (int i = 0; i < messageProviders.size() && messages == null; i++) {
            final AmazonSQSMessageProvider currentProvider = messageProviders.get(i);
            List<Message> current = currentProvider.next();
            if (current.size() > 0) {
                messages = Lists.transform(current, new Function<Message, Message>() {
                    @Override
                    public OriginatingMessage apply(Message input) {
                        return new OriginatingMessage(currentProvider.getQueueUrl(), input);
                    }
                });
            } else {
                // empty queues are discarded from the list of available queues to minimize receive requests
                messageProviders.remove(i);

                // adjust i for shifting of the list
                i--;
            }
        }

        // always return a list, even an empty one when there are no messages
        return messages == null ? Collections.<Message>emptyList() : messages;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported");
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Iterator<Collection<Message>> iterator() {
        return this;
    }

    @Override
    public void delete(Message message) {
        if (message instanceof OriginatingMessage) {
            OriginatingMessage originatingMessage = (OriginatingMessage) message;
            sqs.deleteMessage(new DeleteMessageRequest(originatingMessage.getOriginatingQueueUrl(), message.getReceipt()));
        } else {
            throw new RuntimeException("Unsupported message type: " + message.getBody());
        }
    }

    @Override
    public void setVisibilityTimeout(Message message, Integer visibilityTimeoutSeconds) {
        if (message instanceof OriginatingMessage) {
            OriginatingMessage originatingMessage = (OriginatingMessage) message;
            sqs.changeMessageVisibility(new ChangeMessageVisibilityRequest(originatingMessage.getOriginatingQueueUrl(), message.getReceipt(), visibilityTimeoutSeconds));
        } else {
            throw new RuntimeException("Unsupported message type: " + message.getBody());
        }
    }

    private void updateAvailableQueues() {
        try {
            ListQueuesResult result = sqs.listQueues(new ListQueuesRequest(queuePrefix));
            List<String> availableQueues = Lists.newArrayList(Iterables.filter(result.getQueueUrls(), include));
            Collections.sort(availableQueues, queueComparator);
            messageProviders.clear();
            for (String queueUrl : availableQueues) {
                messageProviders.add(new AmazonSQSMessageProvider(sqs, queueUrl));
            }
        } catch (AmazonClientException e) {
            LOG.error("An error occurred while listing SQS queues: {}", e);
        }
    }

    /**
     * Wrap a {@link Message} and tack on the originating queue url.
     */
    class OriginatingMessage implements Message {

        private String originatingQueueUrl;
        private Message message;

        public OriginatingMessage(String originatingQueueUrl, Message message) {
            this.originatingQueueUrl = originatingQueueUrl;
            this.message = message;
        }

        @Override
        public String getId() {
            return message.getId();
        }

        @Override
        public String getReceipt() {
            return message.getReceipt();
        }

        @Override
        public String getBody() {
            return message.getBody();
        }

        @Override
        public Map<String, String> getAttributes() {
            return message.getAttributes();
        }

        public String getOriginatingQueueUrl() {
            return originatingQueueUrl;
        }
    }
}
