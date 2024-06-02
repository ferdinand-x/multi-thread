package chapter1.chapter1.cancel;

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
public class CancelTest {

    private static final ExecutorService TASK_EXECUTOR = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        // 1. create 300 sleep man
        var sleepMans = sleepManList();
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
            return val.get();
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
        return CompletableFuture.supplyAsync(supplier, taskExecutor)
                .orTimeout(3000, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> {
                    sleepMan.setHasError(true);
                    sleepMan.setDetails(String.format("execute-task-timeout,current-thread-name=%s", Thread.currentThread().getName()));
                    return sleepMan;
                });
    }

    public static List<SleepMan> sleepManList() {
        return IntStream.rangeClosed(1, 300)
                .boxed()
                .map(SleepMan::create)
                .collect(Collectors.toList());
    }
}
