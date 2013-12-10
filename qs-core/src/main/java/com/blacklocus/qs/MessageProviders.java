package com.blacklocus.qs;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Provide some common functionality to wrap around existing
 * {@link MessageProvider} implementations.
 */
public class MessageProviders {

    /**
     * Chain together a safe and an empty message return tracking
     * {@link MessageProvider}.
     *
     * @param messageProvider   the base {@link MessageProvider} to wrap
     * @param maxTimesNextEmpty the number of times next() can return with 0
     *                          messages until hasNext() begins to return false
     * @return the wrapped {@link MessageProvider}
     */
    public static MessageProvider newDefaultMessageProvider(MessageProvider messageProvider, long maxTimesNextEmpty) {
        return newSafeMessageProvider(newUntilNextEmptyMessageProvider(messageProvider, maxTimesNextEmpty));
    }

    /**
     * Chain together a safe and an empty message return tracking
     * {@link MessageProvider}.
     *
     * @param messageProvider   the base {@link MessageProvider} to wrap
     * @param maxTimesNextEmpty the number of times next() can return with 0
     *                          messages until hasNext() begins to return false
     * @param maxMessages       An maximum limit of messages to provide
     *                          until hasNext() begins to return false
     * @return the wrapped {@link MessageProvider}
     */
    public static MessageProvider newDefaultMessageProvider(MessageProvider messageProvider, long maxTimesNextEmpty, int maxMessages) {
        return newSafeMessageProvider(newUntilNextEmptyOrMaximumMessageProvider(messageProvider, maxTimesNextEmpty, maxMessages));
    }

    /**
     * Wrap each call of the given {@link MessageProvider} with a try/catch
     * such that they always return correctly without throwing an
     * {@link Exception}. Caught {@link Exception}'s are swallowed and logged.
     *
     * @param messageProvider the base {@link MessageProvider} to wrap
     * @return the safely wrapped {@link MessageProvider}
     */
    public static MessageProvider newSafeMessageProvider(final MessageProvider messageProvider) {
        return new MessageProvider() {

            private final Logger log = LoggerFactory.getLogger(getClass());

            @Override
            public Iterator<Collection<Message>> iterator() {
                return this;
            }

            @Override
            public boolean hasNext() {
                try {
                    return messageProvider.hasNext();
                } catch (Throwable t) {
                    log.error("An error occurred during hasNext()", t);
                    return true;
                }
            }

            @Override
            public Collection<Message> next() {
                try {
                    return messageProvider.next();
                } catch (Throwable t) {
                    log.error("An error occurred during next()", t);
                    return Collections.emptyList();
                }
            }

            @Override
            public void remove() {
                try {
                    messageProvider.remove();
                } catch (Throwable t) {
                    log.error("An error occurred during remove()", t);
                }
            }

            @Override
            public void delete(Message message) {
                try {
                    messageProvider.delete(message);
                } catch (Throwable t) {
                    log.error("An error occurred during delete()", t);
                }
            }
        };
    }

    /**
     * Wrap an existing {@link MessageProvider} such that after a specified
     * number of calls to next() that results in 0 messages being returned,
     * hasNext() flips over to return false. If the underlying
     * {@link MessageProvider}'s hasNext() returns false before the threshold is
     * exceeded, the hasNext() will also return false.
     *
     * @param messageProvider   the base {@link MessageProvider} to wrap
     * @param maxTimesNextEmpty the number of times next() can return with 0
     *                          messages until hasNext() begins to return false
     * @return the empty message handling wrapped {@link MessageProvider}
     */
    public static MessageProvider newUntilNextEmptyMessageProvider(
            final MessageProvider messageProvider,
            final long maxTimesNextEmpty) {

        return new DelegatingMessageProvider(messageProvider) {
            private long timesEmpty = 0;

            @Override
            public boolean hasNext() {
                return messageProvider.hasNext() && timesEmpty <= maxTimesNextEmpty;
            }

            @Override
            public Collection<Message> next() {
                Collection<Message> messages = messageProvider.next();
                if (messages.size() > 0) {
                    timesEmpty = 0;
                } else {
                    timesEmpty++;
                }
                return messages;
            }

        };
    }

