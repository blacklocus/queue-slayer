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

import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.Future;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public abstract class AbstractQueueItemHandler<Q, T, R> implements QueueItemHandler<Q, T, R> {

    @Override
    public void onSuccess(Q queueItem, T convertedQueueItem, R result) {
        // do nothing
    }

    @Override
    public void onError(Q queueItem, T convertedQueueItem, Throwable throwable) {
        // do nothing
    }

    @Override
    public void onComplete(Q queueItem, T convertedQueueItem, R result) {
        // do nothing
    }

    @Override
    public void withFuture(Q queueItem, Future<Pair<Q, R>> future) {
        // do nothing
    }
}
