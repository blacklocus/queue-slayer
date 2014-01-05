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
