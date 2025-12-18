# J.U.C (Java Util Concurrent) 深度解析

## 一、J.U.C 整体架构

J.U.C 是 Java 并发编程的核心包，主要分为五大模块：
1. **Executor** - 执行器框架（线程池管理）
2. **Collections** - 并发集合
3. **Locks** - 锁机制
4. **Atomic** - 原子类
5. **Tools** - 并发工具类

---

## 二、顶层接口分析

### 1. Executor 框架（最核心）

#### 1.1 Executor 接口（顶层接口）
```java
public interface Executor {
    void execute(Runnable command);
}
```

**实现思路**：
- **设计模式**：命令模式 + 策略模式
- **核心思想**：将任务提交与执行解耦
- **关键点**：不关心线程如何创建、调度，只关注任务的执行

**重要节点**：
- 最简单的执行器接口
- 所有线程池的根基

---

#### 1.2 ExecutorService 接口（扩展 Executor）

**实现思路**：
- **生命周期管理**：`shutdown()`, `shutdownNow()`, `isShutdown()`
- **任务提交增强**：`submit()` 返回 `Future`，支持异步结果获取
- **批量执行**：`invokeAll()`, `invokeAny()`

**关键方法**：
```java
<T> Future<T> submit(Callable<T> task);
<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks);
```

**重要节点**：
- 引入了 `Future` 机制，实现异步编程
- 支持任务取消和结果获取

---

#### 1.3 ThreadPoolExecutor（核心实现类）

**实现思路**：
- **线程池状态管理**：使用原子整数 `ctl` 同时保存线程数和状态
  ```java
  private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
  // 高3位：状态（RUNNING, SHUTDOWN, STOP, TIDYING, TERMINATED）
  // 低29位：工作线程数
  ```

- **工作队列**：使用 `BlockingQueue` 存储待执行任务
  - `ArrayBlockingQueue`：有界队列
  - `LinkedBlockingQueue`：无界队列
  - `SynchronousQueue`：同步队列（直接传递）

- **线程工厂**：`ThreadFactory` 创建线程，可自定义线程名、优先级

- **拒绝策略**：`RejectedExecutionHandler`
  - `AbortPolicy`：抛异常（默认）
  - `CallerRunsPolicy`：调用者执行
  - `DiscardPolicy`：静默丢弃
  - `DiscardOldestPolicy`：丢弃最老任务

**核心执行流程**：
```
1. 提交任务 execute() → addWorker()
2. 创建工作线程 Worker（内部类，实现 Runnable）
3. Worker.run() → runWorker() → getTask() → task.run()
4. 线程复用：循环从队列取任务执行
```

**重要节点**：
- **ctl 设计**：巧妙使用位运算，一个原子变量管理两个状态
- **Worker 设计**：继承 AQS，实现线程复用
- **拒绝策略**：策略模式，灵活扩展

---

#### 1.4 ScheduledExecutorService 接口

**实现思路**：
- 扩展 `ExecutorService`，支持延迟和周期性任务
- 核心方法：`schedule()`, `scheduleAtFixedRate()`, `scheduleWithFixedDelay()`

---

#### 1.5 ScheduledThreadPoolExecutor（定时任务实现）

**实现思路**：
- 继承 `ThreadPoolExecutor`
- **延迟队列**：使用 `DelayedWorkQueue`（内部类，基于堆）
- **任务封装**：`ScheduledFutureTask` 实现 `Delayed` 接口
- **执行策略**：
  - `scheduleAtFixedRate`：固定频率（可能并发执行）
  - `scheduleWithFixedDelay`：固定延迟（串行执行）

**重要节点**：
- 使用优先级队列实现延迟调度
- 时间精度：`System.nanoTime()` 避免系统时间调整影响

---

#### 1.6 Future 接口

**实现思路**：
- **异步结果获取**：`get()` 阻塞等待结果
- **任务取消**：`cancel(boolean mayInterruptIfRunning)`
- **状态查询**：`isDone()`, `isCancelled()`

**重要节点**：
- 实现异步编程模型
- `get()` 可能阻塞，需要超时控制

---

#### 1.7 Callable 接口

**实现思路**：
- 相比 `Runnable`，可以返回结果和抛出异常
```java
V call() throws Exception;
```

**重要节点**：
- 与 `Future` 配合，实现有返回值的异步任务

---

### 2. Collections（并发集合）

#### 2.1 ConcurrentHashMap（最重要的并发集合）

**实现思路（JDK 8+）**：
- **分段锁 → CAS + synchronized**：
  - JDK 7：Segment 分段锁（16个段）
  - JDK 8：数组 + 链表/红黑树，每个桶独立加锁

