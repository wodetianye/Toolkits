package com.java.utils.log

import android.util.Log

/**
 * @Introduce: 日志对外 API.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 封装 LogUtils，对外只暴露简洁的 v / d / i / w / e 等方法.
 */
object Logger {
    const val VERBOSE = Log.VERBOSE
    const val DEBUG = Log.DEBUG
    const val INFO = Log.INFO
    const val WARN = Log.WARN
    const val ERROR = Log.ERROR

    /**
     * Verbose 日志（无 tag）。
     *
     * 例：Logger.v("debug message")
     */
    @JvmStatic
    fun v(args: Any?) = LogUtils.verbose(args)

    /**
     * Verbose 日志（指定 tag）。
     *
     * 例：Logger.v("TAG", "debug message")
     */
    @JvmStatic
    fun v(tag: String?, vararg args: Any?) = LogUtils.verbose(tag, *args)

    @JvmStatic
    fun d(args: Any?) = LogUtils.debug(args)

    @JvmStatic
    fun d(tag: String?, vararg args: Any?) = LogUtils.debug(tag, *args)

    @JvmStatic
    fun i(args: Any?) = LogUtils.info(args)

    @JvmStatic
    fun i(tag: String?, vararg args: Any?) = LogUtils.info(tag, *args)

    @JvmStatic
    fun w(args: Any?) = LogUtils.warn(args)

    @JvmStatic
    fun w(tag: String?, vararg args: Any?) = LogUtils.warn(tag, *args)

    @JvmStatic
    fun e(args: Any?) = LogUtils.error(args)

    @JvmStatic
    fun e(tag: String?, vararg args: Any?) = LogUtils.error(tag, *args)

    /**
     * 日志总开关。
     *
     * 例：Logger.switchLog(false) -> 关闭日志
     */
    @JvmStatic
    fun switchLog(logSwitch: Boolean) = LogUtils.switchLog(logSwitch)

    /**
     * 设置日志级别。
     *
     * 例：Logger.setLogLevel(Log.WARN)
     */
    @JvmStatic
    fun setLogLevel(level: Int) = LogUtils.setLogLevel(level)

    /**
     * 设置默认 Tag。
     *
     * 例：Logger.setDefaultTag("MyApp")
     */
    @JvmStatic
    fun setDefaultTag(defaultTag: String?) = LogUtils.setDefaultTag(defaultTag)

    /**
     * 设置堆栈过滤包名。
     *
     * 例：Logger.setStackTraceFilter("com.yourpackage")
     */
    @JvmStatic
    fun setStackTraceFilter(packageName: String?) =
        LogUtils.setStackTraceFilter(packageName)

    /**
     * 当前是否为 DEBUG 级别。
     *
     * 例：Logger.isDebugLevel() -> true
     */
    @JvmStatic
    fun isDebugLevel(): Boolean = LogUtils.isDebugLevel()

    /**
     * 打印当前堆栈。
     *
     * 例：Logger.printStackTrace("TAG", true)
     */
    @JvmStatic
    fun printStackTrace(tag: String?, info: Boolean) =
        LogUtils.printStackTrace(tag, info)

    @JvmStatic
    fun getCurrentTagName(): String = LogUtils.getCurrentTagName()

    /**
     * 初始化日志工具。
     *
     * 例：Logger.initLogUtils(true, Log.DEBUG, "MyApp")
     */
    @JvmStatic
    @JvmOverloads
    fun initLogUtils(logSwitch: Boolean, level: Int, tag: String? = null) =
        LogUtils.initLogUtils(logSwitch, level, tag)
}