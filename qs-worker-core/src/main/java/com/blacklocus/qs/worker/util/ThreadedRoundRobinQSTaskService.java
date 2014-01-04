package com.blacklocus.qs.worker.util;

import com.blacklocus.misc.ExceptingRunnable;
import com.blacklocus.qs.worker.QSTaskService;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.github.rholder.moar.concurrent.QueueingStrategy;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class ThreadedRoundRobinQSTaskService implements QSTaskService {

    private final SynchronousQueue<QSTaskModel> transferQueue = new SynchronousQueue<QSTaskModel>(true);

    public ThreadedRoundRobinQSTaskService(final QueueingStrategy<QSTaskModel> queueingStrategy, Collection<QSTaskService> taskServices) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (final QSTaskService taskService : taskServices) {

            executorService.submit(new ExceptingRunnable() {
                @Override
                protected void go() throws Exception {
                    while (!Thread.interrupted()) {

                        // If the heap is filling up, this should block for a bit. queueStrategy.on*Remove not called
                        // task processing completes in the WorkerQueueItemHandler.
                        queueingStrategy.onBeforeAdd(null);
                        transferQueue.put(taskService.getAvailableTask());
                        queueingStrategy.onAfterAdd();

                    }
                }
            });

        }
    }

    @Override
    public QSTaskModel getAvailableTask() {
        try {
            return transferQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetTask(QSTaskModel task) {
        ((BackReferencingQSTaskModel) task).resetOriginalTask();
    }

    @Override
    public void closeTask(QSTaskModel task) {
        ((BackReferencingQSTaskModel) task).closeOriginalTask();
    }

    static class BackReferencingQSTaskModel extends QSTaskModel {
        final QSTaskModel task;
        final QSTaskService taskService;

        BackReferencingQSTaskModel(QSTaskModel task, QSTaskService taskService) {
            this.task = task;
            this.taskService = taskService;

            super.batchId = task.batchId;
            super.taskId = task.taskId;
            super.handler = task.handler;
            super.params = task.params;
        }

        void resetOriginalTask() {
            taskService.resetTask(task);
        }

        void closeOriginalTask() {
            taskService.closeTask(task);
        }
    }
}
