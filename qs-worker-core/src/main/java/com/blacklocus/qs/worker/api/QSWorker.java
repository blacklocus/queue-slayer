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
     * @param factory which should be used to generate the {@link TaskKit} from decoded {@link TaskKitFactory#params()}.
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