- **核心数据结构**：
  ```java
  transient volatile Node<K,V>[] table;  // 哈希表
  transient volatile Node<K,V>[] nextTable;  // 扩容时的临时表
  ```

- **关键操作**：
  - **put()**：
    1. 计算 hash，定位到桶
    2. 如果桶为空，CAS 插入
    3. 如果桶有值，synchronized 锁住头节点
    4. 链表转红黑树（阈值 8）
  
  - **get()**：无锁读取（volatile 保证可见性）
  
  - **扩容**：
    - 多线程协助扩容（transfer）
    - 使用 `sizeCtl` 控制扩容状态

- **重要节点**：
  - **CAS 优化**：减少锁竞争
  - **红黑树优化**：O(log n) 查找
  - **扩容优化**：多线程协助，提高效率

---

#### 2.2 BlockingQueue 接口

**实现思路**：
- 阻塞队列，支持生产者-消费者模式
- 核心方法：
  - `put()`：阻塞插入（队列满时）
  - `take()`：阻塞取出（队列空时）
  - `offer()`, `poll()`：非阻塞版本

---

#### 2.3 ArrayBlockingQueue

**实现思路**：
- **有界队列**：固定容量数组
- **锁机制**：`ReentrantLock` + `Condition`
  ```java
  final ReentrantLock lock;
  private final Condition notEmpty;  // 非空条件
  private final Condition notFull;    // 非满条件
  ```

- **重要节点**：
  - 使用 `Condition` 实现精确唤醒
  - 数组循环使用（取模运算）

---

#### 2.4 LinkedBlockingQueue

**实现思路**：
- **无界/有界可选**：链表实现
- **双锁设计**：`putLock` 和 `takeLock` 分离
  - 提高并发度（生产者和消费者不互斥）
- **重要节点**：
  - 双锁优化：减少锁竞争
  - 使用 `AtomicInteger` 统计元素数量

---

#### 2.5 DelayQueue

**实现思路**：
- **延迟队列**：元素必须实现 `Delayed` 接口
- **内部结构**：`PriorityQueue`（堆）
- **应用场景**：定时任务、缓存过期
- **重要节点**：
  - 使用优先级队列按到期时间排序
  - `take()` 阻塞直到有元素到期

---

#### 2.6 CopyOnWriteArrayList

**实现思路**：
- **写时复制**：修改时复制整个数组
  ```java
  public boolean add(E e) {
      final ReentrantLock lock = this.lock;
      lock.lock();
      try {
          Object[] elements = getArray();
          int len = elements.length;
          Object[] newElements = Arrays.copyOf(elements, len + 1);
          newElements[len] = e;
          setArray(newElements);
          return true;
      } finally {
          lock.unlock();
      }
  }
  ```

- **读操作**：无锁，直接读取数组引用
- **重要节点**：
  - 适合读多写少场景
  - 写操作性能差（需要复制）
  - 弱一致性：读操作可能读到旧数据

---

### 3. Locks（锁机制）

#### 3.1 Lock 接口

**实现思路**：
- 相比 `synchronized`，提供更灵活的锁机制
- 核心方法：
  - `lock()`：获取锁
  - `unlock()`：释放锁
  - `tryLock()`：尝试获取锁（非阻塞）
  - `lockInterruptibly()`：可中断获取锁

**重要节点**：
- 显式锁，需要手动释放
- 支持公平锁和非公平锁

---

#### 3.2 ReentrantLock（可重入锁）

**实现思路**：
- **基于 AQS**：`AbstractQueuedSynchronizer`
- **可重入**：同一线程可多次获取锁
- **公平性**：
  - 公平锁：按等待时间排序
  - 非公平锁：允许插队（性能更好）

**核心实现**：
```java
// 非公平锁获取
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;  // 重入计数
        setState(nextc);
        return true;
    }
    return false;
}
```

**重要节点**：
- **AQS 核心**：`state` 表示锁状态，`0` 表示未锁定
- **CLH 队列**：等待线程组成双向链表
- **CAS 操作**：原子性更新状态

---

#### 3.3 ReadWriteLock 接口

**实现思路**：
- 读写分离：读锁共享，写锁独占
- 提高并发度：多读一写场景

---

#### 3.4 ReentrantReadWriteLock

**实现思路**：
- **双锁设计**：`ReadLock` 和 `WriteLock`
- **AQS 状态设计**：
  ```java
  // state 高16位：读锁数量
  // state 低16位：写锁数量
  static final int SHARED_SHIFT   = 16;
  static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
  static final int MAX_COUNT       = (1 << SHARED_SHIFT) - 1;
  static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
  ```

- **锁降级**：写锁可以降级为读锁
- **重要节点**：
  - 位运算巧妙设计，一个 state 管理两种锁
  - 写锁优先：防止写饥饿

