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
