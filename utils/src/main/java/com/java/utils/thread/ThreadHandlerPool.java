package com.java.utils.thread;

import android.os.Handler;
import android.os.Looper;

/**
 * @Introduce: Handler线程池.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 全局唯一Handler，可避免内存泄漏。支持切换主线程 移除未完成任务.
 */
public class ThreadHandlerPool {
    private static Handler mHandler;

    protected ThreadHandlerPool() {
        if (null == mHandler) mHandler = new Handler(Looper.getMainLooper());
    }

    protected void postUi(Runnable runnable) {
        isShutdown();
        mHandler.post(runnable);
    }

    protected void postDelay(Runnable runnable, long time) {
        isShutdown();
        mHandler.postDelayed(runnable, time);
    }

    protected void removeTask(Runnable runnable) {
        isShutdown();
        mHandler.removeCallbacks(runnable);
    }

    protected void isShutdown() {
        if (null == mHandler) throw new RuntimeException("mHandler is shutdown");
    }

    protected void closePool() {
        if (null != mHandler) mHandler = null;
    }
}
