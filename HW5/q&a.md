## 1. How to Create a Thread in Java — 4 Ways

### Way 1: Extend `Thread`

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        Thread t = new MyThread();
        t.start();
    }
}
```

One way is to extend the `Thread` class and override the `run()` method. Then we call `start()` to create a new thread. We should not call `run()` directly because that would execute the method in the current thread instead of creating a new one.

---

### Way 2: Implement `Runnable`

```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running: " + Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(new MyRunnable());
        t.start();
    }
}
```

A better way is to implement `Runnable` because Java supports single inheritance. By using `Runnable`, the task is separated from the thread object.

---

### Way 3: Use Lambda with `Runnable`

```java
public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("Lambda thread: " + Thread.currentThread().getName());
        });

        t.start();
    }
}
```
 
Since `Runnable` is a functional interface, we can create a thread using a lambda expression. This is cleaner for short tasks.

---

### Way 4: Use ExecutorService / Thread Pool

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> {
            System.out.println("Task running: " + Thread.currentThread().getName());
        });

        executor.shutdown();
    }
}
```

 In real projects, we usually use a thread pool instead of manually creating threads. Thread pools reuse existing threads, reduce thread creation overhead, and provide better resource management.

---

## 2. Thread Lifecycle

Main thread states:

```text
NEW -> RUNNABLE -> RUNNING -> BLOCKED / WAITING / TIMED_WAITING -> RUNNABLE -> TERMINATED
```

### States

| State           | Meaning                                                    |
| --------------- | ---------------------------------------------------------- |
| `NEW`           | Thread object is created but `start()` has not been called |
| `RUNNABLE`      | Thread is ready to run and waiting for CPU scheduling      |
| `RUNNING`       | Thread is currently executing                              |
| `BLOCKED`       | Waiting to acquire a monitor lock                          |
| `WAITING`       | Waiting indefinitely for another thread’s signal           |
| `TIMED_WAITING` | Waiting for a limited time                                 |
| `TERMINATED`    | Thread has finished execution                              |

A thread starts in the `NEW` state. After calling `start()`, it enters `RUNNABLE`. The scheduler picks it to run. If it tries to enter a synchronized block but the lock is held by another thread, it becomes `BLOCKED`. If it calls `wait()` or `join()`, it enters `WAITING`. If it calls `sleep()` or timed `join()`, it enters `TIMED_WAITING`. Once the `run()` method finishes, the thread becomes `TERMINATED`.

Example:

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000); // TIMED_WAITING
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(t.getState()); // NEW

        t.start();
        System.out.println(t.getState()); // RUNNABLE or TIMED_WAITING

        t.join();

        System.out.println(t.getState()); // TERMINATED
    }
}
```

---

## 3. How Does a Thread Pool Work?

A thread pool maintains a group of worker threads. When we submit a task, the pool either assigns it to an idle thread, creates a new thread if allowed, or puts the task into a queue. After a worker finishes a task, it does not die immediately. Instead, it goes back to the pool and waits for the next task.

Typical flow:

```text
Submit task
   ↓
Core thread available? -> execute
   ↓
No core thread?
   ↓
Put task into queue
   ↓
Queue full?
   ↓
Create non-core thread if max not reached
   ↓
Still cannot handle?
   ↓
Reject task
```

Example:

```java
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 5; i++) {
            int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " running on " 
                    + Thread.currentThread().getName());
            });
        }

        executor.shutdown();
    }
}
```

---

## 4. Problems with `newCachedThreadPool()` and `newFixedThreadPool()`

### `newCachedThreadPool()`

```java
ExecutorService executor = Executors.newCachedThreadPool();
```

`newCachedThreadPool()` can create an unbounded number of threads. If many tasks arrive quickly, it may create too many threads and cause high memory usage, context switching overhead, or even `OutOfMemoryError`.

Why?

```java
Executors.newCachedThreadPool();
// Internally:
// corePoolSize = 0
// maximumPoolSize = Integer.MAX_VALUE
// queue = SynchronousQueue
```

Problem:

```text
Too many tasks -> too many threads -> memory pressure -> system crash
```

---

### `newFixedThreadPool()`

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

`newFixedThreadPool()` has a fixed number of threads, but it uses an unbounded queue. If tasks are submitted faster than they are processed, the queue may grow indefinitely and cause memory issues.

Why?

```java
Executors.newFixedThreadPool(10);
// Internally:
// corePoolSize = 10
// maximumPoolSize = 10
// queue = LinkedBlockingQueue with Integer.MAX_VALUE capacity
```

Problem:

```text
Too many tasks -> huge queue -> memory pressure -> OutOfMemoryError
```

Better approach:

```java
import java.util.concurrent.*;

ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5,
    10,
    60,
    TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

