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
package com.blacklocus.qs.worker.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

import java.util.HashMap;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSConfig {

    public static final Configuration DEFAULTS = new MapConfiguration(new HashMap<String, Object>());

    public static final String PROP_WORKER_POOL_CORE = "qs.workerPool.core";
    public static final int DEF_WORKER_POOL_CORE = 0;
    public static final String PROP_WORKER_POOL_MAX = "qs.workerPool.max";
    public static final int DEF_WORKER_POOL_MAX = 1000;
    public static final String PROP_WORKER_POOL_UTILIZATION = "qs.workerPool.utilization";
    public static final float DEF_WORKER_POOL_UTILIZATION = 0.9f;
    static {
        DEFAULTS.setProperty(PROP_WORKER_POOL_CORE, DEF_WORKER_POOL_CORE);
        DEFAULTS.setProperty(PROP_WORKER_POOL_MAX, DEF_WORKER_POOL_MAX);
        DEFAULTS.setProperty(PROP_WORKER_POOL_UTILIZATION, DEF_WORKER_POOL_UTILIZATION);
    }

    public static final String PROP_HEAP_STRATEGY_TRIGGER = "qs.heapStrategy.trigger";
    public static final double DEF_HEAP_STRATEGY_TRIGGER = 0.70;
    public static final String PROP_HEAP_STRATEGY_MAX_DELAY = "qs.heapStrategy.maxDelay";
    public static final long DEF_HEAP_STRATEGY_MAX_DELAY = 60 * 1000;
    public static final String PROP_HEAP_STRATEGY_HINT = "qs.heapStrategy.hint";
    // This default is motivated by an entirely presumptuous memory per worker thread of 5MiB.
    public static final long DEF_HEAP_STRATEGY_HINT = Runtime.getRuntime().maxMemory() / (5 * 1024 * 1024);
    static {
        DEFAULTS.setProperty(PROP_HEAP_STRATEGY_TRIGGER, DEF_HEAP_STRATEGY_TRIGGER);
        DEFAULTS.setProperty(PROP_HEAP_STRATEGY_MAX_DELAY, DEF_HEAP_STRATEGY_MAX_DELAY);
        DEFAULTS.setProperty(PROP_HEAP_STRATEGY_HINT, DEF_HEAP_STRATEGY_HINT);
    }
}
