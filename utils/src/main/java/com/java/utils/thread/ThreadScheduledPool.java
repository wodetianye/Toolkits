package com.java.utils.thread;

import android.content.Context;

import com.java.utils.log.Logger;

import java.util.concurrent.*;

/**
 * @Introduce: 定时调度线程池，延迟任务、周期重复任务
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 1.包装Runnable捕获Throwable，周期任务异常不会直接终止调度器
 * 2.setRemoveOnCancelPolicy(true)：取消任务直接从队列移除，减少内存占用
 */
public class ThreadScheduledPool {
    private static final String TAG = "ThreadScheduledPool";
    private ScheduledThreadPoolExecutor schedulePool;
    private static final int THREAD_POOL_CENTER_SIZE = 4;
    private ThreadFactoryPxy mFactoryPxy;

    protected ThreadScheduledPool(Context context) {
        int threadMaxNumber = THREAD_POOL_CENTER_SIZE * 4;
        mFactoryPxy = new ThreadFactoryPxy(threadMaxNumber, Logger.getCurrentTagName(), context.getPackageName());
        schedulePool = new ScheduledThreadPoolExecutor(THREAD_POOL_CENTER_SIZE, mFactoryPxy);
        // 取消任务立刻从队列删除，减少内存占用
        schedulePool.setRemoveOnCancelPolicy(true);
    }

    /**
     * 延迟执行一次，单位秒
     */
    protected ScheduledFuture schedule(Runnable runnable, long start, TimeUnit timeUnit) {
        if (isShutdown() || runnable == null) return null;
        return schedulePool.schedule(wrapRunnable(runnable), start, timeUnit);
    }

    /**
     * 固定速率执行：按间隔时间，不管上一轮是否执行完成，到点就执行
     */
    protected ScheduledFuture scheduleAtFixedRate(Runnable runnable, long start, long interval, TimeUnit timeUnit) {
        if (isShutdown() || runnable == null) return null;
        return schedulePool.scheduleAtFixedRate(wrapRunnable(runnable), start, interval, timeUnit);
    }

    /**
     * 固定延迟执行：上一轮执行完毕之后，再等待interval才执行下一轮
     */
    protected ScheduledFuture scheduleWithFixed(Runnable runnable, long start, long interval, TimeUnit timeUnit) {
        if (isShutdown() || runnable == null) return null;
        return schedulePool.scheduleWithFixedDelay(wrapRunnable(runnable), start, interval, timeUnit);
    }

    /**
     * 取消定时任务
     *
     * @param scheduledFuture schedule返回的future对象
     */
    protected void cancelSchedule(ScheduledFuture scheduledFuture) {
        if (null != scheduledFuture && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
        }
    }

    protected boolean isShutdown() {
        if (null == schedulePool || schedulePool.isShutdown()) {
            Logger.w(TAG, "ScheduledPool shutdown");
            return true;
        }
        return false;
    }

    protected void closePool() {
        if (null == schedulePool) return;
        schedulePool.shutdown();
        try {
            if (!schedulePool.awaitTermination(2, TimeUnit.SECONDS)) {
                schedulePool.shutdownNow();
            }
        } catch (InterruptedException e) {
            schedulePool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        mFactoryPxy = null;
        schedulePool = null;
    }

    /**
     * 包装Runnable，捕获全部Throwable，防止ScheduledExecutor周期任务因为异常直接停止调度
     */
    private Runnable wrapRunnable(Runnable origin) {
        if (origin == null) return () -> {
        };
        return () -> {
            try {
                origin.run();
            } catch (Throwable t) {
                Logger.e(TAG, "scheduled task exception, will not stop scheduler", t);
            }
        };
    }
}