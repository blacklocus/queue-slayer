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
package com.blacklocus.qs.worker;

import com.blacklocus.qs.worker.api.QSWorker;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.Future;

/**
 * Which needs implementations for {@link #getHandlerName()}, {@link #convert(TaskKitFactory)},
 * and {@link #process(TaskKit)}.
 */
public abstract class AbstractQSWorker<P> implements QSWorker<P> {

    @Override
    public void onSuccess(TaskKitFactory<P> factory, TaskKit<P> kit, Object result) {}

    @Override
    public void onError(TaskKitFactory<P> factory, TaskKit<P> kit, Throwable throwable) {}

    @Override
    public void onComplete(TaskKitFactory<P> factory, TaskKit<P> kit, Object result) {}

    @Override
    public void withFuture(TaskKitFactory<P> factory, Future<Pair<TaskKitFactory<P>, Object>> future) {}
}
