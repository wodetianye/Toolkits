package com.java.utils.thread;

import android.content.Context;
import android.os.Looper;

import com.java.utils.log.Logger;

import java.util.concurrent.*;

/**
 * @Introduce: 业务子线程池封装
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 1.核心线程数做上限保护，避免corePoolSize大于maximumPoolSize；allowCoreThreadTimeOut允许核心线程空闲回收
 * 2.有界队列ArrayBlockingQueue，队列满走PriorityRejectPolicy重试拒绝策略
 * 3.提供阻塞同步API、CompletableFuture异步回调API
 * 【重要】execute、supplyAsync、thenApplyXXX不带Call后缀是阻塞API，严禁主线程调用，会ANR
 */
public class ThreadPool {
    private static final String TAG = "ThreadPool";
    /**
     * CPU 逻辑核数(包含大小核)
     */
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    /**
     * 默认核心线程数
     */
    private static final int DEFAULT_CORE_SIZE = 4;
    /**
     * 线程最大并发上限，保护车载大小核SOC，core不能超过该值
     */
    private static final int MAX_POOL_SIZE_LIMIT = 8;
    /**
     * 队列最大值
     */
    private static final int QUEUE_NUM = 60;
    /**
     * 重试缓冲队列大小
     */
    private static final int RETRY_BUFFER_SIZE = 10;
    /**
     * 线程保活时间 秒
     */
    private static final long KEEP_ALIVE_TIME = 20L;

    private ThreadPoolExecutor mThreadPool;
    private PriorityRejectPolicy rejectPolicy;

