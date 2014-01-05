package com.blacklocus.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class InfiniteRunnable implements Runnable {

    private final Logger LOG;

    private final Runnable runnable;

    public InfiniteRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.LOG = LoggerFactory.getLogger(runnable.getClass());
    }

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
}
