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

import com.blacklocus.misc.Runnables;
import com.blacklocus.qs.QueueReader;
import com.blacklocus.qs.worker.api.QSLogService;
import com.blacklocus.qs.worker.api.QSTaskService;
import com.blacklocus.qs.worker.api.QSWorker;
import com.blacklocus.qs.worker.api.QSWorkerIdService;
import com.blacklocus.qs.worker.config.QSConfig;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.util.task.ThreadedFIFOQSTaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rholder.moar.concurrent.QueueingStrategies;
import com.github.rholder.moar.concurrent.QueueingStrategy;
import com.github.rholder.moar.concurrent.StrategicExecutors;
import com.github.rholder.moar.concurrent.thread.CallerBlocksPolicy;
import com.google.common.base.Preconditions;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
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
public class QSAssembly {

    public static QSAssembly newBuilder() {
        return new QSAssembly();
    }


    private final List<QSTaskService> taskServices = new ArrayList<QSTaskService>();
    private final CompositeConfiguration configuration = new CompositeConfiguration();
    private final Map<String, QSWorker<Object>> workers = new HashMap<String, QSWorker<Object>>();
    private QSLogService logService;
    private QSWorkerIdService workerIdService;
    private ObjectMapper objectMapper = new ObjectMapper();

    private QSAssembly() {
    }

    public QSAssembly configuration(Configuration configuration) {
        this.configuration.addConfiguration(configuration);
        return this;
    }

    public QSAssembly taskServices(QSTaskService... taskServices) {
        this.taskServices.addAll(Arrays.asList(taskServices));
        return this;
    }

    public QSAssembly logService(QSLogService logService) {
        this.logService = logService;
        return this;
    }

    public QSAssembly workerIdService(QSWorkerIdService workerIdService) {
        this.workerIdService = workerIdService;
        return this;
    }

    @SuppressWarnings("unchecked")
    public QSAssembly workers(QSWorker<?>... workers) {
        // Accepts <?> for convenience. Casted away.
        for (QSWorker<?> worker : workers) {
            this.workers.put(worker.getHandlerName(), (QSWorker<Object>) worker);
        }
        return this;
    }

    public QSAssembly objectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public void validate() {
        Preconditions.checkState(taskServices.size() > 0, "At least one QSTaskService must be present.");
        Preconditions.checkState(workers.size() > 0, "At least one QSWorker must be present.");
        Preconditions.checkNotNull(logService, "A QSLogService implementation is required.");
        Preconditions.checkNotNull(workerIdService, "A QSWorkerIdService implementation is required.");
        Preconditions.checkNotNull(objectMapper, "An ObjectMapper must be available.");
    }

    /**
     * @return a configured QueueReader to process tasks. The QueueReader must be started via {@link QueueReader#run()}.
     */
    public QueueReader build() {
        validate();

        Runnable heartbeater = Runnables.newInfiniteLoggingRunnable(new QSWorkerHeartbeater(workerIdService, logService));
        new Thread(heartbeater, "WorkerHeartbeater").start();

        configuration.addConfiguration(QSConfig.DEFAULTS);

        QueueingStrategy<QSTaskModel> queueingStrategy = QueueingStrategies.newHeapQueueingStrategy(
                configuration.getDouble(QSConfig.PROP_HEAP_STRATEGY_TRIGGER),
                configuration.getLong(QSConfig.PROP_HEAP_STRATEGY_MAX_DELAY),
                configuration.getLong(QSConfig.PROP_HEAP_STRATEGY_HINT)
        );
        QSTaskService taskService = new ThreadedFIFOQSTaskService(queueingStrategy, taskServices);
        TaskServiceIterable taskIterable = new TaskServiceIterable(taskService);

        ExecutorService workerExecutorService = StrategicExecutors.newBalancingThreadPoolExecutor(
                new ThreadPoolExecutor(
                        configuration.getInt(QSConfig.PROP_WORKER_POOL_CORE),
                        configuration.getInt(QSConfig.PROP_WORKER_POOL_MAX),
                        1, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(), new CallerBlocksPolicy()
                ),
                configuration.getFloat(QSConfig.PROP_WORKER_POOL_UTILIZATION), DEFAULT_SMOOTHING_WEIGHT, DEFAULT_BALANCE_AFTER
        );

        return new QueueReader<QSTaskModel, TaskKit<Object>, Object>(
                taskIterable,
                new WorkerQueueItemHandler(queueingStrategy, taskService, logService, workerIdService, workers),
                workerExecutorService,
                0
        );
    }

}