    protected ThreadPool(Context context) {
        // 核心线程不能超过最大线程上限，防止coreSize > maxSize触发ThreadPoolExecutor IllegalArgumentException
        int coreSize = Math.min(Math.max(DEFAULT_CORE_SIZE, CPU_NUM), MAX_POOL_SIZE_LIMIT);
        int maxSize = Math.min(coreSize * 2, MAX_POOL_SIZE_LIMIT);

        BlockingQueue<Runnable> mWorkQueue = new ArrayBlockingQueue<>(QUEUE_NUM);
        ThreadFactoryPxy mFactoryPxy = new ThreadFactoryPxy(maxSize, Logger.getCurrentTagName(), context.getPackageName());
        mThreadPool = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                mWorkQueue,
                mFactoryPxy
        );
        rejectPolicy = new PriorityRejectPolicy(mThreadPool, RETRY_BUFFER_SIZE);
        mThreadPool.setRejectedExecutionHandler(rejectPolicy);
        // 允许空闲核心线程回收，降低常驻内存
        mThreadPool.allowCoreThreadTimeOut(true);
    }

    /**
     * 获取活跃正在执行任务的线程数
     */
    public int getActiveCount() {
        if (isShutdown()) return 0;
        return mThreadPool.getActiveCount();
    }

    /**
     * 获取等待队列任务数量
     */
    public int getQueueSize() {
        if (isShutdown()) return 0;
        return mThreadPool.getQueue().size();
    }

    /**
     * 关闭线程池
     * 1.先关闭线程池拒绝提交新任务
     * 2.再关闭拒绝策略重试worker
     * 3.shutdown等待3秒，如果没有终止，执行shutdownNow强制中断
     */
    protected void closePool() {
        if (null == mThreadPool) return;
        mThreadPool.shutdown();
        rejectPolicy.shutdown();
        try {
            if (!mThreadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                mThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            mThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        // 切断拒绝策略引用，帮助GC
        rejectPolicy = null;
        mThreadPool = null;
    }

    /**
     * 判断线程池是否已经关闭
     *
     * @return true代表关闭，不能提交新任务
     */
    protected boolean isShutdown() {
        if (null == mThreadPool || mThreadPool.isShutdown() || mThreadPool.isTerminated()) {
            Logger.w(TAG, "ThreadPool is shutdown");
            return true;
        }
        return false;
    }

    /**
     * 普通Runnable提交，无返回
     */
    protected void execute(Runnable runnable) {
        if (isShutdown() || runnable == null) return;
        mThreadPool.execute(runnable);
    }

    /**
     * ⚠️【同步阻塞API，禁止主线程调用，会ANR！兼容旧业务保留】
     *
     * @param runTask 任务
     * @return 执行结果
     */
    protected <T> T execute(RunTask<T> runTask) {
        if (isShutdown() || runTask == null) return null;
        // 增加主线程强校验
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logger.e(TAG, "execute(RunTask) run on main thread! will ANR!!!");
            return null;
        }
        Logger.w(TAG, "execute(RunTask) is blocking api, avoid call on main thread!");
        FutureTask<T> futureTask = new FutureTask<>(runTask);
        mThreadPool.submit(futureTask);
        try {
            return futureTask.get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(TAG, e);
            // 注意：futureTask.cancel(true)仅修改future状态，不能终止已运行任务
            futureTask.cancel(true);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * ⚠️【同步阻塞API，禁止主线程调用，会ANR！兼容旧业务保留】
     */
    protected <T> T executeNewRun(RunTask<T> runTask) {
        if (isShutdown() || runTask == null) return null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logger.e(TAG, "executeNewRun run on main thread! will ANR!!!");
            return null;
        }
        Logger.w(TAG, "executeNewRun is blocking api, avoid call on main thread!");
        try {
            return mThreadPool.submit((Callable<T>) runTask).get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(TAG, e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * 异步执行Runnable，无返回，CompletableFuture包装
     */
    protected void runAsync(Runnable runnable) {
        if (isShutdown() || runnable == null) return;
        CompletableFuture.runAsync(runnable, mThreadPool);
    }

    /**
     * ⚠️同步阻塞，携带返回值；优先使用supplyFuture
     *
     * @param runnable 任务
     * @param timeout  超时时间
     * @param unit     时间单位
     * @param <T>      返回值类型
     * @return 执行结果；异常/超时/池关闭返回null
     */
    protected <T> T supplyAsync(RunTask<T> runnable, long timeout, TimeUnit unit) {
        if (isShutdown() || runnable == null) return null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logger.e(TAG, "supplyAsync(block) run on main thread! will ANR!!!");
            return null;
        }
        Logger.w(TAG, "supplyAsync(block) is blocking api, suggest use supplyFuture");
        try {
            CompletableFuture<T> future = supplyFuture(runnable);
            if (0 == timeout) return future.get();
            return future.get(timeout, unit);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Logger.e(TAG, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    /**
     * 真正非阻塞接口，返回CompletableFuture，业务优先使用这个！
     */
    protected <T> CompletableFuture<T> supplyFuture(RunTask<T> runnable) {
        if (isShutdown()) {
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("pool shutdown"));
            return future;
        }
        if (runnable == null) {
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(new NullPointerException("runnable is null"));
            return future;
        }
        return CompletableFuture.supplyAsync(runnable, mThreadPool);
    }

    /**
     * ⚠️【同步阻塞API，禁止主线程调用，会ANR！】
     * 链式转换，转换逻辑执行在上游任务完成的线程，**不会提交到本线程池**
     * 优先使用非阻塞{@link #thenApplyCall(CompletableFuture, RunFunction)}
     * 开启异步线程 可携带返回值, 本次调用会和参数线程在同一个线程.
     */
    protected <T, U> U thenApply(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        if (isShutdown() || future == null || uRunTask == null) return null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logger.e(TAG, "thenApply run on main thread ,will ANR!");
            return null;
        }
        Logger.w(TAG, "thenApply is blocking api, suggest use thenApplyCall");
        CompletableFuture<U> completableFuture = thenApplyCall(future, uRunTask);
        try {
            return completableFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(TAG, e);
            completableFuture.cancel(true);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * ⚠️【同步阻塞API，禁止主线程调用，会ANR！】
     * 链式转换带超时；转换逻辑执行在上游任务完成的线程，**不会提交到本线程池**
     * 开启异步线程 可携带返回值, 本次调用会和参数线程在同一个线程.
     *
     * @param future   上游future
     * @param uRunTask 转换函数
     * @param timeout  超时时间
     * @param unit     时间单位
     * @return 转换结果；异常/超时/池关闭返回null
     * <p>注意：cancel(true)仅修改Future状态；不能终止已经正在运行的任务，业务内部需要检测isInterrupted()自行退出</p>
     */
    protected <T, U> U thenApply(CompletableFuture<T> future, RunFunction<T, U> uRunTask, long timeout, TimeUnit unit) {
        if (isShutdown() || future == null || uRunTask == null) return null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logger.e(TAG, "thenApply(timeout) run on main thread ,will ANR!");
            return null;
        }
        Logger.w(TAG, "thenApply is blocking api, suggest use thenApplyCall");
        CompletableFuture<U> completableFuture = thenApplyCall(future, uRunTask);
        try {
            return completableFuture.get(timeout, unit);
        } catch (TimeoutException e) {
            Logger.e(TAG, "thenApply timeout");
            completableFuture.cancel(true);
            return null;
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(TAG, "thenApply error", e);
            completableFuture.cancel(true);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    /**
     * 【非阻塞】链式转换，返回CompletableFuture；
     * 转换逻辑运行在上游任务完成的线程，**不会提交到本业务线程池**
     * 开启异步线程 可携带返回值, 本次调用会和参数线程在同一个线程.
     */
    protected <T, U> CompletableFuture<U> thenApplyCall(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        if (future == null || uRunTask == null) {
            CompletableFuture<U> f = new CompletableFuture<>();
            f.completeExceptionally(new NullPointerException("param null"));
            return f;
        }
        return future.thenApply(uRunTask);
    }


    /**
     * ⚠️【同步阻塞API，禁止主线程调用，会ANR！】
     * 链式异步转换，转换任务会提交到本线程池执行；优先使用{@link #thenApplyAsyncCall(CompletableFuture, RunFunction)}
     * 本次调用会和参数线程不在同一个线程
     */
    protected <T, U> U thenApplyAsync(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        if (isShutdown() || future == null || uRunTask == null) return null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logger.e(TAG, "thenApplyAsync(block) run on main thread ,will ANR!");
            return null;
        }
        Logger.w(TAG, "thenApplyAsync(block) is blocking api, suggest use thenApplyAsyncCall");
        try {
            return thenApplyAsyncCall(future, uRunTask).get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.e(TAG, e);
            Thread.currentThread().interrupt();
            return null;
        }
    }


    /**
     * ⚠️【同步阻塞API，禁止主线程调用，会ANR！】
     * 异步链式转换，带超时阻塞等待结果；转换任务提交到本线程池
     * 本次调用会和参数线程不在同一个线程
     *
     * @param future   上游CompletableFuture
     * @param uRunTask 转换函数
     * @param timeout  超时时间
     * @param unit     时间单位
     * @return 转换后结果；异常/超时/池关闭返回null
     * <p>注意：cancel(true) 仅仅修改Future状态；不能终止已经正在运行的任务，业务内部检测isInterrupted()自行退出</p>
     */
    protected <T, U> U thenApplyAsync(CompletableFuture<T> future, RunFunction<T, U> uRunTask, long timeout, TimeUnit unit) {
        if (isShutdown() || future == null || uRunTask == null) {
            return null;
        }
        // 主线程强校验，打印告警，防止ANR
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logger.e(TAG, "thenApplyAsync(timeout) run on main thread! will ANR!!!");
            return null;
        }
        CompletableFuture<U> completableFuture = thenApplyAsyncCall(future, uRunTask);
        try {
            return completableFuture.get(timeout, unit);
        } catch (TimeoutException e) {
            Logger.e(TAG, "thenApplyAsync timeout");
            completableFuture.cancel(true);
            return null;
        } catch (ExecutionException e) {
            Logger.e(TAG, "thenApplyAsync execute exception", e);
            completableFuture.cancel(true);
            return null;
        } catch (InterruptedException e) {
            Logger.e(TAG, "thenApplyAsync interrupted", e);
            completableFuture.cancel(true);
            Thread.currentThread().interrupt(); // 恢复中断标记，规范
            return null;
        }
    }


    /**
     * 【非阻塞】异步链式转换；转换任务提交到本线程池执行
     * 本次调用会和参数线程不在同一个线程
     *
     * @param future   上游CompletableFuture
     * @param uRunTask 转换函数
     * @return CompletableFuture 转换结果Future对象，调用方自行处理get/超时/回调whenComplete
     */
    protected <T, U> CompletableFuture<U> thenApplyAsyncCall(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        if (isShutdown()) {
            CompletableFuture<U> f = new CompletableFuture<>();
            f.completeExceptionally(new IllegalStateException("pool shutdown or param null"));
        }
        if (isShutdown() || future == null || uRunTask == null) {
            CompletableFuture<U> f = new CompletableFuture<>();
            f.completeExceptionally(new NullPointerException("param null"));
            return f;
        }
        return future.thenApplyAsync(uRunTask, mThreadPool);
    }
}