    /**
     * Wrap an existing {@link MessageProvider} such that after a specified
     * number of calls to next() that results in 0 messages being returned, or
     * the maximum limit of messages is reached, hasNext() flips over to
     * return false. If the underlying {@link MessageProvider}'s hasNext()
     * returns false before the threshold is exceeded,
     * the hasNext() will also return false.
     * <p/>
     * Since the existing {@link MessageProvider}'s next() method might return more
     * than one message, the maxMessages parameter can be exceeded for that particular
     * next() call. However, the hasNext() method will return false on the following call.
     *
     * @param messageProvider   the base {@link MessageProvider} to wrap
     * @param maxTimesNextEmpty the number of times next() can return with 0
     *                          messages until hasNext() begins to return false
     * @param maxMessages       An maximum limit of messages to provide
     *                          until hasNext() begins to return false
     * @return the empty message handling wrapped {@link MessageProvider}
     */
    public static MessageProvider newUntilNextEmptyOrMaximumMessageProvider(
            final MessageProvider messageProvider,
            final long maxTimesNextEmpty,
            final int maxMessages) {

        return newUntilSlowOrMaximumMessageProvider(messageProvider, maxTimesNextEmpty, 0, maxMessages);
    }

    /**
     * Like {@link #newUntilNextEmptyOrMaximumMessageProvider(MessageProvider, long, int)} but instead of detecting
     * numerous "empties" considers numerous "slows", where that is defined to be calls to next that produce less
     * than the specified batch threshold. This is useful in the case that a queue is being written to while being
     * dumped. This can cause endless, slow queue reading as a messages continuously trickle in before "timesEmpty" is
     * achieved. Logic is identical except that instead of requiring empty, "timesSlow" is counted.
     * <p/>
     * {@link #newUntilNextEmptyOrMaximumMessageProvider(MessageProvider, long, int)} is actually a specialized form
     * of this wrapper, where 'slowThreshold = 0'
     *
     * @param slowThreshold calls to next that contribute less than or equal to this threshold will contribute one
     *                      increment to 'timesSlow' where when 'timesSlow == maxTimesSlow', the MessageProvider will
     *                      terminate. For AmazonSQS, 10 is the usual returned message clump. So anything less than 10
     *                      may be a valid consideration for slow, e.g. 9, but in any case depends on the underlying
     *                      MessageProvider.
     */
    public static MessageProvider newUntilSlowOrMaximumMessageProvider(
            final MessageProvider messageProvider,
            final long maxTimesSlow,
            final int slowThreshold,
            final int maxMessages) {

        return new DelegatingMessageProvider(messageProvider) {
            private long timesSlow = 0;
            private int messageCounter = 0;

            @Override
            public boolean hasNext() {
                return messageProvider.hasNext() && timesSlow <= maxTimesSlow && messageCounter < maxMessages;
            }

            @Override
            public Collection<Message> next() {
                Collection<Message> messages = messageProvider.next();
                if (messages.size() > slowThreshold) {
                    timesSlow = 0;
                } else {
                    timesSlow++;
                }
                messageCounter += messages.size();
                return messages;
            }

        };
    }

    /**
     * Filters the values returned by {@link MessageProvider#next()} by some filter. This does not reduce the
     * scan count of the given message provider, but does reduce what bubbles out of it throught `next`. Note that
     * this may cause some calls to `next` to contain 0 elements even though there are more messages to scan.
     */
    public static MessageProvider filtered(
            final MessageProvider messageProvider,
            final Predicate<Message> filter) {

        return new DelegatingMessageProvider(messageProvider) {
            @Override
            public Collection<Message> next() {
                return Collections2.filter(messageProvider.next(), filter);
            }
        };
    }

    /**
     * Applies an arbitrary callback to the results of calls to {@link MessageProvider#next()} before bubbling onward.
     */
    public static MessageProvider scanningCallback(
            final MessageProvider messageProvider,
            final NextCallback nextCallback) {

        return new DelegatingMessageProvider(messageProvider) {
            @Override
            public Collection<Message> next() {
                Collection<Message> messages = super.next();
                nextCallback.next(messages);
                return messages;
            }
        };
    }

    public static interface NextCallback {
        void next(Collection<Message> messages);
    }
}

class DelegatingMessageProvider implements MessageProvider {

    final MessageProvider messageProvider;

    DelegatingMessageProvider(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public Iterator<Collection<Message>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return messageProvider.hasNext();
    }

    @Override
    public Collection<Message> next() {
        return messageProvider.next();
    }

    @Override
    public void remove() {
        messageProvider.remove();
    }

    @Override
    public void delete(Message message) {
        messageProvider.delete(message);
    }
}