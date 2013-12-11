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
package com.blacklocus.qs;

import java.util.concurrent.ExecutorService;

/**
 * This class provides a general purpose "grab thing off queue and process it
 * with threads" abstraction, decoupling the queue reading thread from the pool
 * of processing threads.
 *
 * @param <T> the type of message that this reader can convert
 * @param <R> the result of processing the converted message
 */
public class MessageQueueReader<T, R> extends QueueReader<Message, T, R> {

    /**
     * Construct a new MessageQueueReader with the default idle timeout.
     *
     * @param messageProvider provider to endlessly pull messages from
     * @param handler         handler implementation to convert and process messages
     * @param executor        executor service used for forking message handler processing
     */
    public MessageQueueReader(MessageProvider messageProvider,
                              MessageHandler<T, R> handler,
                              ExecutorService executor) {
        super(messageProvider, handler, executor, DEFAULT_SLEEP_MS);
    }

    /**
     * Construct a new MessageQueueReader with the given timeout.
     *
     * @param messageProvider provider to endlessly pull messages from
     * @param handler         handler implementation to convert and process messages
     * @param executor        executor service used for forking message handler processing
     * @param sleepMs         how long to sleep in ms between reads from the queue where no messages are returned
     */
    public MessageQueueReader(MessageProvider messageProvider,
                              MessageHandler<T, R> handler,
                              ExecutorService executor,
                              long sleepMs) {
        super(messageProvider, handler, executor, sleepMs);
    }

}