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
package com.blacklocus.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class Runnables {

    public static Runnable newLoggingRunnable(final Runnable runnable) {
        return new Runnable() {
            final Logger LOG = LoggerFactory.getLogger(runnable.getClass());

            @Override
            public final void run() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    LOG.error(toString(), t);
                }
            }
        };
    }

    public static Runnable newInfiniteLoggingRunnable(final Runnable runnable) {
        return new Runnable() {
            final Logger LOG = LoggerFactory.getLogger(runnable.getClass());

            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        runnable.run();
                    } catch (Throwable t) {
                        LOG.error("Runnable excepted. Restarting it." + t);
                    }
                }
            }
        };
    }
}
