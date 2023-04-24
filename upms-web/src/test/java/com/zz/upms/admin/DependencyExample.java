package com.zz.upms.admin;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-04-21 16:19
 * @desc CyclicBarrierTest
 * ************************************
 */
public class DependencyExample {

    private final Lock lock = new ReentrantLock();
    private final Condition conditionB = lock.newCondition();
    private final Condition conditionC = lock.newCondition();
    private boolean flagA = false;
    private boolean flagB = false;

    public static void main(String[] args) throws InterruptedException {
        DependencyExample dependencyExample = new DependencyExample();
        Thread threadA = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 线程 A 执行任务
            System.out.println("Thread A executed");
            dependencyExample.setFlagA();
            dependencyExample.signalAll();
        });

        Thread threadB = new Thread(() -> {
            dependencyExample.awaitB();
            // 线程 B 执行任务
            System.out.println("Thread B executed");
            dependencyExample.setFlagB();
            dependencyExample.signalAll();
        });

        Thread threadC = new Thread(() -> {
            dependencyExample.awaitC();
            // 线程 C 执行任务
            System.out.println("Thread C executed");
        });

        threadC.start();
        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();
        threadC.join();
    }

    private void setFlagA() {
        lock.lock();
        System.out.println("A");
        try {
            flagA = true;
        } finally {
            lock.unlock();
        }
    }

    private void setFlagB() {
        lock.lock();
        try {
            flagB = true;
        } finally {
            lock.unlock();
        }
    }

    private void awaitB() {
        lock.lock();
        try {
            while (!flagA) {
                conditionB.await();
            }
            System.out.println("B");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void awaitC() {
        lock.lock();
        try {
            while (!flagB) {
                // 调用await会释放锁
                conditionC.await();
            }
            System.out.println("C");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void signalAll() {
        lock.lock();
        try {
            conditionB.signalAll();
            conditionC.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
