(incubating...)

queue-slayer
============
A micro pattern for thread-parallelized processing of messages off of a queue.



## Usage ##

    repositories {
        mavenCentral()
    }

    dependencies {

        compile 'com.blacklocus:qs-core:0.1'

        // for AWS SQS message providers
        compile 'com.blacklocus:qs-aws:0.1'
    }


## License ##

Copyright 2013 BlackLocus under [the Apache 2.0 license](LICENSE)