In production, I prefer creating `ThreadPoolExecutor` manually because I can control core size, max size, queue capacity, keep-alive time, and rejection policy.

---

## 5. What Is `Future`?

`Future` represents the result of an asynchronous computation. When we submit a `Callable` task to an executor, we get a `Future`. We can use it to check whether the task is done, cancel the task, or get the result.

Example:

```java
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(1000);
            return 100;
        });

        System.out.println("Doing other work...");

        Integer result = future.get(); // blocks until result is ready
        System.out.println(result);

        executor.shutdown();
    }
}
```

Important methods:

```java
future.get();
future.get(1, TimeUnit.SECONDS);
future.isDone();
future.cancel(true);
future.isCancelled();
```

Problem with `Future`:

> The main limitation is that `get()` is blocking. Also, it does not provide convenient chaining, combining, or exception handling.

---

## 6. What Is `CompletableFuture`?

`CompletableFuture` is an enhanced version of `Future`. It supports asynchronous programming, callback chaining, combining multiple async tasks, exception handling, and manual completion.

Example:

```java
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "Hello";
        });

        future.thenApply(result -> result + " World")
              .thenAccept(System.out::println);

        future.join();
    }
}
```

---

## 7. `Future` vs `CompletableFuture`

| Feature            | `Future` | `CompletableFuture`   |
| ------------------ | -------- | --------------------- |
| Async result       | Yes      | Yes                   |
| Blocking `get()`   | Yes      | Yes, but can avoid it |
| Callback support   | No       | Yes                   |
| Chaining           | No       | Yes                   |
| Combine tasks      | Hard     | Easy                  |
| Exception handling | Limited  | Powerful              |
| Manual completion  | No       | Yes                   |

`Future` is simple but limited. It mainly gives us a placeholder for a result. `CompletableFuture` is more powerful because it supports non-blocking callbacks, chaining, combining multiple async tasks, and better exception handling.

---

## 8. `Lock` vs `synchronized`

### `synchronized`

```java
class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }
}
```

### `Lock`

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Counter {
    private int count = 0;
    private final Lock lock = new ReentrantLock();

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
}
```

Comparison:

| Feature             | `synchronized` | `Lock` |
| ------------------- | -------------- | ------ |
| Built-in keyword    | Yes            | No     |
| Manual unlock       | No             | Yes    |
| Try lock            | No             | Yes    |
| Interruptible lock  | No             | Yes    |
| Fairness control    | No             | Yes    |
| Multiple conditions | No             | Yes    |

`synchronized` is simpler and automatically releases the lock when the block exits. `Lock` is more flexible because it supports `tryLock()`, interruptible locking, fairness, and multiple condition variables. However, with `Lock`, we must release it manually in a `finally` block to avoid deadlock.

---

## 9. `wait()`, `notify()`, `notifyAll()`, and `join()`

### `wait()`

`wait()` causes the current thread to release the monitor lock and enter the waiting state until another thread calls `notify()` or `notifyAll()`.

### `notify()`

 `notify()` wakes up one waiting thread, but we cannot control which one.

### `notifyAll()`

 `notifyAll()` wakes up all waiting threads. Usually, `notifyAll()` is safer when multiple conditions may be involved.

Example:

```java
class SharedResource {
    private boolean ready = false;

    public synchronized void waitForReady() throws InterruptedException {
        while (!ready) {
            wait();
        }
        System.out.println("Resource is ready");
    }

    public synchronized void makeReady() {
        ready = true;
        notifyAll();
    }
}
```

Why use `while`, not `if`?

We use `while` because of spurious wakeups. A thread may wake up without the condition actually being true.

---

### `join()`

`join()` makes the current thread wait until another thread finishes.

Example:

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println("Child thread running");
        });

        t.start();
        t.join();

        System.out.println("Main thread continues after child finishes");
    }
}
```

---

# CompletableFuture APIs

## 10. `runAsync()`

Use when the task does not return a result.

```java
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Running async task");
        });

        future.join();
    }
}
```

