package com.blacklocus.qs;

/**
 * This class provides boring default implementations for the MessageHandler
 * interface.
 *
 * @param <T> the type of message that this handler can convert
 * @param <R> the result of processing the converted message
 */
public abstract class AbstractMessageHandler<T, R> extends AbstractQueueItemHandler<Message, T, R> implements MessageHandler<T, R> {
}
