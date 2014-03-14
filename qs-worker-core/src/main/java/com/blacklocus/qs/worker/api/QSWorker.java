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
package com.blacklocus.qs.worker.api;

import com.blacklocus.qs.QueueItemHandler;
import com.blacklocus.qs.worker.TaskKitFactory;
import com.blacklocus.qs.worker.TaskKit;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.Future;

public interface QSWorker<P> extends QueueItemHandler<TaskKitFactory<P>, TaskKit<P>, Object> {

    String getHandlerName();

    // Just to provide more specific documentation and parameter names.

    /**
     * @param factory which should be used to generate the {@link TaskKit} from decoded {@link TaskKitFactory#paramsJson()}.
     */
    @Override
    TaskKit<P> convert(TaskKitFactory<P> factory) throws Exception;

    @Override
    Object process(TaskKit<P> kit) throws Exception;

    @Override
    void onSuccess(TaskKitFactory<P> factory, TaskKit<P> kit, Object result);

    @Override
    void onError(TaskKitFactory<P> factory, TaskKit<P> kit, Throwable throwable);

    @Override
    void onComplete(TaskKitFactory<P> factory, TaskKit<P> kit, Object result);

    @Override
    void withFuture(TaskKitFactory<P> factory, Future<Pair<TaskKitFactory<P>, Object>> future);
}
