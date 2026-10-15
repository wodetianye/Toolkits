package com.java.utils.thread;

import com.java.utils.log.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @Introduce: 自定义任务封装，同时实现Runnable/Callable/Supplier，支持重试、取消、状态记录
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description:
 * 1.统一入口execute()，call/run/get全部代理execute，保证一套业务逻辑
 * 2.支持最大重试次数、重试间隔、任务取消
 * 3.记录任务id、执行结果、异常、状态
 * 【重要】状态使用AtomicReference保证多线程CAS流转，避免volatile变量赋值覆盖竞态
 */
public abstract class RunTask<T> implements Supplier<T>, Callable<T>, Runnable {
    private static final String TAG = "RunTask";

    /** 任务唯一标识，nanoTime+hashCode，尽量保证不重复 */
    private final String taskId;
    /** 任务状态：原子引用，多线程安全流转 */
    private final AtomicReference<TaskStatus> status;

    /** 最大重试次数，0代表不重试 */
    private final int maxRetryCount;
    /** 每次重试间隔毫秒 */
    private final long retryIntervalMs;
    /** 当前已经重试次数 */
    private volatile int currentRetry;

    /** 执行结果，volatile保证多线程可见 */
    private volatile T result;
    /** 执行抛出异常，volatile保证多线程可见 */
    private volatile Throwable throwable;

    public RunTask() {
        this(0, 0L);
    }

    /**
     * @param maxRetryCount 最大重试次数
     * @param retryIntervalMs 重试间隔ms
     */
    public RunTask(int maxRetryCount, long retryIntervalMs) {
        this.taskId = System.nanoTime() + "_" + hashCode();
        this.status = new AtomicReference<>(TaskStatus.WAITING);
        this.maxRetryCount = maxRetryCount;
        this.retryIntervalMs = retryIntervalMs;
    }

    /**
     * 业务实现入口，子类重写，执行业务逻辑
     */
    public abstract T execute() throws Exception;

    /**
     * Callable入口，线程池submit调用
     */
    @Override
    public T call() throws Exception {
        // 如果已经取消，直接返回，不执行业务
        if (status.get() == TaskStatus.CANCELLED) {
            Logger.w(TAG, "task is cancelled skip execute, taskId=" + taskId);
            return result;
        }
        // CAS流转状态：WAITING -> RUNNING，防止重复执行
        if (!status.compareAndSet(TaskStatus.WAITING, TaskStatus.RUNNING)) {
            return result;
        }
        try {
            result = execute();
            status.set(TaskStatus.SUCCESS);
            return result;
        } catch (Throwable t) {
            this.throwable = t;
            status.set(TaskStatus.FAILED);
            Logger.e(TAG, "task execute error, taskId:" + taskId, t);
            throw t;
        }
    }

    /**
     * Runnable入口，线程池execute调用
     * 修复：不再直接抛出异常销毁线程池worker；异常记录到对象内部
     */
    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            // 异常已经记录在throwable，此处不向外抛，避免工作线程退出重建
            Logger.e(TAG, "run() catch exception taskId=" + taskId, e);
        }
    }

    /**
     * Supplier入口，CompletableFuture.supplyAsync调用
     */
    @Override
    public T get() {
        if (status.get() == TaskStatus.CANCELLED) {
            return result;
        }
        try {
            return call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否还可以重试
     * @return true允许加入重试缓冲队列
     */
    public boolean canRetry() {
        return currentRetry < maxRetryCount
                && status.get() != TaskStatus.CANCELLED
                && status.get() != TaskStatus.SUCCESS;
    }

    /**
     * 增加重试计数
     */
    public void incrRetry() {
        currentRetry++;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status.get();
    }

    /**
     * 取消任务，修改状态为CANCELLED
     */
    public void cancel() {
        status.set(TaskStatus.CANCELLED);
    }

    public int getCurrentRetry() {
        return currentRetry;
    }

    public long getRetryIntervalMs() {
        return retryIntervalMs;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public T getResult() {
        return result;
    }
}