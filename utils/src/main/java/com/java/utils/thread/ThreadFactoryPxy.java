package com.java.utils.thread;

import com.java.utils.log.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Introduce: 自定义线程工厂，统一线程命名、线程组、未捕获异常处理器
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description:
 * 1.自定义线程组，方便后续调试、dump线程
 * 2.统一线程名字，便于日志定位
 * 3.设置未捕获异常回调，打印线程崩溃堆栈
 * 4.不返回null，超过maxNum仅打警告，避免线程池NPE崩溃
 */
public class ThreadFactoryPxy implements ThreadFactory {
    private static final String TAG = ThreadFactoryPxy.class.getSimpleName();
    private final AtomicInteger threadSeq = new AtomicInteger(0);
    private final String mThreadName;
    private final int threadMaxNum;
    private final ThreadGroup group;

    protected ThreadFactoryPxy(int maxNum, String threadName, String groupName) {
        threadMaxNum = maxNum;
        mThreadName = threadName;
        group = new ThreadGroup(groupName);
    }

    @Override
    public Thread newThread(Runnable r) {
        int seq = threadSeq.getAndIncrement();
        if (threadMaxNum < seq) {
            Logger.w(TAG, "thread count exceed maxNum:" + threadMaxNum + ", current:" + seq);
        }
        // 修复：直接使用seq，不再二次get，避免线程名跳号
        Thread thread = new Thread(group, r, mThreadName + "-" + seq);
        thread.setDaemon(false);
        thread.setPriority(Thread.NORM_PRIORITY);
        // 线程未捕获异常处理器，worker线程崩溃打印完整堆栈
        thread.setUncaughtExceptionHandler((t, e) -> {
            Logger.e(TAG, "worker thread uncaughtException, thread will exit, threadName:" + t.getName(), e);
        });
        return thread;
    }
}