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

import com.blacklocus.qs.QueueReader;
import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.QSTaskService;
import com.blacklocus.qs.worker.QSWorker;
import com.blacklocus.qs.worker.QSWorkerIdService;
import com.blacklocus.qs.worker.config.QSConfig;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.github.rholder.moar.concurrent.QueueingStrategies;
import com.github.rholder.moar.concurrent.QueueingStrategy;
import com.github.rholder.moar.concurrent.StrategicExecutors;
import com.github.rholder.moar.concurrent.thread.CallerBlocksPolicy;
import com.google.common.collect.Iterables;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.github.rholder.moar.concurrent.StrategicExecutors.DEFAULT_BALANCE_AFTER;
import static com.github.rholder.moar.concurrent.StrategicExecutors.DEFAULT_SMOOTHING_WEIGHT;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSProcessBuilder {

    private final List<QSTaskService> taskServices = new ArrayList<QSTaskService>();
    private final CompositeConfiguration configuration = new CompositeConfiguration();
    private final Map<String, QSWorker> workers = new HashMap<String, QSWorker>();
    private QSLogService logService;
    private QSWorkerIdService workerIdService;

    public QSProcessBuilder configuration(Configuration configuration) {
        this.configuration.addConfiguration(configuration);
        return this;
    }

    public QSProcessBuilder taskServices(QSTaskService... taskServices) {
        this.taskServices.addAll(Arrays.asList(taskServices));
        return this;
    }

    public QSProcessBuilder logService(QSLogService logService) {
        this.logService = logService;
        return this;
    }

    public QSProcessBuilder workerIdService(QSWorkerIdService workerIdService) {
        this.workerIdService = workerIdService;
        return this;
    }

    public QSProcessBuilder workers(QSWorker... workers) {
        for (QSWorker worker : workers) {
            this.workers.put(worker.getHandlerName(), worker);
        }
        return this;
    }

    public QueueReader build() {

        configuration.addConfiguration(QSConfig.DEFAULTS);

        QueueingStrategy<QSTaskModel> queueingStrategy = QueueingStrategies.newHeapQueueingStrategy(
                configuration.getDouble(QSConfig.PROP_HEAP_STRATEGY_TRIGGER),
                configuration.getLong(QSConfig.PROP_HEAP_STRATEGY_MAX_DELAY),
                configuration.getLong(QSConfig.PROP_HEAP_STRATEGY_HINT)
        );
        QSTaskService taskService = new ThreadedRoundRobinQSTaskService(queueingStrategy, taskServices);
        TaskServiceIterable taskIterable = new TaskServiceIterable(taskService);
        Iterable<Collection<TaskHandle>> taskControlIterable = Iterables.transform(taskIterable, new TaskControlFunction(workerIdService));

        ExecutorService workerExecutorService = StrategicExecutors.newBalancingThreadPoolExecutor(
                new ThreadPoolExecutor(
                        configuration.getInt(QSConfig.PROP_WORKER_POOL_CORE),
                        configuration.getInt(QSConfig.PROP_WORKER_POOL_MAX),
                        1, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(), new CallerBlocksPolicy()
                ),
                configuration.getFloat(QSConfig.PROP_WORKER_POOL_UTILIZATION), DEFAULT_SMOOTHING_WEIGHT, DEFAULT_BALANCE_AFTER
        );

        return new QueueReader<TaskHandle, TaskHandle, Object>(
                taskControlIterable,
                new WorkerQueueItemHandler(queueingStrategy, taskService, logService, workerIdService, workers),
                workerExecutorService,
                0
        );
    }

}
