# The Queue Slayer Streaming Framework

## Overview

The Queue Slayer streaming code defines a framework for reading records from an input stream, 
transforming the records, and writing the transformed records to an output stream.

The input stream can be an SQS queue, Kinesis stream, or any other input
source for which there's a com.blacklocus.qs.Reader<T1, T2>. T1 is the type of record
read by the Reader and T2 is the type of record writen by a com.blacklocus.qs.Writer<T2>.
The Writer's job is to write the T2 record to an output stream, which can be an SNS topic, SQS queue, Kinesis stream, 
the console, or any stream for which there's a com.blacklocus.qs.Writer<T2>.
The Reader uses a transform function (a Guava Function<Interable<T1>, Iterable<T2>>) to transform the 
records of type T1 to records of type T2.

## Writing and Running a Stream Transform 

Transforming data involves writing a Guava transform Function<Interable<T1>, Iterable<T2>> and then configuring
an instance of the com.blacklocus.qs.drivers.DriverApp with a Reader, your transform, and a Writer. The examples below
show how the configuration is done using system properties. The DriverApp assumes that the Reader produces
an Iterable<String> that the transform will convert into another Iterable<String> (not necessarily the same size)
that a writer will write to its output stream.

## Examples

To run the examples, you'll need to first create a few AWS resources:
 
* Create a SNS topic named TestTopic1. Make note of the topic's ARN (Amazon Resource Name) and use it
in the -D options below.
* Create a SQS queue named TestQueue1 and subscribe it to the SNS TestTopic1.
* Create a SQS queue named TestQueue2.
* Create a Kinesis Stream named TestStream1.

Now you're ready to run the example applications:

Run the ExampleQueueWriterApp to write a set of test records to the TestQueue2 SQS queue:

<blockquote>
java \<br>
  -Dbl.output.queue.name=TestQueue2 \<br>
  -classpath "..." \<br>
  com.blacklocus.qs.aws.sqs.writers.ExampleQueueWriterApp
</blockquote>

You should see output on your console similar to:

<blockquote>
Writing 'msg 412' to 'TestQueue2'...<br>
Writing 'msg 413' to 'TestQueue2'...<br>
Writing 'msg 414' to 'TestQueue2'...<br>
Writing 'msg 415' to 'TestQueue2'...<br>
...
</blockquote>

Now run the com.blacklocus.qs.drivers.DriverApp to read records from the TestQueue2 SQS queue, transform them to uppercase, and write
the results to the TestStream1 Kinesis stream:

<blockquote>
java \<br>
  -Dbl.reader.factory.class.name=com.blacklocus.qs.aws.sqs.readers.QueueReaderFactory \<br>
  -Dbl.input.queue.url=TestQueue2 \<br>
  -Dbl.transform.class.name=com.blacklocus.qs.transforms.ExampleTransform \<br>
  -Dbl.writer.factory.class.name=com.blacklocus.qs.aws.kinesis.writers.StreamWriterFactory \<br>
  -Dbl.output.stream.name=TestStream1 \<br>
  -classpath "..." \<br>
  com.blacklocus.qs.drivers.DriverApp
</blockquote>

You should see output on your console similar to:

<blockquote>
Writing 'MSG 375' to TestStream1<br>
Writing 'MSG 376' to TestStream1<br>
Writing 'MSG 377' to TestStream1<br>
Writing 'MSG 378' to TestStream1<br>
...
</blockquote>

Note that the ExampleTransform has transformed the lowercase "msg 123" to uppercase "MSG 123".

Run the DriverApp to read records from the TestStream1 Kinesis stream and write them to the console:

<blockquote>
java \<br>
  -Dbl.reader.factory.class.name=com.blacklocus.qs.aws.kinesis.readers.StreamReaderFactory \<br>
  -Dbl.app.name=StreamToConsole \<br>
  -Dbl.input.stream.name=TestStream1 \<br>
  -Dbl.transform.class.name=com.blacklocus.qs.transforms.ExampleTransform \<br>
  -Dbl.writer.class.name=com.blacklocus.qs.writers.ConsoleWriter \<br>
  -classpath "..." \<br>
  com.blacklocus.qs.drivers.DriverApp
</blockquote>

You should see output on your console similar to:

<blockquote>
MSG 402<br>
MSG 404<br>
MSG 405<br>
MSG 406<br>
...
</blockquote>

Run the DriverApp to read records from the TestStream1 Kinesis stream and write them to the TestTopic1 SNS topic.
If you subscribed the TestQueue1 SQS queue to the TestTopic1, the messages should appear in the TestQueue1 queue.

<blockquote>
java \<br>
  -Dbl.reader.factory.class.name=com.blacklocus.qs.aws.kinesis.readers.StreamReaderFactory \<br>
  -Dbl.app.name=StreamToTopic \<br>
  -Dbl.input.stream.name=TestStream1 \<br>
  -Dbl.transform.class.name=com.blacklocus.qs.transforms.ExampleTransform \<br>
  -Dbl.writer.factory.class.name=com.blacklocus.qs.aws.sns.writers.TopicWriterFactory \<br>
  -Dbl.output.topic.arn=arn:aws:sns:us-east-1:493847008801:TestTopic1 \<br>
  -classpath "..." \<br>
  com.blacklocus.qs.drivers.DriverApp
</blockquote>

You should see output on your console similar to:

<blockquote>
Writing 'MSG 410' to arn:aws:sns:us-east-1:493847008801:TestTopic1<br>
Writing 'MSG 411' to arn:aws:sns:us-east-1:493847008801:TestTopic1<br>
Writing 'MSG 412' to arn:aws:sns:us-east-1:493847008801:TestTopic1<br>
Writing 'MSG 414' to arn:aws:sns:us-east-1:493847008801:TestTopic1<br>
...
</blockquote>

Run the DriverApp to read records from the TestQueue1 SQS queue and write them to the console:

<blockquote>
java \<br>
  -Dbl.reader.factory.class.name=com.blacklocus.qs.aws.sqs.readers.QueueReaderFactory \<br>
  -Dbl.input.queue.url=TestQueue1 \<br>
  -Dbl.transform.class.name=com.blacklocus.qs.aws.sns.transforms.NotificationToMessage \<br>
  -Dbl.writer.class.name=com.blacklocus.qs.writers.ConsoleWriter \<br>
  -classpath "..." \<br>
  com.blacklocus.qs.drivers.DriverApp
</blockquote>

You should see output on your console similar to:

<blockquote>
MSG 402<br>
MSG 404<br>
MSG 405<br>
MSG 406<br>
...
</blockquote>