package chapter1.chapter1.create_thread;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author : PARADISE
 * @ClassName : chapter1.chapter1.create_thread.CreateThread
 * @description : how to create and use thread
 * @since : 2023/11/7 0:00
 */
public class CreateThread {

    private static final ExecutorService TASK_EXECUTOR = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 1. create task
        // 1.1 use the thread child
//        ThreadChild thread1 = new ThreadChild("thread-child");
//        // 1.2 use the runnable child
//        RunnableChild runnableChild = new RunnableChild();
//        Thread thread2 = new Thread(runnableChild, "runnable-child");
//        // 1.3 use the callable child <RunnableFuture extends Runnable>
//        CallableChild callableChild = new CallableChild();
//        FutureTask<String> futureTask = new FutureTask<>(callableChild);
//        Thread thread3 = new Thread(futureTask, "callable-child");
//
//        // 2. start thread
//        System.out.printf("[%s]-Main thread start...%n", Thread.currentThread().getName());
//        thread1.start();
//        thread2.start();
//        thread3.start();

        // 3. future call get result. let me echo it.
//        String futureRes = futureTask.get();
//        System.out.printf("Future task result:%s%n", futureRes);
        var futures = IntStream.range(0, 10).boxed().map(CreateThread::task).toArray(CompletableFuture[]::new);
//        var bigFuture = CompletableFuture.allOf(futures);
//        bigFuture.join();

        var start = System.currentTimeMillis();
        var results = Arrays.stream(futures)
                .map(CreateThread::getResult)
                .collect(Collectors.toList());
        System.out.println(results);
        var end = System.currentTimeMillis();
        var duration = end-start;
        System.out.println(duration);
        TASK_EXECUTOR.shutdown();
    }

    private static Integer getResult(CompletableFuture<Integer> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ignore) {
            return null;
        }
    }

    private static CompletableFuture<Integer> task(Integer i) {
        Supplier<Integer> supplier = () -> {
            if (i % 2 == 0) {
                try {
                    System.out.println(Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ignore) {
                }
            }
            return i;
        };
        return CompletableFuture.supplyAsync(supplier, TASK_EXECUTOR)
                .orTimeout(1, TimeUnit.SECONDS)
                .exceptionally(ex -> null);
    }

    static class ThreadChild extends Thread {

        public ThreadChild(String name) {
            super(name);
        }

        @Override
        public void run() {
            var words = String.format("1.i'm Class 'Thread''s child, my name is [%s]", Thread.currentThread().getName());
            System.out.println(words);
        }
    }

    static class RunnableChild implements Runnable {
        @Override
        public void run() {
            var words = String.format("2.i'm Interface 'Runnable''s child, my name is [%s]", Thread.currentThread().getName());
            System.out.println(words);
        }
    }

    static class CallableChild implements Callable<String> {

        @Override
        public String call() throws Exception {
            var words = String.format("3.i'm Interface 'Callable''s child, my name is [%s]", Thread.currentThread().getName());
            System.out.println(words);
            return words;
        }
    }

}
