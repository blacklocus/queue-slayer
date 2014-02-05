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
package com.blacklocus.qs.worker.util.log;

import com.blacklocus.qs.worker.QSLogService;
import com.blacklocus.qs.worker.model.QSLogModel;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.google.common.base.Predicate;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SamplingQSLogServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(SamplingQSLogServiceTest.class);

    @Test
    public void testSampledLifeCycle() throws InterruptedException {

        // With these parameters, by far most logger interactions should be filtered out, very few sampled in.
        final int numThreads = 64, iterations = 200, processingJitterMaxMs = 16, noSoonerThanMs = 100;

        final Set<String> sampledTaskIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

        final QSLogService logService = Mockito.mock(QSLogService.class);
        // track which logging interactions were allowed through (sampled in)
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                sampledTaskIds.add(((QSTaskModel)invocation.getArguments()[0]).taskId);
                return null;
            }
        }).when(logService).startedTask(Matchers.any(QSTaskModel.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                sampledTaskIds.add(((QSLogModel)invocation.getArguments()[0]).taskId);
                return null; //TODO jason
            }
        }).when(logService).log(Matchers.any(QSLogModel.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                sampledTaskIds.add(((QSTaskModel)invocation.getArguments()[0]).taskId);
                return null; //TODO jason
            }
        }).when(logService).completedTask(Matchers.any(QSTaskModel.class));

        Predicate<QSTaskModel> taskPredicate = SamplingPredicates.noSoonerThan(noSoonerThanMs, TimeUnit.MILLISECONDS);
        final QSLogService sampledLogService = new SamplingQSLogService(logService, taskPredicate);


        long startNs = System.nanoTime();
        ExecutorService threads = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            threads.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    LOG.debug("Thread start {}", Thread.currentThread().getName());
                    for (int i = 0; i < iterations; i++) {

                        String taskId = UUID.randomUUID().toString();

                        // simulate task processing, some have logs, some don't, processing time varies between each step
                        QSTaskModel task = new QSTaskModel();
                        task.taskId = taskId;
                        Thread.sleep(RandomUtils.nextInt(processingJitterMaxMs));

                        sampledLogService.startedTask(task);
                        Thread.sleep(RandomUtils.nextInt(processingJitterMaxMs));

                        // random number of associated logs [0, 2]
                        for (int j = RandomUtils.nextInt(2); j > 0; j--) {
                            QSLogModel log = new QSLogModel();
                            log.taskId = taskId;
                            sampledLogService.log(log);
                            Thread.sleep(RandomUtils.nextInt(processingJitterMaxMs));
                        }

                        sampledLogService.completedTask(task);
                    }
                    LOG.debug("Thread end {}", Thread.currentThread().getName());
                    return null;

                }
            });
        }
        threads.shutdown();
        threads.awaitTermination(1, TimeUnit.MINUTES);
        long endNs = System.nanoTime();

        // Theoretical maximum number of sampled in task logging
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endNs - startNs);
        long expectedMax = durationMs / noSoonerThanMs + 1; // +1 for time@0: sampled in
        LOG.debug("Run duration: {}ms  no sooner than: {}ms", durationMs, noSoonerThanMs);
        LOG.debug("Expected max sampled in: {}  Actually sampled: {}", expectedMax, sampledTaskIds.size());
        Assert.assertTrue(expectedMax >= sampledTaskIds.size());
    }
}
