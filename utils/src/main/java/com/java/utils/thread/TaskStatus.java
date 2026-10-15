package com.java.utils.thread;

/**
 * @Introduce: .
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description:
 */
public enum TaskStatus {
    WAITING, // 等待入队/等待执行
    RUNNING, // 正在执行业务逻辑
    SUCCESS, // 执行成功完成
    FAILED, // 执行抛出异常失败
    REJECTED, // 被拒绝策略丢弃
    CANCELLED // 用户手动取消任务
}