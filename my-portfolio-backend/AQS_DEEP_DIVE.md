# AQS (AbstractQueuedSynchronizer) 深度解析

## 一、AQS 核心地位

AQS 是 J.U.C 的基石，`ReentrantLock`、`CountDownLatch`、`Semaphore`、`ReentrantReadWriteLock` 等都基于 AQS 实现。

---

## 二、AQS 核心设计

### 1. 状态管理（state）

```java
private volatile int state;  // 核心状态变量
```

**设计思路**：
- **volatile**：保证可见性
- **state 含义**：由子类定义
  - `ReentrantLock`：0 = 未锁定，1 = 锁定，>1 = 重入次数
  - `CountDownLatch`：剩余计数
  - `Semaphore`：可用许可数

---

### 2. CLH 队列（等待队列）

**CLH 队列结构**：
```
Head (虚拟节点)
  ↓
Node1 (等待线程1)
  ↓
Node2 (等待线程2)
  ↓
Tail
```

**Node 节点结构**：
```java
static final class Node {
    volatile int waitStatus;  // 等待状态
    volatile Node prev;       // 前驱节点
    volatile Node next;       // 后继节点
    volatile Thread thread;   // 等待的线程
    Node nextWaiter;          // 条件队列的下一个节点
}
```

**waitStatus 状态值**：
- `CANCELLED (1)`：节点已取消
- `SIGNAL (-1)`：后继节点需要被唤醒
- `CONDITION (-2)`：节点在条件队列中
- `PROPAGATE (-3)`：共享模式下传播唤醒

---

## 三、AQS 核心方法

### 1. 独占模式（Exclusive）

#### 1.1 acquire(int arg) - 获取锁

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&           // 1. 尝试获取锁
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))  // 2. 失败则入队等待
        selfInterrupt();               // 3. 中断自己
}
```

**执行流程**：
1. **tryAcquire()**：子类实现，尝试获取锁
2. **addWaiter()**：创建节点并入队
3. **acquireQueued()**：在队列中自旋等待
4. **selfInterrupt()**：恢复中断状态

#### 1.2 addWaiter(Node mode) - 添加等待节点

```java
private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    Node pred = tail;
    if (pred != null) {
        node.prev = pred;
        if (compareAndSetTail(pred, node)) {  // CAS 设置尾节点
            pred.next = node;
            return node;
        }
    }
    enq(node);  // 失败则自旋入队
    return node;
}
```

**关键点**：
- **CAS 操作**：原子性更新尾节点
- **失败重试**：`enq()` 方法自旋直到成功

#### 1.3 acquireQueued() - 队列中等待

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {  // 自旋
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {  // 前驱是头节点且获取锁成功
                setHead(node);  // 设置为头节点
                p.next = null;  // 帮助 GC
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&  // 判断是否需要阻塞
                parkAndCheckInterrupt())  // 阻塞线程
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);  // 取消获取
    }
}
```

**关键点**：
- **自旋优化**：前驱是头节点时再次尝试获取（减少阻塞）
- **park()**：使用 `LockSupport.park()` 阻塞线程
- **中断处理**：记录中断状态，但不立即响应

#### 1.4 release(int arg) - 释放锁

```java
public final boolean release(int arg) {
    if (tryRelease(arg)) {  // 子类实现，尝试释放
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);  // 唤醒后继节点
        return true;
    }
    return false;
}
```

**关键点**：
- **unparkSuccessor()**：唤醒下一个等待线程
- **从后往前找**：找到第一个未取消的节点

---

### 2. 共享模式（Shared）

#### 2.1 acquireShared(int arg)

```java
public final void acquireShared(int arg) {
    if (tryAcquireShared(arg) < 0)  // 返回值 < 0 表示失败
        doAcquireShared(arg);
}
```

**与独占模式的区别**：
- 多个线程可以同时获取
- `tryAcquireShared()` 返回剩余资源数

#### 2.2 doAcquireShared()

```java
private void doAcquireShared(int arg) {
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
                int r = tryAcquireShared(arg);
                if (r >= 0) {
                    setHeadAndPropagate(node, r);  // 设置头节点并传播
                    p.next = null;
                    if (interrupted)
                        selfInterrupt();
                    failed = false;
                    return;
                }
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

**关键点**：
- **传播唤醒**：`setHeadAndPropagate()` 唤醒后续共享节点
- **PROPAGATE 状态**：确保唤醒信号不丢失

---

## 四、AQS 在 ReentrantLock 中的应用

### 1. 非公平锁实现

```java
static final class NonfairSync extends Sync {
    final void lock() {
        if (compareAndSetState(0, 1))  // 直接尝试 CAS
            setExclusiveOwnerThread(Thread.currentThread());
        else
            acquire(1);  // 失败则调用 AQS
    }

