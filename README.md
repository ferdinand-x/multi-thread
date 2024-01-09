#   multi-thread
Java Concurrent Programming

###  chapter1 How to use multi-thread    [chapter1](src/chapter1)
* chapter1 How to use multi-thread    [1.1](src/chapter1/chapter1/create_thread)
    * create Thread and start with thread
* thread-api    [1.2](src/chapter1/chapter1/thread_api)
* thread state    [1.3](src/chapter1/chapter1/thread_state)
  * NEW -> Thread state for a thread which has not yet started.
  * RUNNABLE ->  after Thread.start(), become a runnable thread(but it may be waiting for operating system resources such as processor).
  * BLOCKED -> Thread state for a thread blocked waiting for a monitor lock,synchronize block/method.
  * WAITING -> A thread in the waiting state is waiting for another thread to perform a particular action.
    * Object.wait with no timeout -> wait for Object.notify() or Object.notifyAll()
    * Thread.join with no timeout -> wait for another specified thread terminate
    * LockSupport.park
  * TIMED_WAITING -> Thread state for a waiting thread with a specified waiting time.
    * Thread.sleep
    * Object.wait with timeout
    * Thread.join with timeout
    * LockSupport.parkNanos
    * LockSupport.parkUntil
  * TERMINATED -> A terminated thread,the thread has completed execution.

###  chapter2 Locks and keyword "synchronize"    [chapter2](src/chapter2)
* Locks
* synchronize
* synchronize

###  chapter3 FutureTask and Callable and Runnable    [chapter3](src/chapter3)

###  chapter4 ThreadPool    [chapter4](src/chapter4)
* diff Executors and Params
* ForkJoinPool

###  chapter5 BlockingQueue    [chapter5](src/chapter5)

###  chapter6 CountDownLatch and Atomic-type    [chapter6](src/chapter6)

###  chapter7 TimerTask    [chapter7](src/chapter7)

###  chapter8 ThreadLocal and TransmittableThreadLocal(TTL)    [chapter8](src/chapter8)