(incubating...)

queue-slayer
============
A micro pattern for thread-parallelized processing of messages off of a queue.



## Usage ##

    repositories {
        mavenCentral()
    }

    dependencies {

        compile 'com.blacklocus.queue-slayer:qs-core:0.1.3'

        // for AWS SQS message providers
        compile 'com.blacklocus.queue-slayer:qs-aws:0.1.3'

    }

### Java API ###
TODO



queue-slayer-worker
===================
Task processing and tracking.


## Usage ##

    repositories {
        mavenCentral()
    }

    dependencies {

        compile 'com.blacklocus.queue-slayer:qs-worker-core:0.1.3'

        // for AWS implementations (SQS-based tasks)
        compile 'com.blacklocus.queue-slayer:qs-worker-aws:0.1.3'

        // for ES implementations (ES-based logging)
        compile 'com.blacklocus.queue-slayer:qs-worker-elasticsearch:0.1.3'

    }


### Java API ###
Here's an entirely self-contained example that mocks a task service which generates tasks to calculate the lengths of
randomized strings. Tasks are passed through a BlockingQueue (since we're generating them within the machine), and
logged to standard output.

    package com.blacklocus.magnus;

    import com.blacklocus.qs.worker.QSTaskLogger;
    import com.blacklocus.qs.worker.QSWorker;
    import com.blacklocus.qs.worker.QSWorkerBuilder;
    import com.blacklocus.qs.worker.model.QSTaskModel;
    import com.blacklocus.qs.worker.simple.BlockingQueueQSTaskService;
    import com.blacklocus.qs.worker.simple.HostNameQSWorkerIdService;
    import com.blacklocus.qs.worker.simple.SystemOutQSLogService;
    import com.blacklocus.utility.ExceptingRunnable;
    import com.fasterxml.jackson.core.type.TypeReference;
    import com.google.common.collect.ImmutableMap;
    import org.apache.commons.lang.math.RandomUtils;
    import org.apache.commons.lang3.RandomStringUtils;

    import java.util.Map;
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
                    while(true) {
                        workQueue.put(new QSTaskModel(null, "" + MyIdUtils.nextId(), "stdout", 1, ImmutableMap.of(
                                "message", RandomStringUtils.randomAscii(RandomUtils.nextInt(32))
                        )));
                    }
                }
            }).start();

            // All this worker does is log an extra message describing the length of the "message" param.
            QSWorker<Map<String, String>> worker = new QSWorker<Map<String, String>>() {
                @Override
                public String getHandlerName() {
                    // This identifies the type of task this worker can handle. In our task generator above, the
                    // tasks are created with the same handler identifier "stdout".
                    return "stdout";
                }

                @Override
                public TypeReference<Map<String, String>> getTypeReference() {
                    return new TypeReference<Map<String, String>>() {};
                }

                @Override
                public String undertake(Map<String, String> params, QSTaskLogger taskLogger) throws Exception {
                    String msg = params.get("message");
                    taskLogger.log(msg + " is " + msg.length() + " characters long");
                    return null;
                }
            };


            new QSWorkerBuilder()

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

    }



## License ##

Copyright 2013 BlackLocus under [the Apache 2.0 license](LICENSE)
