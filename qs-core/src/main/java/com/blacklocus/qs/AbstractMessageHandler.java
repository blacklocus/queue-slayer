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

/**
 * This class provides boring default implementations for the MessageHandler
 * interface.
 *
 * @param <T> the type of message that this handler can convert
 * @param <R> the result of processing the converted message
 */
public abstract class AbstractMessageHandler<T, R> extends AbstractQueueItemHandler<Message, T, R> implements MessageHandler<T, R> {
}
