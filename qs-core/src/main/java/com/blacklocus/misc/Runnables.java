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
