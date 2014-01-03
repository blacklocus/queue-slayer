package com.blacklocus.qs.worker.config;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSConfig {

    public static final int WORKER_POOL_CORE = 0;
    public static final int WORKER_POOL_MAX = 1000;
    public static final float WORKER_POOL_UTILIZATION = 0.9f;

    public static final double HEAP_STRATEGY_TRIGGER = 0.95;
    public static final long HEAP_STRATEGY_MAX_DELAY = 60 * 1000;
    public static final long HEAP_STRATEGY_HINT = Runtime.getRuntime().maxMemory() / (10 * 1024 * 1024);

}
