package com.java.utils.thread;

import com.java.utils.log.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Introduce: 线程工厂.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description:
 */
public class ThreadFactoryPxy implements ThreadFactory {
    private static final String TAG = ThreadFactoryPxy.class.getSimpleName();

    private final AtomicInteger atomicInteger = new AtomicInteger();

    private final String mThreadName;

    private final int threadMaxNum;

    private final Group group;

    protected ThreadFactoryPxy(int maxNum, String threadName, String groupName) {
        threadMaxNum = maxNum;
        mThreadName = threadName;
        group = new Group(groupName);
    }

    @Override
    public Thread newThread(Runnable r) {
        int currentThreadNum = atomicInteger.getAndIncrement();
        if (threadMaxNum < currentThreadNum) {
            atomicInteger.decrementAndGet();
            Logger.w(TAG, "The thread pool has been exceeded ! ! !");
            return null;
        } else {
            Thread thread = new Thread(group, r, mThreadName + atomicInteger.get());
            if (thread.isDaemon()) thread.setDaemon(false);
            if (thread.getPriority() != Thread.NORM_PRIORITY) thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }

    private static class Group extends ThreadGroup {

        public Group(String name) {
            super(name);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Logger.e(TAG, "捕获到线程异常", "thread name -> " + t.getName(), "exception -> " + e.toString());
        }
    }
}
