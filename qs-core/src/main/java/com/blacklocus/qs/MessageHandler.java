package com.blacklocus.qs;

/**
 * Implementations of this class perform message conversion, processing, error
 * handling and hand off of a message retrieved from a queue.
 *
 * @param <T> the type of message that this handler can convert
 * @param <R> the result of processing the converted message
 */
public interface MessageHandler<T, R> extends QueueItemHandler<Message, T, R> {

}
