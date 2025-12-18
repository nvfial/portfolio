package com.example.portfolio.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * J.U.C 核心类使用示例
 * 演示 Executor、Locks、Atomic、Collections、Tools 的使用
 */
public class JUCExamples {

    // ==================== 1. Executor 框架 ====================

    /**
     * ThreadPoolExecutor 核心示例
     */
    public static void threadPoolExample() {
        // 核心参数：核心线程数、最大线程数、存活时间、时间单位、工作队列、线程工厂、拒绝策略
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,                      // corePoolSize
            5,                      // maximumPoolSize
            60L,                    // keepAliveTime
            TimeUnit.SECONDS,       // unit
            new LinkedBlockingQueue<>(10),  // workQueue
            Executors.defaultThreadFactory(),  // threadFactory
            new ThreadPoolExecutor.CallerRunsPolicy()  // handler
        );

        // 提交任务
        Future<String> future = executor.submit(() -> {
            Thread.sleep(1000);
            return "Task completed";
        });

        try {
            String result = future.get(2, TimeUnit.SECONDS);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * ScheduledThreadPoolExecutor 定时任务示例
     */
    public static void scheduledExecutorExample() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // 延迟执行
        scheduler.schedule(() -> {
            System.out.println("Delayed task executed");
        }, 5, TimeUnit.SECONDS);

        // 固定频率执行
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Fixed rate task");
        }, 0, 1, TimeUnit.SECONDS);

        // 固定延迟执行
        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("Fixed delay task");
        }, 0, 1, TimeUnit.SECONDS);
    }

    // ==================== 2. Locks 锁机制 ====================

    /**
     * ReentrantLock 示例
     */
    public static void reentrantLockExample() {
        ReentrantLock lock = new ReentrantLock(true);  // true = 公平锁

        try {
            lock.lock();  // 获取锁
            // 临界区代码
            System.out.println("Critical section");
        } finally {
            lock.unlock();  // 必须释放锁
        }

        // 尝试获取锁（非阻塞）
        if (lock.tryLock()) {
            try {
                System.out.println("Lock acquired");
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * ReentrantReadWriteLock 读写锁示例
     */
    public static void readWriteLockExample() {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

        // 读操作（共享锁）
        readLock.lock();
        try {
            System.out.println("Reading data");
        } finally {
            readLock.unlock();
        }

        // 写操作（独占锁）
        writeLock.lock();
        try {
            System.out.println("Writing data");
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Condition 条件变量示例
     */
    public static void conditionExample() {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        AtomicInteger flag = new AtomicInteger(0);  // 0 = false, 1 = true

        // 等待线程
        new Thread(() -> {
            lock.lock();
            try {
                while (flag.get() == 0) {
                    condition.await();  // 释放锁并等待
                }
                System.out.println("Condition met!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();

        // 通知线程
        new Thread(() -> {
            lock.lock();
            try {
                flag.set(1);
                condition.signal();  // 唤醒等待线程
            } finally {
                lock.unlock();
            }
        }).start();
    }

    // ==================== 3. Atomic 原子类 ====================

    /**
     * AtomicInteger 示例
     */
    public static void atomicIntegerExample() {
        AtomicInteger counter = new AtomicInteger(0);

        // 原子递增
        int value = counter.incrementAndGet();  // 先加1再返回
        System.out.println("After incrementAndGet: " + value);
        
        int oldValue = counter.getAndIncrement();  // 先返回再加1
        System.out.println("Old value: " + oldValue + ", Current: " + counter.get());

        // CAS 操作
        boolean success = counter.compareAndSet(2, 3);  // 如果当前值是2，则更新为3
        System.out.println("CAS operation success: " + success);

        // 原子更新
        int result = counter.updateAndGet(x -> x * 2);  // 原子地将值乘以2
        System.out.println("After updateAndGet: " + result);
    }

    /**
     * AtomicReference 示例（无锁栈）
     */
    public static class LockFreeStack<T> {
        private static class Node<T> {
            T value;
            Node<T> next;
            Node(T value) { this.value = value; }
        }

        private final AtomicReference<Node<T>> head = new AtomicReference<>();

        public void push(T value) {
            Node<T> newHead = new Node<>(value);
            Node<T> oldHead;
            do {
                oldHead = head.get();
                newHead.next = oldHead;
            } while (!head.compareAndSet(oldHead, newHead));  // CAS 更新
        }

        public T pop() {
            Node<T> oldHead;
            Node<T> newHead;
            do {
                oldHead = head.get();
                if (oldHead == null) return null;
                newHead = oldHead.next;
            } while (!head.compareAndSet(oldHead, newHead));
            return oldHead.value;
        }
    }

    // ==================== 4. Collections 并发集合 ====================

    /**
     * ConcurrentHashMap 示例
     */
    public static void concurrentHashMapExample() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // 线程安全的 put
        map.put("key1", 1);

        // 原子更新
        map.compute("key1", (k, v) -> v == null ? 1 : v + 1);

        // 如果不存在则添加
        map.putIfAbsent("key2", 2);

        // 遍历（弱一致性）
        map.forEach((k, v) -> System.out.println(k + "=" + v));
    }

    /**
     * BlockingQueue 示例（生产者-消费者）
     */
    public static void blockingQueueExample() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

        // 生产者
        new Thread(() -> {
            try {
                queue.put("Item 1");  // 阻塞直到有空间
                queue.offer("Item 2", 1, TimeUnit.SECONDS);  // 带超时
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 消费者
        new Thread(() -> {
            try {
                String item = queue.take();  // 阻塞直到有元素
                System.out.println("Consumed: " + item);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * CopyOnWriteArrayList 示例
     */
    public static void copyOnWriteExample() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        // 写操作（会复制数组）
        list.add("Item 1");
        list.add("Item 2");

        // 读操作（无锁，高性能）
        list.forEach(System.out::println);

        // 适合读多写少场景
    }

    // ==================== 5. Tools 并发工具类 ====================

    /**
     * CountDownLatch 示例（等待多个线程完成）
     */
    public static void countDownLatchExample() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 启动多个线程
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + index + " working");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();  // 计数减1
                }
            }).start();
        }

        latch.await();  // 等待所有线程完成
        System.out.println("All threads completed!");
    }

    /**
     * CyclicBarrier 示例（多线程分阶段协作）
     */
    public static void cyclicBarrierExample() {
        int threadCount = 3;
        CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
            System.out.println("All threads reached barrier!");
        });

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + index + " phase 1");
                    barrier.await();  // 等待所有线程到达

                    System.out.println("Thread " + index + " phase 2");
                    barrier.await();  // 可以重复使用
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Semaphore 示例（限流）
     */
    public static void semaphoreExample() throws InterruptedException {
        Semaphore semaphore = new Semaphore(3);  // 最多3个线程同时访问

        for (int i = 0; i < 10; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    semaphore.acquire();  // 获取许可
                    System.out.println("Thread " + index + " accessing resource");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();  // 释放许可
                }
            }).start();
        }
    }

    /**
     * Exchanger 示例（线程间数据交换）
     */
    public static void exchangerExample() {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                String data1 = "Data from Thread 1";
                String received = exchanger.exchange(data1);
                System.out.println("Thread 1 received: " + received);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                String data2 = "Data from Thread 2";
                String received = exchanger.exchange(data2);
                System.out.println("Thread 2 received: " + received);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ==================== 6. 综合示例：线程池 + 并发集合 + 原子类 ====================

    /**
     * 综合示例：使用线程池处理任务，统计结果
     */
    public static void comprehensiveExample() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4, 8, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        ConcurrentHashMap<String, AtomicInteger> statistics = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(100);

        // 提交100个任务
        for (int i = 0; i < 100; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    // 模拟处理任务
                    String category = "Category" + (taskId % 5);
                    statistics.computeIfAbsent(category, k -> new AtomicInteger(0))
                             .incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 输出统计结果
        statistics.forEach((category, count) -> {
            System.out.println(category + ": " + count.get());
        });
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ThreadPoolExecutor Example ===");
        threadPoolExample();

        System.out.println("\n=== ReentrantLock Example ===");
        reentrantLockExample();

        System.out.println("\n=== AtomicInteger Example ===");
        atomicIntegerExample();

        System.out.println("\n=== CountDownLatch Example ===");
        countDownLatchExample();

        System.out.println("\n=== Comprehensive Example ===");
        comprehensiveExample();
    }
}

