package com.java.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.java.utils.log.Logger;
import com.java.utils.other.ToastUtils;
import com.java.utils.thread.RunTask;
import com.java.utils.thread.ThreadManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadTestActivity extends AppCompatActivity {
    private static final String TAG = "ThreadTestActivity";
    private ThreadManager mThreadManager;

    private ScheduledFuture mFixDelayFuture;
    private ScheduledFuture mFixRateFuture;
    private Runnable mTestDelayUiRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        mThreadManager = ThreadManager.getInstance();

        // ====================== ThreadPool 业务子线程池测试 ======================
        addBtn(root, "【ThreadPool】runAsync普通Runnable(瓦片模拟)", () -> {
            mThreadManager.runAsync(() -> {
                try {
                    Thread.sleep(1200);
                    Logger.d(TAG, "runAsync瓦片任务完成");
                    mThreadManager.postUi(() -> ToastUtils.showShort("runAsync瓦片完成"));
                } catch (InterruptedException e) {
                    Logger.e(TAG, e);
                }
            });
        });

        addBtn(root, "【ThreadPool】execute(RunTask)阻塞调用", () -> {
            new Thread(() -> {
                RunTask<String> task = new RunTask<String>() {
                    @Override
                    public String execute() throws Exception {
                        Thread.sleep(1500);
                        return "execute阻塞返回结果";
                    }
                };
                String ret = mThreadManager.execute(task);
                Logger.d(TAG, "execute返回：" + ret);
                mThreadManager.postUi(() -> ToastUtils.showShort("execute结果:" + ret));
            }).start();
        });

        addBtn(root, "【ThreadPool】executeNewRun(RunTask)旧阻塞接口", () -> {
            new Thread(() -> {
                RunTask<Integer> task = new RunTask<Integer>() {
                    @Override
                    public Integer execute() throws Exception {
                        Thread.sleep(1000);
                        return 666;
                    }
                };
                Integer res = mThreadManager.executeNewRun(task);
                Logger.d(TAG, "executeNewRun res=" + res);
                mThreadManager.postUi(() -> ToastUtils.showShort("executeNewRun:" + res));
            }).start();
        });

        addBtn(root, "【ThreadPool】supplyFuture非阻塞路线计算", () -> {
            RunTask<Long> computeTask = new RunTask<Long>() {
                @Override
                public Long execute() throws Exception {
                    long sum = 0;
                    for (long i = 0; i < 200_000_000L; i++) {
                        sum += i;
                    }
                    return sum;
                }
            };
            CompletableFuture<Long> future = mThreadManager.supplyFuture(computeTask);
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    Logger.e(TAG, "计算失败", throwable);
                    mThreadManager.postUi(() -> ToastUtils.showShort("计算失败"));
                } else {
                    Logger.d(TAG, "路线计算结果:" + result);
                    mThreadManager.postUi(() -> ToastUtils.showShort("计算完成"));
                }
            });
        });

        addBtn(root, "【ThreadPool】批量25个瓦片压测", () -> {
            for (int i = 0; i < 25; i++) {
                int idx = i;
                mThreadManager.runAsync(() -> {
                    try {
                        Thread.sleep(400);
                        Logger.d(TAG, "瓦片任务 " + idx + " 执行结束");
                    } catch (InterruptedException e) {
                        Logger.e(TAG, e);
                    }
                });
            }
            ToastUtils.showShort("25个瓦片任务已提交");
        });

        addBtn(root, "【ThreadPool】打印线程池状态", () -> {
            int active = mThreadManager.getPoolActiveCount();
            int queueSize = mThreadManager.getPoolQueueSize();
            String info = "活跃线程=" + active + "\n队列待执行=" + queueSize;
            Logger.d(TAG, info);
            ToastUtils.showShort(info);
        });

        // ====================== ThreadHandlerPool 主线程Handler池测试 ======================
        addBtn(root, "【ThreadHandlerPool】postUi切主线程", () -> {
            mThreadManager.runAsync(() -> {
                Logger.d(TAG, "子线程执行完毕，切UI线程弹窗");
                mThreadManager.postUi(() -> ToastUtils.showShort("postUi：已经切到主线程"));
            });
        });

        addBtn(root, "【ThreadHandlerPool】postDelay延迟3s弹窗", () -> {
            mTestDelayUiRunnable = () -> {
                Logger.d(TAG, "postDelay延迟任务执行");
                ToastUtils.showShort("postDelay 3秒延迟弹窗");
            };
            mThreadManager.postDelay(mTestDelayUiRunnable, 3000);
            ToastUtils.showShort("已提交3s延迟UI任务");
        });

        addBtn(root, "【ThreadHandlerPool】removeHandleTask移除上面延迟任务", () -> {
            if(mTestDelayUiRunnable != null){
                mThreadManager.removeHandleTask(mTestDelayUiRunnable);
                ToastUtils.showShort("已移除延迟UI任务，不会弹窗");
            }else{
                ToastUtils.showShort("没有可移除的任务");
            }
        });

        addBtn(root, "【ThreadHandlerPool】打印当前线程信息", () -> {
            boolean main = mThreadManager.isMainThread();
            String threadInfo = mThreadManager.getThreadInfo();
            Logger.d(TAG, threadInfo);
            ToastUtils.showShort("isMain:" + main);
        });

        // ====================== ThreadScheduledPool 定时调度池测试 ======================
        addBtn(root, "【ThreadScheduledPool】asyncDelay延迟2s执行子线程任务", () -> {
            mThreadManager.asyncDelay(() -> {
                Logger.d(TAG, "ScheduledPool延迟2s子线程任务执行");
                mThreadManager.postUi(() -> ToastUtils.showShort("定时延迟任务触发"));
            },2, TimeUnit.SECONDS);
            ToastUtils.showShort("已提交2s定时延迟任务");
        });

        addBtn(root, "【ThreadScheduledPool】启动固定延迟(上轮结束隔2s再跑)", () -> {
            if(mFixDelayFuture != null && !mFixDelayFuture.isDone()){
                ToastUtils.showShort("固定延迟任务已经在运行");
                return;
            }
            mFixDelayFuture = mThreadManager.asyncWithFixDelay(() -> {
                Logger.d(TAG,"固定延迟任务执行");
                mThreadManager.postUi(() -> ToastUtils.showShort("固定延迟触发"));
            },0,2,TimeUnit.SECONDS);
            ToastUtils.showShort("启动固定延迟任务，间隔2s");
        });

        addBtn(root, "【ThreadScheduledPool】启动固定速率(每3s跑一次，不管上轮是否结束)", () -> {
            if(mFixRateFuture != null && !mFixRateFuture.isDone()){
                ToastUtils.showShort("固定速率任务已经在运行");
                return;
            }
            mFixRateFuture = mThreadManager.asyncAtFixDelay(() -> {
                Logger.d(TAG,"固定速率任务执行");
                mThreadManager.postUi(() -> ToastUtils.showShort("固定速率触发"));
            },0,3,TimeUnit.SECONDS);
            ToastUtils.showShort("启动固定速率任务，间隔3s");
        });

        addBtn(root, "【ThreadScheduledPool】取消全部定时任务", () -> {
            if(mFixDelayFuture != null){
                mThreadManager.cancelDelayRun(mFixDelayFuture);
                mFixDelayFuture = null;
            }
            if(mFixRateFuture != null){
                mThreadManager.cancelDelayRun(mFixRateFuture);
                mFixRateFuture = null;
            }
            ToastUtils.showShort("全部定时任务已取消");
        });

        setContentView(root);
    }

    private void addBtn(LinearLayout parent, String text, Runnable action) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(22f);
        btn.setPadding(20, 20, 20, 20);
        btn.setOnClickListener(v -> action.run());
        parent.addView(btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity销毁，停止定时任务，不要调用unInitUtils（全局单例）
        if(mFixDelayFuture != null){
            mThreadManager.cancelDelayRun(mFixDelayFuture);
            mFixDelayFuture = null;
        }
        if(mFixRateFuture != null){
            mThreadManager.cancelDelayRun(mFixRateFuture);
            mFixRateFuture = null;
        }
        if(mTestDelayUiRunnable != null){
            mThreadManager.removeHandleTask(mTestDelayUiRunnable);
        }
    }
}