package com.blacklocus.qs;

import com.blacklocus.misc.ExceptingRunnable;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * A more generalized version of the {@link MessageQueueReader}
 *
 * @param <Q> queue item type
 * @param <T> the type of queue item that this reader can convert
 * @param <R> the result of processing the converted message
 * @author Jason Dunkelberger (dirkraft)
 */
public class QueueReader<Q, T, R> extends ExceptingRunnable {

    private static final Logger LOG = LoggerFactory.getLogger(QueueReader.class);

    /**
     * default sleep time in ms between idle queue reads
     */
    public static final long DEFAULT_SLEEP_MS = 20 * 1000;

    protected QueueItemProvider<Q> queueItemProvider;
    protected QueueItemHandler<Q, T, R> handler;
    protected ExecutorService executor;
    protected long sleepMs;

    /**
     * Construct a new MessageQueueReader with the default idle timeout.
     *
     * @param provider provider to endlessly pull messages from
     * @param handler  handler implementation to convert and process messages
     * @param executor executor service used for forking message handler processing
     */
    public QueueReader(QueueItemProvider<Q> provider,
                       QueueItemHandler<Q, T, R> handler,
                       ExecutorService executor) {
        this(provider, handler, executor, DEFAULT_SLEEP_MS);
    }

    /**
     * Construct a new MessageQueueReader with the given timeout.
     *
     * @param provider provider to endlessly pull messages from
     * @param handler  handler implementation to convert and process messages
     * @param executor executor service used for forking message handler processing
     * @param sleepMs  how long to sleep in ms between reads from the queue where no messages are returned
     */
    public QueueReader(QueueItemProvider<Q> provider,
                       QueueItemHandler<Q, T, R> handler,
                       ExecutorService executor,
                       long sleepMs) {
        this.queueItemProvider = provider;
        this.handler = handler;
        this.executor = executor;
        this.sleepMs = sleepMs;
    }

    @Override
    public void go() throws Exception {
        for (Collection<Q> queueItems : queueItemProvider) {
            try {
                if (queueItems.size() > 0) {
                    for (final Q queueItem : queueItems) {
                        handler.withFuture(queueItem, executor.submit(new Callable<Pair<Q, R>>() {
                            public Pair<Q, R> call() throws Exception {
                                try {
                                    T converted = handler.convert(queueItem);
                                    R result = handler.process(converted);
                                    handler.onSuccess(queueItem, result);
                                    return Pair.of(queueItem, result);
                                } catch (Throwable t) {
                                    LOG.error("An error occurred while processing item {}", queueItem, t);
                                    handler.onError(queueItem, t);
                                    throw new RuntimeException(t);
                                } finally {
                                    handler.onComplete(queueItem);
                                }
                            }
                        }));
                    }
                } else {
                    LOG.debug("No items available... sleeping for {} ms", sleepMs);
                    Thread.sleep(sleepMs);
                }
            } catch (InterruptedException e) {
                LOG.error("Reader thread interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Throwable t) {
                LOG.error("Runtime error in reader thread", t);
            }
        }
    }
}
