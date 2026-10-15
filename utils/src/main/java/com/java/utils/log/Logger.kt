package com.java.utils.log;

import android.util.Log;

/**
 * @Introduce: API.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description:
 */
public class Logger {
    public static final int VERBOSE = Log.VERBOSE;

    public static final int DEBUG = Log.DEBUG;

    public static final int INFO = Log.INFO;

    public static final int WARN = Log.WARN;

    public static final int ERROR = Log.ERROR;

    public static void v(Object args) {
        LogUtils.verbose(args);
    }

    public static void v(String tag, Object... args) {
        LogUtils.verbose(tag, args);
    }

    public static void d(Object args) {
        LogUtils.debug(args);
    }

    public static void d(String tag, Object... args) {
        LogUtils.debug(tag, args);
    }

    public static void i(Object args) {
        LogUtils.info(args);
    }

    public static void i(String tag, Object... args) {
        LogUtils.info(tag, args);
    }

    public static void w(Object args) {
        LogUtils.warn(args);
    }

    public static void w(String tag, Object... args) {
        LogUtils.warn(tag, args);
    }

    public static void e(Object args) {
        LogUtils.error(args);
    }

    public static void e(String tag, Object... args) {
        LogUtils.error(tag, args);
    }

    public static void switchLog(boolean logSwitch) {
        LogUtils.switchLog(logSwitch);
    }

    public static void setLogLevel(int level) {
        LogUtils.setLogLevel(level);
    }

    public static void setDefaultTag(String defaultTag) {
        LogUtils.setDefaultTag(defaultTag);
    }

    public static void initLogUtils(boolean logSwitch, int level) {
        LogUtils.initLogUtils(logSwitch, level);
    }

    public static String getCurrentTagName() {
        return LogUtils.getCurrentTagName();
    }

    public static boolean isDebugLevel() {
        return LogUtils.isDebugLevel();
    }

    public static void printStackTrace(String tag, boolean info) {
        LogUtils.printStackTrace(tag, info);
    }
}
