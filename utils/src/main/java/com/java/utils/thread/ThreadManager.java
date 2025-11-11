package com.java.utils.thread;


import android.content.Context;
import android.os.Looper;

import com.java.utils.BaseUtils;
import com.java.utils.ConvertUtils;
import com.java.utils.log.Logger;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Introduce: 线程池管理接口API.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 普通线程、延迟线程、周期线程、切换主线程等
 */
public class ThreadManager implements BaseUtils {
    private ThreadPool mThreadPool;

    private ThreadHandlerPool mThreadHandler;

    private ThreadScheduledPool mThreadScheduled;

    private ThreadManager() {
/*        throw new RuntimeException("Can't create ThreadUtils " + getCurrentThreadName()
                + " Private constructors cannot be created");*/
    }

    @Override
    public void initUtils(@NotNull Context context) {
        mThreadPool = new ThreadPool(context);
        mThreadHandler = new ThreadHandlerPool();
        mThreadScheduled = new ThreadScheduledPool(context);
    }

    @Override
    public void unInitUtils() {
        mThreadPool.closePool();
        mThreadHandler.closePool();
        mThreadScheduled.closePool();
        mThreadPool = null;
        mThreadHandler = null;
        mThreadScheduled = null;
    }

    public Thread getCurrentThread() {
        return Thread.currentThread();
    }

    public long getCurrentThreadId() {
        return Thread.currentThread().getId();
    }

    public String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    public boolean isMainThread() {
        return ConvertUtils.equals(Looper.getMainLooper().getThread(), getCurrentThread());
    }

    /**
     * Get current thread info.
     *
     * @return thread info
     */
    public String getThreadInfo() {
        Thread thread = Thread.currentThread();
        return "current thread info -> group："
                + thread.getThreadGroup()
                + ", name："
                + thread.getName()
                + ", id："
                + thread.getId()
                + ", isMain："
                + isMainThread();
    }

    public void removeHandleTask(Runnable runnable) {
        mThreadHandler.removeTask(runnable);
    }

    public void postUi(Runnable runnable) {
        mThreadHandler.postUi(runnable);
    }

    public void postDelay(Runnable runnable, long time) {
        mThreadHandler.postDelay(runnable, time);
    }

    public void execute(Runnable runnable) {
        mThreadPool.execute(runnable);
    }

    public <T> T execute(RunTask<T> runnable) {
        return mThreadPool.execute(runnable);
    }

    public <T> T executeNewRun(RunTask<T> runnable) {
        return mThreadPool.executeNewRun(runnable);
    }

    public void runAsync(Runnable runnable) {
        mThreadPool.runAsync(runnable);
    }

    public <T> T supplyAsync(RunTask<T> runnable) {
        return mThreadPool.supplyAsync(runnable);
    }

    public <T> T supplyAsync(RunTask<T> runnable, long timeout) {
        return mThreadPool.supplyAsync(runnable, timeout);
    }

    public <T> T supplyAsync(RunTask<T> runnable, long timeout, TimeUnit unit) {
        return mThreadPool.supplyAsync(runnable, timeout, unit);
    }

    public <T, U> U supplyFuture(RunTask<T> runnable, RunFunction<T, U> uRunTask) {
        CompletableFuture<T> completableFuture= supplyFuture(runnable);
        return thenApply(completableFuture, uRunTask);
    }

    public <T> CompletableFuture<T> supplyFuture(RunTask<T> runnable) {
        return mThreadPool.supplyFuture(runnable);
    }

    public <T, U> U thenApply(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        return mThreadPool.thenApply(future, uRunTask);
    }

    public <T, U> CompletableFuture<U> thenApplyCall(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        return mThreadPool.thenApplyCall(future, uRunTask);
    }

    public <T, U> U thenApplyAsync(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        return mThreadPool.thenApplyAsync(future, uRunTask);
    }

    public <T, U> CompletableFuture<U> thenApplyAsyncCall(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        return mThreadPool.thenApplyAsyncCall(future, uRunTask);
    }

    public void asyncDelay(Runnable run, long start) {
        mThreadScheduled.schedule(run, start);
    }

    public void asyncDelay(Runnable run, long start, TimeUnit unit) {
        mThreadScheduled.schedule(run, start, unit);
    }

    public ScheduledFuture asyncWithFixDelay(Runnable run, long start, long interval) {
        return mThreadScheduled.scheduleWithFixed(run, start, interval);
    }

    public ScheduledFuture asyncWithFixDelay(Runnable run, long start, long interval, TimeUnit unit) {
        return mThreadScheduled.scheduleWithFixed(run, start, interval, unit);
    }

    public ScheduledFuture asyncAtFixDelay(Runnable run, long start, long interval) {
        return mThreadScheduled.scheduleAtFixedRate(run, start, interval);
    }

    public ScheduledFuture asyncAtFixDelay(Runnable run, long start, long interval, TimeUnit unit) {
        return mThreadScheduled.scheduleAtFixedRate(run, start, interval, unit);
    }

    public void cancelDelayRun(ScheduledFuture scheduledFuture) {
        mThreadScheduled.cancelSchedule(scheduledFuture);
    }

    public void sleep(int time, TimeUnit unit){
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            Logger.e(e.toString());
        }
    }

    public static ThreadManager getInstance() {
        return Helper.tu;
    }

    private static class Helper {
        private static final ThreadManager tu = new ThreadManager();
    }
}
