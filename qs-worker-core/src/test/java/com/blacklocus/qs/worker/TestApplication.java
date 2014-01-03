package com.blacklocus.qs.worker;

import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.simple.BlockingQueueQSTaskService;
import com.blacklocus.qs.worker.simple.SystemOutQSLogService;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class TestApplication {

    public static void main(String[] args) {
        BlockingQueue<QSTaskModel> numbersMan = new SynchronousQueue<QSTaskModel>();

        // simulated work
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new TaskGenerator(numbersMan, TestWorkerPrintIdentity.HANDLER_NAME));
        executorService.submit(new TaskGenerator(numbersMan, TestWorkerPrintSquare.HANDLER_NAME));
        executorService.submit(new TaskGenerator(numbersMan, TestWorkerPrintZero.HANDLER_NAME));

        // actual qs-worker API
        QSDriver qsDriver = new QSDriver(new BlockingQueueQSTaskService(numbersMan), new SystemOutQSLogService());
        qsDriver.register(TestWorkerPrintIdentity.HANDLER_NAME, new TestWorkerPrintIdentity());
        qsDriver.register(TestWorkerPrintSquare.HANDLER_NAME, new TestWorkerPrintSquare());
        qsDriver.register(TestWorkerPrintZero.HANDLER_NAME, new TestWorkerPrintZero());
        qsDriver.run();
    }

}

class TaskGenerator implements Callable<Void> {
    final BlockingQueue<QSTaskModel> q;
    final String handler;

    TaskGenerator(BlockingQueue<QSTaskModel> q, String handler) {
        this.q = q;
        this.handler = handler;
    }

    @Override
    public Void call() throws Exception {
        while (!Thread.interrupted()) {
            q.put(new QSTaskModel(null, "" + System.currentTimeMillis(), handler, ImmutableMap.of("value", 2)));
            Thread.sleep(1000L);
        }
        return null;
    }
}

class TestWorkerPrintIdentity implements QSWorker {

    public static final String HANDLER_NAME = "identity";

    @Override
    public Object undertake(Configuration params, QSTaskLogger taskLogger) {
        System.out.println(params.getInt("value"));
        return null;
    }
}

class TestWorkerPrintSquare implements QSWorker {

    public static final String HANDLER_NAME = "square";

    @Override
    public Object undertake(Configuration params, QSTaskLogger taskLogger) {
        System.out.println(Math.pow((double) params.getInt("value"), 2));
        return null;
    }
}

class TestWorkerPrintZero implements QSWorker {

    public static final String HANDLER_NAME = "zero";

    @Override
    public Object undertake(Configuration params, QSTaskLogger taskLogger) {
        System.out.println(0);
        return null;
    }
}