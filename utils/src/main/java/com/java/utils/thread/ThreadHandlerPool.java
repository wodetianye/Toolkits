package com.java.utils.thread;

import android.os.Handler;
import android.os.Looper;

import com.java.utils.log.Logger;

/**
 * @Introduce: UI线程Handler池，主线程消息投递
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 1.封装MainLooper Handler，用来post/postDelay切换主线程
 * 2.修复closePool直接置null导致空指针；主线程Handler属于系统资源不能销毁
 * 3.修复：isClosed改为static，多实例共享关闭状态，防止多实例调用逻辑错乱
 * 4.构造私有，禁止外部new实例，统一由ThreadManager持有
 */
public class ThreadHandlerPool {
    private static Handler mHandler;
    /**
     * 静态全局关闭标记：所有实例共用同一个关闭状态
     */
    private static volatile boolean isClosed = false;

    /**
     * 私有构造，禁止外部new
     */
    protected ThreadHandlerPool() {
        if (null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper());
        }
    }

    /**
     * 投递Runnable到主线程立即执行
     *
     * @param runnable 任务
     */
    protected void postUi(Runnable runnable) {
        if (isClosed) {
            Logger.w("ThreadHandlerPool", "pool closed skip postUi");
            return;
        }
        if (runnable == null) {
            return;
        }
        mHandler.post(runnable);
    }

    /**
     * 延迟投递主线程任务
     *
     * @param runnable 任务
     * @param time     延迟毫秒
     */
    protected void postDelay(Runnable runnable, long time) {
        if (isClosed) {
            Logger.w("ThreadHandlerPool", "pool closed skip postDelay");
            return;
        }
        if (runnable == null) {
            return;
        }
        mHandler.postDelayed(runnable, time);
    }

    /**
     * 移除已经提交的延迟任务
     *
     * @param runnable 任务对象
     */
    protected void removeTask(Runnable runnable) {
        if (isClosed || runnable == null) return;
        mHandler.removeCallbacks(runnable);
    }

    /**
     * 关闭池，不再接受新消息；不销毁系统MainLooper Handler
     */
    protected void closePool() {
        isClosed = true;
        // 禁止 mHandler=null，消息队列残留消息会继续执行；置null会导致后续调用空指针崩溃
        // MainLooper属于系统资源，不能销毁
    }
}