package com.blacklocus.qs;

/**
 * Implementations of this interface provide a mechanism for retrieving
 * messages.
 */
public interface MessageProvider extends QueueItemProvider<Message> {

    /**
     * Delete the given message.
     *
     * @param message the message to delete
     */
    public void delete(Message message);
}