---

#### 3.5 Condition 接口

**实现思路**：
- 替代 `Object.wait()/notify()`
- 与 `Lock` 配合使用
- **等待队列**：每个 `Condition` 维护一个等待队列

**重要节点**：
- 支持多个等待队列（一个锁可以有多个 Condition）
- `await()` 释放锁，`signal()` 唤醒后重新获取锁

---

#### 3.6 LockSupport

**实现思路**：
- **底层工具类**：提供线程阻塞/唤醒
- **核心方法**：
  - `park()`：阻塞当前线程
  - `unpark(Thread)`：唤醒指定线程
- **重要节点**：
  - AQS 的底层实现依赖
  - 相比 `wait/notify`，更灵活（可指定线程）

---

### 4. Atomic（原子类）

#### 4.1 原子类整体设计

**实现思路**：
- **CAS 操作**：`compareAndSet()` 原子更新
- **无锁编程**：避免锁竞争，提高性能
- **底层实现**：`Unsafe` 类直接操作内存

---

#### 4.2 AtomicInteger

**实现思路**：
```java
private volatile int value;  // volatile 保证可见性

public final int getAndIncrement() {
    return unsafe.getAndAddInt(this, valueOffset, 1);
}

// Unsafe 实现
public final int getAndAddInt(Object o, long offset, int delta) {
    int v;
    do {
        v = getIntVolatile(o, offset);  // 获取当前值
    } while (!compareAndSwapInt(o, offset, v, v + delta));  // CAS 更新
    return v;
}
```

**重要节点**：
- **CAS 循环**：失败重试（自旋）
- **ABA 问题**：值被修改后改回原值（可用 `AtomicStampedReference` 解决）

---

#### 4.3 AtomicReference

**实现思路**：
- 原子更新引用类型
- 应用：无锁栈、无锁队列

---

#### 4.4 AtomicStampedReference

**实现思路**：
- **版本号机制**：解决 ABA 问题
```java
private static class Pair<T> {
    final T reference;
    final int stamp;  // 版本号
}
```

**重要节点**：
- 每次修改都更新版本号
- `compareAndSet(expectedReference, newReference, expectedStamp, newStamp)`

---

### 5. Tools（并发工具类）

#### 5.1 CountDownLatch（倒计时门闩）

**实现思路**：
- **基于 AQS**：共享模式
- **一次性**：计数到 0 后无法重置
- **应用场景**：等待多个线程完成

**核心实现**：
```java
// 初始化
CountDownLatch latch = new CountDownLatch(5);

// 等待
latch.await();

// 计数减1
latch.countDown();
```

**重要节点**：
- `state` 表示剩余计数
- `countDown()` 减1，`await()` 等待 state 为 0

---

#### 5.2 CyclicBarrier（循环屏障）

**实现思路**：
- **可重用**：计数到 0 后可重置
- **应用场景**：多线程分阶段协作

**重要节点**：
- 相比 `CountDownLatch`，可以循环使用
- 支持 `Runnable barrierAction`（到达屏障时执行）

---

#### 5.3 Semaphore（信号量）

**实现思路**：
- **基于 AQS**：共享模式
- **控制并发数**：限制同时访问资源的线程数
- **重要节点**：
  - `acquire()`：获取许可（state 减1）
  - `release()`：释放许可（state 加1）
  - 可用于限流

---

#### 5.4 Exchanger（交换器）

**实现思路**：
- **线程间数据交换**：两个线程在同步点交换数据
- **应用场景**：管道设计模式

---

## 三、核心设计模式总结

1. **模板方法模式**：AQS 定义算法骨架，子类实现具体步骤
2. **策略模式**：拒绝策略、锁策略
3. **工厂模式**：`Executors` 工具类创建线程池
4. **观察者模式**：`Future` 异步结果通知

---

## 四、关键技术点

1. **CAS（Compare-And-Swap）**：无锁编程基础
2. **AQS（AbstractQueuedSynchronizer）**：锁框架核心
3. **volatile**：可见性保证
4. **内存屏障**：`Unsafe` 提供底层支持
5. **CLH 队列**：等待线程队列

---

## 五、性能优化要点

1. **减少锁竞争**：分段锁、读写分离、CAS
2. **提高并发度**：双锁设计、无锁数据结构
3. **避免上下文切换**：合理使用线程池
4. **缓存友好**：减少 false sharing

---

## 六、学习路径建议

1. **基础**：理解 `synchronized` 和 `volatile`
2. **进阶**：学习 `ReentrantLock` 和 AQS
3. **高级**：深入 `ConcurrentHashMap` 和 `ThreadPoolExecutor`
4. **实战**：结合实际项目应用并发工具






