package com.blacklocus.qs.worker.simple;

import com.blacklocus.qs.worker.QSTaskService;
import com.blacklocus.qs.worker.model.QSTaskModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class BlockingQueueQSTaskService implements QSTaskService {

    private final BlockingQueue<QSTaskModel> queue;

    public BlockingQueueQSTaskService(BlockingQueue<QSTaskModel> queue) {
        this.queue = queue;
    }

    @Override
    public QSTaskModel getAvailableTask() {
        try {
            return queue.poll(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetTask(QSTaskModel task) {
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commitTask(QSTaskModel task) {
        // It's already been removed.
    }
}
