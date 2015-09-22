package com.blacklocus.qs;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class QueueReaderTest {

    private static final Logger LOG = LoggerFactory.getLogger(QueueReaderTest.class);

    @Test
    public void testQueueReaderBasic() throws InterruptedException {
        final List<String> successes = new ArrayList<>();
        final List<String> errors = new ArrayList<>();
        final List<String> completes = new ArrayList<>();

        List<String> rawMsgs = Lists.newArrayList("one", "two", "four", "X");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        QueueReader<String, ParsedString, Result> qr = new QueueReader<>(
                new OneAtATimeQueueItemProvider<>(rawMsgs.iterator()),
                new QueueItemHandler<String, ParsedString, Result>() {
                    @Override
                    public ParsedString convert(String queueItem) throws Exception {
                        return new ParsedString(queueItem.charAt(1));
                    }

                    @Override
                    public Result process(ParsedString convertedQueueItem) throws Exception {
                        return new Result(convertedQueueItem);
                    }

                    @Override
                    public void onSuccess(String queueItem, ParsedString convertedQueueItem, Result result) {
                        LOG.info("success {}", result);
                        successes.add(queueItem);
                    }

                    @Override
                    public void onError(String queueItem, ParsedString convertedQueueItem, Throwable throwable) {
                        LOG.info("error {}", throwable.getMessage());
                        errors.add(queueItem);
                    }

                    @Override
                    public void onComplete(String queueItem, ParsedString convertedQueueItem, Result result) {
                        completes.add(queueItem);
                    }

                    @Override
                    public void withFuture(String queueItem, Future<Pair<String, Result>> future) {
                    }
                },
                executor
        );
        qr.run();

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        Assert.assertEquals(Arrays.asList("one", "two", "four"), successes);
        Assert.assertEquals(Collections.singletonList("X"), errors);
        Assert.assertEquals(rawMsgs, completes);
    }


    class ParsedString {
        char secondLetter;

        public ParsedString(char secondLetter) {
            this.secondLetter = secondLetter;
        }
    }

    class Result {
        ParsedString parsed;

        public Result(ParsedString parsed) {
            this.parsed = parsed;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "parsed=" + parsed.secondLetter +
                    '}';
        }
    }
}
