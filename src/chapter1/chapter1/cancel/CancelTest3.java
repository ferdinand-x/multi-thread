package chapter1.chapter1.cancel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author : PARADISE
 * @ClassName : CancelTest3
 * @description : test cancel Thread task-3
 * @since : 2024/6/2 22:29
 */
public class CancelTest3 {

    private static final ExecutorService TASK_EXECUTOR = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        // 1. create 300 sleep man
        var sleepMans = CancelTest.sleepManList();
        // 2. execute sleep-task
        var t1 = System.currentTimeMillis();
        var sleepManMap = sleepMans.stream()
                .collect(Collectors.toMap(Function.identity(), v -> CancelTest.execute(v, TASK_EXECUTOR)));
        // 3. get results
        var results = sleepManMap.entrySet()
                .stream()
                .map(CancelTest::calc)
                .collect(Collectors.toList());
        var t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
        System.out.println(results);
    }

    public static SleepMan calc(Map.Entry<SleepMan, Future<SleepMan>> entry) {
        var key = entry.getKey();
        var val = entry.getValue();
        try {
            return val.get(3000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            key.setHasError(true);
            key.setDetails(String.format("get-result-interrupted,current-thread-name=%s", Thread.currentThread().getName()));
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            key.setHasError(true);
            key.setDetails(String.format("get-result-execute-err,current-thread-name=%s", Thread.currentThread().getName()));
        } catch (CancellationException e) {
            key.setHasError(true);
            key.setDetails(String.format("get-result-cancelled,current-thread-name=%s", Thread.currentThread().getName()));
        } catch (TimeoutException e) {
            key.setHasError(true);
            key.setDetails(String.format("get-result-timeout,current-thread-name=%s", Thread.currentThread().getName()));
        }
        return key;
    }

    public static Future<SleepMan> execute(SleepMan sleepMan, ExecutorService taskExecutor) {
        Supplier<SleepMan> supplier = () -> {
            try {
                var t1 = System.currentTimeMillis();
                sleepMan.setThreadName(Thread.currentThread().getName());
                TimeUnit.MILLISECONDS.sleep(sleepMan.getExpectSleepTime());
                var t2 = System.currentTimeMillis();
                sleepMan.setActualSleepTime((int) (t2 - t1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sleepMan.setHasError(true);
                sleepMan.setDetails(String.format("execute-task-interrupted,current-thread-name=%s", Thread.currentThread().getName()));
            }
            return sleepMan;
        };
        return CompletableFuture.supplyAsync(supplier, taskExecutor);
    }

}
