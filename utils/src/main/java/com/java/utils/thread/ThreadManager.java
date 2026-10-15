package com.java.utils.thread;

import android.content.Context;
import android.os.Looper;

import com.java.utils.BaseUtils;
import com.java.utils.log.Logger;
import com.java.utils.other.ConvertUtils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Introduce: 线程工具总入口，单例门面类
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 聚合：普通子线程池、UIHandler、Scheduled定时池
 * 使用流程：App初始化调用initUtils(context)；退出页面/组件调用unInitUtils释放资源
 */
public class ThreadManager implements BaseUtils {
    private ThreadPool mThreadPool;
    private ThreadHandlerPool mThreadHandler;
    private ThreadScheduledPool mThreadScheduled;
    private volatile boolean mInited = false;

    private ThreadManager() {
    }

    /**
     * 初始化线程池，Application onCreate调用
     */
    @Override
    public void initUtils(@NotNull Context context) {
        if (mInited) {
            Logger.w("ThreadManager", "already inited, skip repeat init");
            return;
        }
        mThreadPool = new ThreadPool(context);
        mThreadHandler = new ThreadHandlerPool();
        mThreadScheduled = new ThreadScheduledPool(context);
        mInited = true;
    }

    /**
     * 反初始化，释放全部线程池资源，组件退出调用
     */
    @Override
    public void unInitUtils() {
        if (!mInited) {
            return;
        }
        if (mThreadPool != null) {
            mThreadPool.closePool();
        }
        if (mThreadHandler != null) {
            mThreadHandler.closePool();
        }
        if (mThreadScheduled != null) {
            mThreadScheduled.closePool();
        }
        mThreadPool = null;
        mThreadHandler = null;
        mThreadScheduled = null;
        mInited = false;
    }

    /**
     * 是否完成初始化
     */
    public boolean isInited() {
        return mInited;
    }

    public int getPoolActiveCount() {
        if (!mInited || mThreadPool == null) return 0;
        return mThreadPool.getActiveCount();
    }

    public int getPoolQueueSize() {
        if (!mInited || mThreadPool == null) return 0;
        return mThreadPool.getQueueSize();
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

    /**
     * 判断当前是否主线程
     */
    public boolean isMainThread() {
        return ConvertUtils.equals(Looper.getMainLooper().getThread(), getCurrentThread());
    }

    /**
     * 获取当前线程调试信息
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

    /**
     * 移除UI延迟任务
     */
    public void removeHandleTask(Runnable runnable) {
        if (!mInited || mThreadHandler == null || runnable == null) return;
        mThreadHandler.removeTask(runnable);
    }

    /**
     * 切主线程执行
     */
    public void postUi(Runnable runnable) {
        if (!mInited || mThreadHandler == null || runnable == null) return;
        mThreadHandler.postUi(runnable);
    }

    /**
     * 主线程延迟执行
     */
    public void postDelay(Runnable runnable, long time) {
        if (!mInited || mThreadHandler == null || runnable == null) return;
        mThreadHandler.postDelay(runnable, time);
    }

    /**
     * 普通Runnable提交子线程，无返回
     */
    public void execute(Runnable runnable) {
        if (!mInited || mThreadPool == null || runnable == null) return;
        mThreadPool.execute(runnable);
    }

    /**
     * runAsync，异步无返回
     */
    public void runAsync(Runnable runnable) {
        if (!mInited || mThreadPool == null || runnable == null) return;
        mThreadPool.runAsync(runnable);
    }

    /**
     * ⚠️阻塞调用，不要主线程调用
     */
    public <T> T execute(RunTask<T> runnable) {
        if (!mInited || mThreadPool == null || runnable == null) return null;
        return mThreadPool.execute(runnable);
    }

    /**
     * ⚠️阻塞调用，不要主线程调用
     */
    public <T> T executeNewRun(RunTask<T> runnable) {
        if (!mInited || mThreadPool == null || runnable == null) return null;
        return mThreadPool.executeNewRun(runnable);
    }

    /**
     * ⚠️阻塞API，优先supplyFuture
     */
    public <T> T supplyAsync(RunTask<T> runnable) {
        if (!mInited || mThreadPool == null || runnable == null) return null;
        return mThreadPool.supplyAsync(runnable, 0, TimeUnit.SECONDS);
    }

    public <T> T supplyAsync(RunTask<T> runnable, long timeout) {
        if (!mInited || mThreadPool == null || runnable == null) return null;
        return mThreadPool.supplyAsync(runnable, timeout, TimeUnit.SECONDS);
    }

    public <T> T supplyAsync(RunTask<T> runnable, long timeout, TimeUnit unit) {
        if (!mInited || mThreadPool == null || runnable == null) return null;
        return mThreadPool.supplyAsync(runnable, timeout, unit);
    }

    /**
     * 【推荐使用】获取CompletableFuture，非阻塞回调
     */
    public <T> CompletableFuture<T> supplyFuture(RunTask<T> runnable) {
        if (!mInited || mThreadPool == null) {
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("ThreadManager unInited"));
            return future;
        }
        return mThreadPool.supplyFuture(runnable);
    }

