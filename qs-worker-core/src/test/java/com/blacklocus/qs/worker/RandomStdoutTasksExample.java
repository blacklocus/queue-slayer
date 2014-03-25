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
package com.blacklocus.qs.worker;

import com.blacklocus.misc.ExceptingRunnable;
import com.blacklocus.qs.worker.api.QSWorker;
import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.simple.BlockingQueueQSTaskService;
import com.blacklocus.qs.worker.simple.HostNameQSWorkerIdService;
import com.blacklocus.qs.worker.simple.SystemOutQSLogService;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class RandomStdoutTasksExample {

    public static void main(String[] args) {
        // Mock our source of tasks.
        final BlockingQueue<QSTaskModel> workQueue = new SynchronousQueue<QSTaskModel>();

        // Generates tasks
        new Thread(new ExceptingRunnable() {
            @Override
            protected void go() throws Exception {
                while (true) {
                    workQueue.put(new QSTaskModel(null, "" + RandomUtils.nextInt(), "stdout", 1,
                            new Params(RandomStringUtils.randomAscii(RandomUtils.nextInt(32))
                    )));
                }
            }
        }).start();

        // All this worker does is log an extra message describing the length of the "message" param.
        QSWorker<Params> worker = new AbstractQSWorker<Params>() {
            @Override
            public String getHandlerName() {
                // This identifies the type of task this worker can handle. In our task generator above, the
                // tasks are created with the same handler identifier "stdout".
                return "stdout";
            }

            @Override
            public TaskKit<Params> convert(TaskKitFactory<Params> factory) throws Exception {
                return factory.newTaskKit(Params.class);
            }

            @Override
            public Object process(TaskKit<Params> kit) throws Exception {
                String msg = kit.params().message;
                kit.log(msg + " is " + msg.length() + " characters long");
                return null;
            }
        };


        QSAssembly.newBuilder()

                // The source of work.
                .taskServices(new BlockingQueueQSTaskService(workQueue))

                // Logging service which records task start, task-specific logging, task end.
                .logService(new SystemOutQSLogService())

                // Service that helps identify the machine completing tasks, this machine.
                .workerIdService(new HostNameQSWorkerIdService())

                // The worker logic observed by this instance.
                .workers(worker)

                // Run it in the current thread.
                .build().run();
    }

    static class Params {
        String message;

        Params() {
        }

        Params(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}