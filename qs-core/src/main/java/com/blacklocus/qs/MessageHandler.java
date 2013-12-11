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
 * Implementations of this class perform message conversion, processing, error
 * handling and hand off of a message retrieved from a queue.
 *
 * @param <T> the type of message that this handler can convert
 * @param <R> the result of processing the converted message
 */
public interface MessageHandler<T, R> extends QueueItemHandler<Message, T, R> {

}
