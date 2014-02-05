package com.blacklocus.qs.worker.util.log;

import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.QSTaskLogger;
import com.blacklocus.qs.worker.QSWorker;
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.model.QSWorkerModel;
import com.google.common.base.Predicate;

/**
 * Wrapper around a {@link QSLogService} which applies a given {@link Predicate} to every method call to determine
 * whether or not to allow it through. Useful if you have a lot of tasks producing a lot of output and want to reduce
 * total logging volume. Some stock predicates are available in {@link SamplingPredicates}.
 *
 * <p>Currently it is only supported to filter logs by the corresponding task. So once a task's logs have been determined
 * to be filtered out (not sampled in), all related log calls to that task will be circumvented. The task logging
 * lifecycle follows<ul>
 *     <li>{@link #startedTask(QSTaskModel)} - task predicate applied. The result of this affects all subsequent
 *                                             QSLogService method calls for this task. <code>true</code> means to
 *                                             sample in all logging messages produced by this task's processing.
 *                                             <code>false</code> to filter out.
 *     <li>{@link #log(QSLogModel)} - any number of logs produced by a worker through the `taskLogger` provided through
 *                                    {@link QSWorker#undertake(Object, QSTaskLogger)} will be sampled in or filtered
 *                                    out as determined by the first step.
 *     <li>{@link #completedTask(QSTaskModel)} -
 * </ul>
 *
 * <p>Future development considerations:<ul>
 *     <li>Allow separate predication of {@link #startedTask(QSTaskModel)} {@link #log(QSLogModel)} {@link #completedTask(QSTaskModel)}
 * </ul>
 */
public class SamplingQSLogService implements QSLogService {

    private final QSLogService delegate;
    private final Predicate<QSTaskModel> taskPredicate;

    private static final ThreadLocal<Boolean> SAMPLED = new ThreadLocal<Boolean>();

    public SamplingQSLogService(QSLogService delegate, Predicate<QSTaskModel> taskPredicate) {
        this.delegate = delegate;
        this.taskPredicate = taskPredicate;
    }

    @Override
    public void startedTask(QSTaskModel task) {
        SAMPLED.set(taskPredicate.apply(task));
        if (SAMPLED.get()) {
            delegate.startedTask(task);
        }
    }

    @Override
    public void log(QSLogModel log) {
        if (SAMPLED.get()) {
            delegate.log(log);
        }
    }

    @Override
    public void completedTask(QSTaskModel task) {
        if (SAMPLED.get()) {
            delegate.completedTask(task);
        }
    }

    @Override
    public void workerHeartbeat(QSWorkerModel worker) {
        delegate.workerHeartbeat(worker);
    }
}
