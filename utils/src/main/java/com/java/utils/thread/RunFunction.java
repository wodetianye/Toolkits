package com.java.utils.thread;

import java.util.function.Function;

/**
 * @Introduce: 自定义函数接口，用于CompletableFuture thenApply回调
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description:
 * @param <T> 输入参数类型
 * @param <U> 返回结果类型
 */
@FunctionalInterface
public interface RunFunction<T, U> extends Function<T, U> {
    @Override
    U apply(T t);
}