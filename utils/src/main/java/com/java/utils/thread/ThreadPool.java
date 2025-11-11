package com.java.utils.thread;

import android.content.Context;

import com.java.utils.log.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Introduce: 异步线程池.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 根据CPU核数来自动设置最大核心线程以及最大线程数量及线程任务战
 */
public class ThreadPool {
    /*** CPU 核数 **/
    private static final int CPU_NAM = Runtime.getRuntime().availableProcessors();

    /*** 核心线程 **/
    private static int THREAD_POOL_CENTER_SIZE = 4;

    /*** 队列最大值 **/
    private static final int QUEUE_NUM = 20;

    /*** 线程保活时间 **/
    private static final long KEEP_ALIVE_TIME = 20L;

    private ThreadPoolExecutor mThreadPool;

    protected ThreadPool(Context context) {
        //取最优核心线程数
        if (CPU_NAM > THREAD_POOL_CENTER_SIZE) THREAD_POOL_CENTER_SIZE = CPU_NAM;
        // 最大线程数
        int THREAD_MAX_NUMBER = THREAD_POOL_CENTER_SIZE * 10;
        BlockingQueue<Runnable> mWorkQueue = new LinkedBlockingQueue<>(QUEUE_NUM);
        ThreadFactoryPxy mFactoryPxy = new ThreadFactoryPxy(THREAD_MAX_NUMBER, Logger.getCurrentTagName(), context.getPackageName());
        mThreadPool = new ThreadPoolExecutor(THREAD_POOL_CENTER_SIZE,
                THREAD_MAX_NUMBER,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, mWorkQueue, mFactoryPxy);
    }

    protected boolean isShutdown() {
        if (null == mThreadPool || mThreadPool.isShutdown())
            throw new RuntimeException("ThreadPoolExecutor is shutdown");
        else return false;
    }

    protected void closePool() {
        if (null == mThreadPool) return;
        mThreadPool.shutdown();
        mThreadPool = null;
    }

    /**
     * 开启异步线程.{经大量测试, execute没有 @link runAsync执行的快}
     *
     * @param runnable task run
     */
    protected void execute(Runnable runnable) {
        isShutdown();
        mThreadPool.execute(runnable);
    }

    /**
     * 开启异步线程 可携带返回值.
     *
     * @param runTask 任务线程
     */
    protected <T> T execute(RunTask<T> runTask) {
        try {
            isShutdown();
            FutureTask<T> futureTask = new FutureTask<T>(runTask);
            mThreadPool.submit(futureTask);
            return futureTask.get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(e.toString());
            return null;
        }
    }

    /**
     * 开启异步线程 可携带返回值.
     *
     * @param runTask 任务线程
     */
    protected <T> T executeNewRun(RunTask<T> runTask) {
        try {
            isShutdown();
            return mThreadPool.submit((Callable<T>) runTask).get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(e.toString());
            return null;
        }
    }

    /**
     * 开启异步线程.
     *
     * @param runnable 任务线程
     */
    protected void runAsync(Runnable runnable) {
        if (isShutdown()) return;
        CompletableFuture.runAsync(runnable, mThreadPool);
    }

    /**
     * 开启异步线程 可携带返回值.
     *
     * @param runnable 任务线程
     * @param <T>      返回类型参数
     * @return T
     */
    protected <T> T supplyAsync(RunTask<T> runnable) {
        return supplyAsync(runnable, 0);
    }

    /**
     * 开启异步线程 可携带返回值, 本次调用会和参数线程不在同一个线程.
     *
     * @param runnable 任务线程
     * @param timeout  超时时间 默认单位 秒
     * @param <T>      返回类型参数
     * @return T
     */
    protected <T> T supplyAsync(RunTask<T> runnable, long timeout) {
        return supplyAsync(runnable, timeout, TimeUnit.SECONDS);
    }

    /**
     * 开启异步线程 可携带返回值, 本次调用会和参数线程不在同一个线程.
     *
     * @param runnable 任务线程
     * @param timeout  超时时间 默认单位 秒
     * @param <T>      返回类型参数
     * @return T
     */
    protected <T> T supplyAsync(RunTask<T> runnable, long timeout, TimeUnit unit) {
        try {
            if (0 == timeout) return supplyFuture(runnable).get();
            return supplyFuture(runnable).get(timeout, unit);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Logger.e(e.toString());
            return null;
        }
    }

    /**
     * 开启异步线程 可携带返回值, 本次调用会和参数线程不在同一个线程.
     *
     * @param runnable 任务线程
     * @param <T>      返回类型参数
     * @return T CompletableFuture<T>
     */
    protected <T> CompletableFuture<T> supplyFuture(RunTask<T> runnable) {
        isShutdown();
        return CompletableFuture.supplyAsync(runnable, mThreadPool);
    }

    /**
     * 开启异步线程 可携带返回值, 本次调用会和参数线程不在同一个线程.
     *
     * @param uRunTask 任务线程
     * @param <T>      入参类型,
     * @param <U>      返回类型参数
     * @return U
     */
    protected <T, U> U thenApply(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        try {
            return thenApplyCall(future, uRunTask).get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(e.toString());
            return null;
        }
    }

    protected <T, U> CompletableFuture<U> thenApplyCall(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        return future.thenApply(uRunTask);
    }

    /**
     * 本次调用不会和参数线程不在同一个线程.
     *
     * @param uRunTask 任务线程
     * @param <T>      入参类型,
     * @param <U>      返回类型参数
     * @return U
     */
    protected <T, U> U thenApplyAsync(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        try {
            return thenApplyAsyncCall(future, uRunTask).get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(e.toString());
            return null;
        }
    }

    /**
     * 本次调用不会和参数线程不在同一个线程.
     *
     * @param uRunTask 任务线程
     * @param <T>      入参类型,
     * @param <U>      返回类型参数
     * @return U
     */
    protected <T, U> CompletableFuture<U> thenApplyAsyncCall(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        return future.thenApplyAsync(uRunTask);
    }
}
