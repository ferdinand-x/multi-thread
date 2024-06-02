package chapter1.chapter1.cancel;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author PARADISE
 */
public class CancelTest2 {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final ThreadPoolExecutor TASK_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new CustomRejectionHandler());

    static {
        TASK_EXECUTOR.allowCoreThreadTimeOut(true);
    }

    public static void main(String[] args) {
        // Create 300 sleep man tasks
        var sleepMans = CancelTest.sleepManList();

        var t1 = System.currentTimeMillis();
        var subSleepManList = Lists.partition(sleepMans, CORE_POOL_SIZE);
        var results = Lists.newArrayList();
        for (var subList : subSleepManList) {
            // Execute sleep tasks and collect futures
            var subMap = subList.stream()
                    .collect(Collectors.toMap(Function.identity(), v -> CancelTest.execute(v, TASK_EXECUTOR)));

            // Get results
            var subResults = subMap.entrySet()
                    .stream()
                    .map(CancelTest::calc)
                    .collect(Collectors.toList());
            results.addAll(subResults);
        }
        var t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
        System.out.println(results);

        // Shutdown the executor service
        TASK_EXECUTOR.shutdown();
    }

    private static class CustomRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                // Wait for space to become available and retry adding the task to the queue
                executor.getQueue().offer(r, 100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
