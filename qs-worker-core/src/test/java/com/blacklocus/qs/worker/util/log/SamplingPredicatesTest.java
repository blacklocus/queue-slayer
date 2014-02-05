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

import com.blacklocus.qs.worker.model.QSTaskModel;
import com.google.common.base.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SamplingPredicatesTest {

    private static final Logger LOG = LoggerFactory.getLogger(SamplingPredicatesTest.class);

    @Test
    public void testEveryNthStepSingleThread() {

        int multiple = 7;

        final Predicate<QSTaskModel> p = SamplingPredicates.everyNthEntry(multiple);

        // single-threaded
        for (int i = 0; i < multiple * 100; i++) {
            // Not a multiple or the predicate says that it is. These are exclusive conditions, hence XOR.
            Assert.assertTrue(i % multiple != 0 ^ p.apply(new QSTaskModel()));
        }
    }

    @Test
    public void testEveryNthStepMultiThread() throws InterruptedException {

        int multiple = 13;

        final Predicate<QSTaskModel> p = SamplingPredicates.everyNthEntry(multiple);

        final int numThreads = 32, iterations = 1234567;
        final AtomicInteger numTrues = new AtomicInteger();

        ExecutorService threads = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            threads.submit(new Runnable() {
                @Override
                public void run() {
                    LOG.debug("Thread start {}", Thread.currentThread().getName());
                    for (int j = 0; j < iterations; j++) {
                        if (p.apply(new QSTaskModel())) {
                            numTrues.incrementAndGet();
                        }
                    }
                    LOG.debug("Thread end {}", Thread.currentThread().getName());
                }
            });
        }
        threads.shutdown();
        threads.awaitTermination(1, TimeUnit.MINUTES);

        final int expectedTrues = numThreads * iterations / multiple + 1; // +1 for 0: true
        Assert.assertEquals(expectedTrues, numTrues.get());

    }

    @Test
    public void testNoSoonerThanSingleThread() throws InterruptedException {

        int minDurationMs = 100;

        Predicate<QSTaskModel> p = SamplingPredicates.noSoonerThan(minDurationMs, TimeUnit.MILLISECONDS);

        // Thread sleep is inexact. Test upper bound given total runtime of the test.
        long start = System.nanoTime();
        int trueCount = 0;
        int sleepMs = minDurationMs / 20;
        for (int i = 0; i < 200; i++) {
            if (p.apply(new QSTaskModel())) {
                ++trueCount;
            }
            Thread.sleep(sleepMs);
        }
        long finish = System.nanoTime();

        // Assumption: maximum trues would be if there was a p.apply at exactly the minimum boundary. +1 for time@0: true
        long upperBound = TimeUnit.NANOSECONDS.toMillis(finish - start) / minDurationMs + 1;
        LOG.debug("Upper bound: {}  true count: {}", upperBound, trueCount);
        Assert.assertTrue(upperBound >= trueCount);

    }

    @Test
    public void testNoSoonerThanMultiThread() throws InterruptedException {

        int minDurationMs = 100;

        final Predicate<QSTaskModel> p = SamplingPredicates.noSoonerThan(minDurationMs, TimeUnit.MILLISECONDS);

        // Thread sleep is inexact. Test upper bound given total runtime of the test.
        long start = System.nanoTime();

        final int sleepMs = minDurationMs / 20;
        final int numThreads = 32, iterations = 200;
        final AtomicInteger numTrues = new AtomicInteger();

        ExecutorService threads = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            threads.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    LOG.debug("Thread start {}", Thread.currentThread().getName());
                    for (int i = 0; i < iterations; i++) {
                        if (p.apply(new QSTaskModel())) {
                            numTrues.incrementAndGet();
                        }
                        Thread.sleep(sleepMs);
                    }
                    LOG.debug("Thread end {}", Thread.currentThread().getName());
                    return null;
                }
            });
        }
        threads.shutdown();
        threads.awaitTermination(1, TimeUnit.MINUTES);

        long finish = System.nanoTime();


        // Assumption: maximum trues would be if there was a p.apply at exactly the minimum boundary. +1 for time@0: true
        long upperBound = TimeUnit.NANOSECONDS.toMillis(finish - start) / minDurationMs + 1;
        LOG.debug("Upper bound: {}  true count: {}", upperBound, numTrues.get());
        Assert.assertTrue(upperBound >= numTrues.get());

    }
}
