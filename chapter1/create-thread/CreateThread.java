/**
 * @author : PARADISE
 * @ClassName : CreateThread
 * @description : how to create and use thread
 * @since : 2023/11/7 0:00
 */
public class CreateThread {

    public static void main(String[] args) {

    }

    static class ThreadChild extends Thread {
        @Override
        public void run() {
            var words = String.format("i'm Class 'Thread''s child, my name is %s", Thread.currentThread().getName());
            System.out.println(words);
        }
    }

    static class CallableChild extends Thread {
        @Override
        public void run() {
            var words = String.format("i'm Interface 'Callable''s child, my name is %s", Thread.currentThread().getName());
            System.out.println(words);
        }
    }

    static class RunnableChild implements Runnable {
        @Override
        public void run() {
            var words = String.format("i'm Interface 'Runnable''s child, my name is %s", Thread.currentThread().getName());
            System.out.println(words);
        }
    }
}