    protected final boolean tryAcquire(int acquires) {
        return nonfairTryAcquire(acquires);
    }
}
```

**关键点**：
- **插队机制**：新线程可以直接尝试获取锁
- **性能优化**：减少上下文切换

### 2. 公平锁实现

```java
static final class FairSync extends Sync {
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (!hasQueuedPredecessors() &&  // 检查是否有等待线程
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;  // 重入
            setState(nextc);
            return true;
        }
        return false;
    }
}
```

**关键点**：
- **hasQueuedPredecessors()**：确保按等待时间排序
- **公平性**：先来先服务

---

## 五、AQS 在 CountDownLatch 中的应用

### 1. 共享模式实现

```java
public class CountDownLatch {
    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync(int count) {
            setState(count);  // state = 初始计数
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;  // 计数为0则成功
        }

        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))  // CAS 减1
                    return nextc == 0;  // 减到0则唤醒所有等待线程
            }
        }
    }
}
```

**关键点**：
- **共享模式**：多个线程可以同时 `await()`
- **CAS 循环**：原子性减计数

---

## 六、AQS 在 Semaphore 中的应用

### 1. 共享模式实现

```java
abstract static class Sync extends AbstractQueuedSynchronizer {
    Sync(int permits) {
        setState(permits);  // state = 许可数
    }

    final int getPermits() {
        return getState();
    }

    final int nonfairTryAcquireShared(int acquires) {
        for (;;) {
            int available = getState();
            int remaining = available - acquires;
            if (remaining < 0 ||
                compareAndSetState(available, remaining))  // CAS 减许可
                return remaining;
        }
    }

    protected final boolean tryReleaseShared(int releases) {
        for (;;) {
            int current = getState();
            int next = current + releases;
            if (next < current)  // 溢出检查
                throw new Error("Maximum permit count exceeded");
            if (compareAndSetState(current, next))  // CAS 加许可
                return true;
        }
    }
}
```

**关键点**：
- **非公平获取**：新线程可以插队
- **溢出保护**：防止许可数溢出

---

## 七、AQS 设计模式

### 1. 模板方法模式

**AQS 定义算法骨架**：
```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&        // 子类实现
        acquireQueued(...))         // AQS 实现
        selfInterrupt();
}
```

**子类实现具体步骤**：
- `tryAcquire()`：如何获取锁
- `tryRelease()`：如何释放锁

### 2. 策略模式

不同的同步器使用不同的策略：
- `ReentrantLock`：独占模式
- `CountDownLatch`：共享模式
- `ReentrantReadWriteLock`：混合模式

---

## 八、AQS 性能优化技巧

### 1. 自旋优化

```java
// 在 acquireQueued 中
if (p == head && tryAcquire(arg)) {  // 前驱是头节点时再次尝试
    // 成功获取，避免阻塞
}
```

**原理**：减少线程阻塞和唤醒的开销

### 2. CAS 操作

所有状态更新都使用 CAS，避免锁竞争

### 3. 队列优化

- **虚拟头节点**：简化边界处理
- **从后往前找**：提高找到有效节点的概率

---

## 九、AQS 常见问题

### 1. 为什么需要虚拟头节点？

- 简化边界条件处理
- 统一队列操作逻辑

### 2. 为什么 acquireQueued 中要自旋？

- 减少线程阻塞/唤醒开销
- 提高获取锁的成功率

### 3. 共享模式如何实现传播唤醒？

- `PROPAGATE` 状态确保唤醒信号不丢失
- `setHeadAndPropagate()` 唤醒后续共享节点

---

## 十、AQS 学习建议

1. **理解 CLH 队列**：掌握队列结构和操作
2. **理解 CAS**：原子操作的基础
3. **理解模板方法**：AQS 的设计模式
4. **阅读源码**：从 `ReentrantLock` 开始
5. **调试跟踪**：使用 IDE 单步调试理解流程

---

## 十一、AQS 源码关键位置

### 1. 核心字段
- `state`：状态变量
- `head`：队列头
- `tail`：队列尾

### 2. 核心方法
- `acquire()`：独占获取
- `release()`：独占释放
- `acquireShared()`：共享获取
- `releaseShared()`：共享释放
- `compareAndSetState()`：CAS 更新状态

### 3. 辅助方法
- `addWaiter()`：添加等待节点
- `enq()`：自旋入队
- `unparkSuccessor()`：唤醒后继节点
- `shouldParkAfterFailedAcquire()`：判断是否需要阻塞

---

## 总结

AQS 是 J.U.C 的核心，通过：
1. **状态管理**：volatile state + CAS
2. **队列管理**：CLH 队列 + 节点状态
3. **模板方法**：定义算法骨架，子类实现细节
4. **性能优化**：自旋、CAS、队列优化

实现了高效、灵活的同步框架，是学习 Java 并发编程的必经之路。






