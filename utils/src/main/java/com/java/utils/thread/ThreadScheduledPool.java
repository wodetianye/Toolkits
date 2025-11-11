package com.java.utils.thread;

import android.content.Context;

import com.java.utils.log.Logger;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Introduce: 周期线程池.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 定时线程池区分西等待任务和不等任务的定时器.
 */
public class ThreadScheduledPool {
    private ScheduledThreadPoolExecutor schedulePool;

    private static final int THREAD_POOL_CENTER_SIZE = 4;

    private ThreadFactoryPxy mFactoryPxy;


    protected ThreadScheduledPool(Context context) {
        int threadMaxNumber = THREAD_POOL_CENTER_SIZE * 4;
        mFactoryPxy = new ThreadFactoryPxy(threadMaxNumber, Logger.getCurrentTagName(), context.getPackageName());
        if (null == schedulePool)
            schedulePool = new ScheduledThreadPoolExecutor(THREAD_POOL_CENTER_SIZE, mFactoryPxy);
    }

    /**
     * 延迟执行任务，单位默认为秒.
     *
     * @param runnable task run
     * @param start    delay time
     * @return 任务体
     */
    protected ScheduledFuture schedule(Runnable runnable, long start) {
        return schedule(runnable, start, TimeUnit.SECONDS);
    }

    /**
     * 延迟执行任务.
     *
     * @param runnable task run
     * @param start    delay time
     * @param timeUnit time unit
     * @return 任务体
     */
    protected ScheduledFuture schedule(Runnable runnable, long start, TimeUnit timeUnit) {
        isShutdown();
        return schedulePool.schedule(runnable, start, timeUnit);
    }

    /**
     * 固定比率周期任务，任务结束后，下一个任务马上执行
     *
     * @param runnable task run
     * @param start    start time
     * @param interval interval time
     * @return
     */
    protected ScheduledFuture scheduleAtFixedRate(Runnable runnable, long start, long interval) {
        return scheduleAtFixedRate(runnable, start, interval, TimeUnit.SECONDS);
    }

    /**
     * 固定比率周期任务，任务结束后，下一个任务马上执行
     *
     * @param runnable task run
     * @param start    start time
     * @param interval interval time
     * @param timeUnit time unit
     * @return
     */
    protected ScheduledFuture scheduleAtFixedRate(Runnable runnable, long start, long interval, TimeUnit timeUnit) {
        isShutdown();
        return schedulePool.scheduleAtFixedRate(runnable, start, interval, timeUnit);
    }

    /**
     * 固定延迟周期任务，任务结束后也会等待interval才执行下一个任务
     *
     * @param runnable task run
     * @param start    start time
     * @param interval interval time
     * @return
     */
    protected ScheduledFuture scheduleWithFixed(Runnable runnable, long start, long interval) {
        return scheduleWithFixed(runnable, start, interval, TimeUnit.SECONDS);
    }

    /**
     * 固定延迟周期任务，任务结束后也会等待interval才执行下一个任务
     *
     * @param runnable task run
     * @param start    start time
     * @param interval interval time
     * @param timeUnit time unit
     * @return
     */
    protected ScheduledFuture scheduleWithFixed(Runnable runnable, long start, long interval, TimeUnit timeUnit) {
        isShutdown();
        return schedulePool.scheduleWithFixedDelay(runnable, start, interval, timeUnit);
    }

    /**
     * 取消延迟任务
     *
     * @param scheduledFuture 任务体
     */
    protected void cancelSchedule(ScheduledFuture scheduledFuture) {
        if (null != scheduledFuture) scheduledFuture.cancel(true);
    }

    protected boolean isShutdown() {
        if (null == schedulePool || schedulePool.isShutdown())
            throw new RuntimeException("ScheduledThreadPoolExecutor is shutdown");
        else return false;
    }

    protected void closePool() {
        if (null == schedulePool) return;
        schedulePool.shutdown();
        schedulePool = null;
    }
}
