package com.java.utils.thread;

import com.java.utils.log.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Introduce: 重试优先插队拒绝策略，用于ThreadPoolExecutor队列满时处理任务
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description:
 * 业务逻辑：
 * 1.线程池队列满触发rejectedExecution；
 * 2.如果是RunTask并且允许重试，放入有界retryBuffer缓冲队列；
 * 3.后台retryWorker循环取出缓冲任务尝试重新提交到线程池队列；
 * 4.缓冲队列容量有限，防止OOM；
 * 修复点：
 * ①普通Runnable提交满队列打明确告警；
 * ②使用RunTask的retryIntervalMs做重试间隔；
 * ③shutdown等待worker线程结束，避免线程残留；
 */
public class PriorityRejectPolicy implements RejectedExecutionHandler {
    private static final String TAG = "PriorityRejectPolicy";
    private final ThreadPoolExecutor targetPool;
    private final BlockingQueue<RunTask<?>> retryBuffer;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread retryWorker;

    public PriorityRejectPolicy(ThreadPoolExecutor targetPool, int bufferCapacity) {
        this.targetPool = targetPool;
        this.retryBuffer = new ArrayBlockingQueue<>(bufferCapacity);
        retryWorker = new Thread(this::retryLoop, "pool-retry-priority-worker");
        retryWorker.setDaemon(true);
        retryWorker.start();
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!running.get()) {
            Logger.e(TAG, "rejectPolicy shutdown, discard task");
            return;
        }
        // 只支持RunTask类型任务重试；普通Runnable直接丢弃
        if (!(r instanceof RunTask<?> task)) {
            Logger.w(TAG, "unknown task type(not RunTask), discard runnable");
            return;
        }
        if (!task.canRetry()) {
            Logger.e(TAG, "max retry reach, final reject taskId=" + task.getTaskId());
            task.cancel();
            return;
        }
        boolean ok = retryBuffer.offer(task);
        if (!ok) {
            Logger.e(TAG, "retry buffer full, discard taskId=" + task.getTaskId());
            task.cancel();
        } else {
            Logger.w(TAG, "pool full, add to retry buffer taskId=" + task.getTaskId() + ",retry=" + task.getCurrentRetry());
        }
    }

    /**
     * 后台重试循环线程
     */
    private void retryLoop() {
        while (running.get()) {
            try {
                RunTask<?> task = retryBuffer.poll(200, TimeUnit.MILLISECONDS);
                if (task == null) {
                    continue;
                }
                // 任务已经取消，跳过重试
                if (task.getStatus() == TaskStatus.CANCELLED) {
                    Logger.i(TAG, "task cancelled skip retry taskId=" + task.getTaskId());
                    continue;
                }
                // 修复：使用任务配置的重试间隔sleep
                long sleepMs = task.getRetryIntervalMs();
                if (sleepMs > 0) {
                    TimeUnit.MILLISECONDS.sleep(sleepMs);
                }
                // 尝试插队放入线程池队列
                boolean offer = targetPool.getQueue().offer(task, 0, TimeUnit.MILLISECONDS);
                if (offer) {
                    Logger.i(TAG, "retry success submit taskId=" + task.getTaskId());
                } else {
                    task.incrRetry();
                    // 再次放回缓冲队列
                    boolean reAdd = retryBuffer.offer(task);
                    if (!reAdd) {
                        Logger.e(TAG, "retry buffer full again discard taskId=" + task.getTaskId());
                        task.cancel();
                    }
                }
            } catch (InterruptedException ie) {
                Logger.i(TAG, "retry worker interrupted exit");
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable t) {
                Logger.e(TAG, "retry loop exception", t);
            }
        }
        Logger.i(TAG, "retry worker thread exit");
    }

    /**
     * 关闭拒绝策略，停止重试线程，清空缓冲队列
     */
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            if (retryWorker != null) {
                retryWorker.interrupt();
                try {
                    // 等待线程退出，最多500ms
                    retryWorker.join(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            retryBuffer.clear();
        }
    }
}