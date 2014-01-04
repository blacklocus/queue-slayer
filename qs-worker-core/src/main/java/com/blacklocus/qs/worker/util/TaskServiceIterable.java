package com.blacklocus.qs.worker.util;

import com.blacklocus.qs.QueueItemProvider;
import com.blacklocus.qs.worker.QSTaskService;
import com.blacklocus.qs.worker.model.QSTaskModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
class TaskServiceIterable implements QueueItemProvider<QSTaskModel> {

    private final QSTaskService taskService;

    TaskServiceIterable(QSTaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public Iterator<Collection<QSTaskModel>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Collection<QSTaskModel> next() {
        return Collections.singleton(taskService.getAvailableTask());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
