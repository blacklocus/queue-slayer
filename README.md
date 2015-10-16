(experimental...)

queue-slayer
============
A micro pattern for thread-parallelized processing of messages off of a queue.

[![Build Status](https://travis-ci.org/blacklocus/queue-slayer.svg?branch=passthru-wait-time-sqs)](https://travis-ci.org/blacklocus/queue-slayer)


## Usage ##

```gradle
repositories {
    mavenCentral()
}

dependencies {

    compile 'com.blacklocus.queue-slayer:qs-core:0.4.0'

    // for AWS SQS message providers
    compile 'com.blacklocus.queue-slayer:qs-aws:0.4.0'
}
```

other dependency syntax on [mvnrepository.com](http://mvnrepository.com/artifact/com.blacklocus.queue-slayer/qs-worker-core/0.4.0)

A quick look:
```java
// Reads in work items
MessageProvider provider = ...;

// Processes work items
MessageHandler handler = ...;

// Provides worker threads which process work items through the MessageHandler logic.
ExecutorService executor = ...;

// The orchestration of these components together.
MessageQueueReader reader = new MessageQueueReader(provider, handler, executor);

// The current thread will read in the items through the MessageProvider
// and pass them to the threads running the work items through the MessageHandler
// logic. The worker threads themselves are provided by the ExecutorService.
reader.run();
```


## License ##

Copyright 2013-2015 BlackLocus under [the Apache 2.0 license](LICENSE)
