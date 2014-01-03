package com.blacklocus.qs.worker;

import com.blacklocus.qs.worker.model.QSTaskModel;
import com.blacklocus.qs.worker.simple.BlockingQueueQSTaskService;
import com.blacklocus.qs.worker.simple.SystemOutQSLogService;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.math.RandomUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class TestApplication implements Runnable {

    BlockingQueue<QSTaskModel> numbersMan = new SynchronousQueue<QSTaskModel>();

    @Override
    public void run() {
        // simulated work
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new TaskGenerator());

        // actual qs-worker API
        QSDriver qsDriver = new QSDriver(new BlockingQueueQSTaskService(numbersMan), new SystemOutQSLogService());
        qsDriver.register(new TestWorkerPrintIdentity(), new TestWorkerPrintSquare(), new TestWorkerPrintZero(), new TestWorkerUnmotivated());
        qsDriver.run();
    }

    public static void main(String[] args) {
        new TestApplication().run();
    }

    class TaskGenerator implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            while (!Thread.interrupted()) {
                numbersMan.put(new QSTaskModel(null, "" + System.currentTimeMillis(), randomHandler(), ImmutableMap.of("value", 2)));
                Thread.sleep(1000L);
            }
            return null;
        }

        String[] WORKER_NAMES = {
                TestWorkerPrintIdentity.HANDLER_NAME,
                TestWorkerPrintSquare.HANDLER_NAME,
                TestWorkerPrintZero.HANDLER_NAME,
                TestWorkerUnmotivated.HANDLER_NAME
        };

        String randomHandler() {
            return WORKER_NAMES[RandomUtils.nextInt(WORKER_NAMES.length)];
        }
    }
}

class TestWorkerPrintIdentity implements QSWorker {

    public static final String HANDLER_NAME = "identity";

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }

    @Override
    public Object undertake(Configuration params, QSTaskLogger taskLogger) {
        System.out.println(params.getInt("value"));
        return null;
    }
}

class TestWorkerPrintSquare implements QSWorker {

    public static final String HANDLER_NAME = "square";

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }

    @Override
    public Object undertake(Configuration params, QSTaskLogger taskLogger) {
        System.out.println(Math.pow((double) params.getInt("value"), 2));
        return null;
    }
}

class TestWorkerPrintZero implements QSWorker {

    public static final String HANDLER_NAME = "zero";

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }

    @Override
    public Object undertake(Configuration params, QSTaskLogger taskLogger) {
        System.out.println(0);
        return null;
    }
}

class TestWorkerUnmotivated implements QSWorker {

    public static final String HANDLER_NAME = "unmotivated";

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }

    @Override
    public Object undertake(Configuration params, QSTaskLogger taskLogger) {
        taskLogger.log("This is dum.");
        return null;
    }
}