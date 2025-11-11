package com.java.utils.thread;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * @Introduce: .
 * @Author: Lww
 * @Date: 2023/10/12
 * @Description:
 */
public class RunTask<T> implements Supplier<T>, Callable<T>, Runnable {

    @Override
    public T get() {
        return null;
    }

    @Override
    public T call() throws Exception {
        return null;
    }

    @Override
    public void run() {

    }
}
