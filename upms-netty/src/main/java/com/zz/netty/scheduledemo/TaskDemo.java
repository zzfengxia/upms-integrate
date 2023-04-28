package com.zz.netty.scheduledemo;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-04-27 11:23
 * @desc netty HashedWheelTimer时间轮定时任务调度函数使用
 * ************************************
 */
public class TaskDemo {
    public static void main(String[] args) {
        /**

         * HashedWheelTimer构造函数参数解释：

         * threadFactory - 线程工厂，默认是 Executors 中的 defaultThreadFactory
         * tickDuration - 时间轮两次 tick 之间的间隔时间，默认值 100ms。模拟时间指针的轮动，每隔指定时间指针跳一次。
         * 即每隔指定的时间，扫一次执行任务。该值设置越大，定时任务精度越低。比如设置为2s，那么延迟1s和1.5s的两个任务都会在2s后先后执行。
         * unit - tick 的时间单位，默认为 毫秒
         * ticksPerWheel - 一轮 tick 的数量
         * leakDetection - 用于优雅关闭的引用对象，默认 true
         * maxPendingTimeouts - 最大任务数，默认 -1，即为不限制
         *
         * HashedWheelTimer 建议使用单例模式创建，因为每个HashedWheelTimer对象都会开启一个线程。
         **/
        Timer timer = new HashedWheelTimer(5L, TimeUnit.SECONDS);
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("a");
            }
        }, 1, TimeUnit.SECONDS);

        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("b");
            }
        }, 3500, TimeUnit.MILLISECONDS);
    }
}