    public <T, U> U supplyFuture(RunTask<T> runnable, RunFunction<T, U> uRunTask) {
        if (!mInited || mThreadPool == null || runnable == null || uRunTask == null) return null;
        CompletableFuture<T> completableFuture = supplyFuture(runnable);
        return thenApply(completableFuture, uRunTask);
    }

    public <T, U> U thenApply(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        if (!mInited || mThreadPool == null || future == null || uRunTask == null) return null;
        return mThreadPool.thenApply(future, uRunTask);
    }

    public <T, U> U thenApply(CompletableFuture<T> future, RunFunction<T, U> uRunTask, long timeout) {
        if (!mInited || mThreadPool == null || future == null || uRunTask == null) return null;
        return mThreadPool.thenApply(future, uRunTask, timeout, TimeUnit.SECONDS);
    }

    public <T, U> U thenApply(CompletableFuture<T> future, RunFunction<T, U> uRunTask, long timeout, TimeUnit unit) {
        if (!mInited || mThreadPool == null || future == null || uRunTask == null) return null;
        return mThreadPool.thenApply(future, uRunTask, timeout, unit);
    }

    public <T, U> CompletableFuture<U> thenApplyCall(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        if (!mInited || mThreadPool == null) {
            CompletableFuture<U> f = new CompletableFuture<>();
            f.completeExceptionally(new IllegalStateException("ThreadManager unInited"));
            return f;
        }
        return mThreadPool.thenApplyCall(future, uRunTask);
    }

    public <T, U> U thenApplyAsync(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        if (!mInited || mThreadPool == null || future == null || uRunTask == null) return null;
        return mThreadPool.thenApplyAsync(future, uRunTask);
    }

    protected <T, U> U thenApplyAsync(CompletableFuture<T> future, RunFunction<T, U> uRunTask, long timeout) {
        if (!mInited || mThreadPool == null || future == null || uRunTask == null) return null;
        return mThreadPool.thenApplyAsync(future, uRunTask, timeout, TimeUnit.SECONDS);
    }

    protected <T, U> U thenApplyAsync(CompletableFuture<T> future, RunFunction<T, U> uRunTask, long timeout, TimeUnit unit) {
        if (!mInited || mThreadPool == null || future == null || uRunTask == null) return null;
        return mThreadPool.thenApplyAsync(future, uRunTask, timeout, unit);
    }

    public <T, U> CompletableFuture<U> thenApplyAsyncCall(CompletableFuture<T> future, RunFunction<T, U> uRunTask) {
        if (!mInited || mThreadPool == null) {
            CompletableFuture<U> f = new CompletableFuture<>();
            f.completeExceptionally(new IllegalStateException("ThreadManager unInited"));
            return f;
        }
        return mThreadPool.thenApplyAsyncCall(future, uRunTask);
    }

    /**
     * 延迟单次执行任务
     */
    public void asyncDelay(Runnable run, long start) {
        if (!mInited || mThreadScheduled == null || run == null) return;
        asyncDelay(run, start, TimeUnit.SECONDS);
    }

    public void asyncDelay(Runnable run, long start, TimeUnit unit) {
        if (!mInited || mThreadScheduled == null || run == null) return;
        mThreadScheduled.schedule(run, start, unit);
    }

    /**
     * 固定延迟：上一轮执行完，间隔再执行
     */
    public ScheduledFuture asyncWithFixDelay(Runnable run, long start, long interval) {
        if (!mInited || mThreadScheduled == null || run == null) return null;
        return asyncWithFixDelay(run, start, interval, TimeUnit.SECONDS);
    }

    public ScheduledFuture asyncWithFixDelay(Runnable run, long start, long interval, TimeUnit unit) {
        if (!mInited || mThreadScheduled == null || run == null) return null;
        return mThreadScheduled.scheduleWithFixed(run, start, interval, unit);
    }

    /**
     * 固定速率：按时间点执行，不管上一轮是否结束
     */
    public ScheduledFuture asyncAtFixDelay(Runnable run, long start, long interval) {
        if (!mInited || mThreadScheduled == null || run == null) return null;
        return asyncAtFixDelay(run, start, interval, TimeUnit.SECONDS);
    }

    public ScheduledFuture asyncAtFixDelay(Runnable run, long start, long interval, TimeUnit unit) {
        if (!mInited || mThreadScheduled == null || run == null) return null;
        return mThreadScheduled.scheduleAtFixedRate(run, start, interval, unit);
    }

    /**
     * 取消定时任务
     */
    public void cancelDelayRun(ScheduledFuture scheduledFuture) {
        if (!mInited || mThreadScheduled == null || scheduledFuture == null) return;
        mThreadScheduled.cancelSchedule(scheduledFuture);
    }

    /**
     * 当前线程sleep，捕获InterruptedException，恢复中断标记
     */
    public void sleep(int time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            Logger.e(e.toString());
            Thread.currentThread().interrupt();
        }
    }

    public static ThreadManager getInstance() {
        return Helper.tu;
    }

    private static class Helper {
        private static final ThreadManager tu = new ThreadManager();
    }
}