`runAsync()` is used for asynchronous tasks that do not return a value. It returns `CompletableFuture<Void>`.

---

## 11. `supplyAsync()`

Use when the task returns a result.

```java
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            return 10;
        });

        System.out.println(future.join());
    }
}
```

`supplyAsync()` is used when the asynchronous task produces a result.

---

## 12. `thenApply()`

Transforms the result.

```java
CompletableFuture<Integer> future = CompletableFuture
        .supplyAsync(() -> 10)
        .thenApply(num -> num * 2);

System.out.println(future.join()); // 20
```

`thenApply()` is used to transform the result of a previous stage. It is similar to `map`.

---

## 13. `thenApplyAsync()`

Runs transformation asynchronously.

```java
CompletableFuture<Integer> future = CompletableFuture
        .supplyAsync(() -> 10)
        .thenApplyAsync(num -> {
            System.out.println(Thread.currentThread().getName());
            return num * 2;
        });

System.out.println(future.join());
```

`thenApply()` may run in the same thread that completed the previous stage. `thenApplyAsync()` submits the next stage to an executor, usually the common ForkJoinPool unless we provide a custom executor.

With custom executor:

```java
ExecutorService executor = Executors.newFixedThreadPool(2);

CompletableFuture<Integer> future = CompletableFuture
        .supplyAsync(() -> 10, executor)
        .thenApplyAsync(num -> num * 2, executor);

System.out.println(future.join());

executor.shutdown();
```

---

## 14. `thenAccept()`

Consumes result but does not return a new value.

```java
CompletableFuture<Void> future = CompletableFuture
        .supplyAsync(() -> "Hello")
        .thenAccept(result -> System.out.println(result));

future.join();
```

`thenAccept()` is used when we want to consume the result but do not need to return another value.

---

## 15. `thenRun()`

Runs next task without using previous result.

```java
CompletableFuture<Void> future = CompletableFuture
        .supplyAsync(() -> "Hello")
        .thenRun(() -> System.out.println("Task finished"));

future.join();
```

`thenRun()` runs after the previous stage completes, but it does not use the previous result.

---

## 16. `exceptionally()`

Handles exception and returns fallback value.

```java
CompletableFuture<Integer> future = CompletableFuture
        .supplyAsync(() -> {
            int x = 1 / 0;
            return x;
        })
        .exceptionally(ex -> {
            System.out.println("Exception: " + ex.getMessage());
            return -1;
        });

System.out.println(future.join()); // -1
```

`exceptionally()` is used to recover from an exception by returning a fallback value.

---

## 17. `handle()`

Handles both success and failure.

```java
CompletableFuture<Integer> future = CompletableFuture
        .supplyAsync(() -> {
            return 10;
        })
        .handle((result, ex) -> {
            if (ex != null) {
                return -1;
            }
            return result * 2;
        });

System.out.println(future.join()); // 20
```

Failure example:

```java
CompletableFuture<Integer> future = CompletableFuture
        .supplyAsync(() -> {
            throw new RuntimeException("Something went wrong");
        })
        .handle((result, ex) -> {
            if (ex != null) {
                return -1;
            }
            return result;
        });

System.out.println(future.join()); // -1
```

`handle()` is more general than `exceptionally()` because it is called whether the previous stage succeeds or fails.

---

## 18. `thenCompose()`

Flattens nested asynchronous calls.

```java
CompletableFuture<String> getUserId() {
    return CompletableFuture.supplyAsync(() -> "user123");
}

CompletableFuture<String> getUserName(String userId) {
    return CompletableFuture.supplyAsync(() -> "Alice");
}

CompletableFuture<String> future = getUserId()
        .thenCompose(userId -> getUserName(userId));

System.out.println(future.join());
```

`thenCompose()` is used when the next async task depends on the previous result. It avoids nested `CompletableFuture<CompletableFuture<T>>`. It is similar to `flatMap`.

---

## 19. `thenCombine()`

Combines two independent async results.

```java
CompletableFuture<Integer> priceFuture = CompletableFuture.supplyAsync(() -> 100);
CompletableFuture<Integer> taxFuture = CompletableFuture.supplyAsync(() -> 20);

CompletableFuture<Integer> totalFuture = priceFuture.thenCombine(
        taxFuture,
        (price, tax) -> price + tax
);

System.out.println(totalFuture.join()); // 120
```

