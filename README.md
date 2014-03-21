(incubating...)

queue-slayer
============
A micro pattern for thread-parallelized processing of messages off of a queue.



## Usage ##

    repositories {
        mavenCentral()
    }

    dependencies {

        compile 'com.blacklocus.queue-slayer:qs-core:0.3.0'

        // for AWS SQS message providers
        compile 'com.blacklocus.queue-slayer:qs-aws:0.3.0'

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

        compile 'com.blacklocus.queue-slayer:qs-worker-core:0.3.0'

        // for AWS implementations (SQS-based tasks)
        compile 'com.blacklocus.queue-slayer:qs-worker-aws:0.3.0'

        // for ES implementations (ES-based logging)
        compile 'com.blacklocus.queue-slayer:qs-worker-elasticsearch:0.3.0'

    }


### Java API ###
Here's an entirely self-contained example that mocks a task service which generates tasks to calculate the lengths of
randomized strings. Tasks are passed through a BlockingQueue (since we're generating them within the machine), and
logged to standard output.

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



## License ##

Copyright 2013 BlackLocus under [the Apache 2.0 license](LICENSE)
