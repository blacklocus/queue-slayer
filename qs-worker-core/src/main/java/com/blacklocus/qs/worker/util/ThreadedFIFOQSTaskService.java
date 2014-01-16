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
import com.blacklocus.misc.InfiniteRunnable;
import com.blacklocus.qs.worker.QSTaskService;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.github.rholder.moar.concurrent.QueueingStrategy;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
class ThreadedFIFOQSTaskService implements QSTaskService {

    private final QueueingStrategy<QSTaskModel> queueingStrategy;

    // This fair=true is essentially what makes it round-robin.
    private final SynchronousQueue<QSTaskModel> transferQueue = new SynchronousQueue<QSTaskModel>(true);
    private final Map<QSTaskModel, QSTaskService> taskServices = new ConcurrentHashMap<QSTaskModel, QSTaskService>();

    ThreadedFIFOQSTaskService(final QueueingStrategy<QSTaskModel> queueingStrategy, Collection<QSTaskService> taskServices) {
        this.queueingStrategy = queueingStrategy;

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (final QSTaskService taskService : taskServices) {
            executorService.submit(new InfiniteRunnable(new TaskTransferRunnable(taskService)));
        }
    }

    @Override
    public void putTask(QSTaskModel task) {
        throw new RuntimeException("ThreadedRoundRobinQSTaskService is purposed for retrieving tasks and cannot " +
                "determine which constituent Task Service should be used to place a new task.");
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
        taskServices.remove(task).resetTask(task);
    }

    @Override
    public void closeTask(QSTaskModel task) {
        taskServices.remove(task).closeTask(task);
    }

    class TaskTransferRunnable extends ExceptingRunnable {

        final QSTaskService taskService;

        TaskTransferRunnable(QSTaskService taskService) {
            this.taskService = taskService;
        }

        @Override
        protected void go() throws Exception {
            // If the heap is filling up, this should block for a bit. queueStrategy.on*Remove called when
            // task processing completes in the WorkerQueueItemHandler.
            queueingStrategy.onBeforeAdd(null);
            QSTaskModel task = taskService.getAvailableTask();
            taskServices.put(task, taskService);
            transferQueue.put(task);
            queueingStrategy.onAfterAdd();
        }
    }
}