`thenCombine()` is used when two independent asynchronous tasks can run in parallel and we want to combine their results.

---

## 20. `allOf()`

Waits for all futures to complete.

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "C");

CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2, f3);

all.join();

System.out.println(f1.join());
System.out.println(f2.join());
System.out.println(f3.join());
```

`allOf()` waits for all given futures to finish. It returns `CompletableFuture<Void>`, so if we need individual results, we call `join()` on each future after `allOf()` completes.

Collect results:

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Main {
    public static void main(String[] args) {
        List<CompletableFuture<String>> futures = List.of(
                CompletableFuture.supplyAsync(() -> "A"),
                CompletableFuture.supplyAsync(() -> "B"),
                CompletableFuture.supplyAsync(() -> "C")
        );

        CompletableFuture<Void> all = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        List<String> results = all.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        ).join();

        System.out.println(results);
    }
}
```

---

## 21. `anyOf()`

Returns when any one future completes.

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
    sleep(1000);
    return "Slow";
});

CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
    sleep(500);
    return "Fast";
});

CompletableFuture<Object> any = CompletableFuture.anyOf(f1, f2);

System.out.println(any.join()); // Fast

static void sleep(long ms) {
    try {
        Thread.sleep(ms);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```

`anyOf()` completes as soon as any one of the given futures completes. It is useful for racing multiple tasks and taking the fastest result.

---

## 22. `complete()`

Manually completes a future.

```java
CompletableFuture<String> future = new CompletableFuture<>();

future.complete("Manual result");

System.out.println(future.join());
```

`complete()` allows us to manually complete a `CompletableFuture`.

---

## 23. `completeExceptionally()`

Manually completes with exception.

```java
CompletableFuture<String> future = new CompletableFuture<>();

future.completeExceptionally(new RuntimeException("Failed"));

future.exceptionally(ex -> {
    System.out.println(ex.getMessage());
    return "fallback";
}).join();
```

`completeExceptionally()` completes the future with an exception.

---

## 24. `orTimeout()`

Throws timeout exception if not completed in time.

```java
CompletableFuture<String> future = CompletableFuture
        .supplyAsync(() -> {
            sleep(2000);
            return "Done";
        })
        .orTimeout(1, TimeUnit.SECONDS);

future.exceptionally(ex -> "Timeout").join();
```

`orTimeout()` completes the future exceptionally if it does not finish within the given time.

---

## 25. `completeOnTimeout()`

Returns fallback value on timeout.

```java
CompletableFuture<String> future = CompletableFuture
        .supplyAsync(() -> {
            sleep(2000);
            return "Done";
        })
        .completeOnTimeout("Fallback", 1, TimeUnit.SECONDS);

System.out.println(future.join()); // Fallback
```

`completeOnTimeout()` provides a fallback value if the future does not complete in time.

---

# Common Interview Summary

## Thread Creation

> Threads can be created by extending `Thread`, implementing `Runnable`, using lambda expressions, or using `ExecutorService`. In production, thread pools are preferred because they reuse threads and manage resources better.

## Thread Lifecycle

> A thread moves from `NEW` to `RUNNABLE` after `start()` is called. It may enter `BLOCKED`, `WAITING`, or `TIMED_WAITING` depending on locks or waiting operations. When execution finishes, it becomes `TERMINATED`.

## Thread Pool

> A thread pool manages worker threads and a task queue. It improves performance by reusing threads instead of creating new ones for every task.

## Cached vs Fixed Thread Pool

> `newCachedThreadPool()` can create too many threads because its max pool size is very large. `newFixedThreadPool()` can accumulate too many queued tasks because it uses an unbounded queue. In production, it is safer to create `ThreadPoolExecutor` manually with bounded queues and rejection policies.

## Future

> `Future` represents the result of an async task, but `get()` is blocking and it does not support easy chaining.

## CompletableFuture

> `CompletableFuture` supports asynchronous callbacks, chaining, combining tasks, and exception handling. It is much more flexible than `Future`.

## Lock vs synchronized

> `synchronized` is simpler and automatically releases the lock. `Lock` is more flexible and supports `tryLock`, interruptible locking, fairness, and multiple conditions, but we must release it manually.

## wait / notify / notifyAll / join

> `wait()` releases the lock and waits. `notify()` wakes one waiting thread. `notifyAll()` wakes all waiting threads. `join()` waits for another thread to finish.
