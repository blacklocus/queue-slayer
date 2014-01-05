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
