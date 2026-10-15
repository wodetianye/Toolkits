package com.java.utils.log

import android.util.Log
import com.google.gson.GsonBuilder

/**
 * @Introduce: 日志管理输出类.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 支持多级别日志、堆栈定位、JSON 序列化、超长日志分段.
 */
internal object LogUtils {
    private const val LOG_MAX_LENGTH = 1024 * 2
    private const val LOG_TOTAL_LIMIT = 5 * LOG_MAX_LENGTH

    /** 默认 Tag。 */
    @Volatile
    private var defaultTag: String = "App"

    /** 日志级别，默认 DEBUG（release 建议改成 ERROR）。 */
    @Volatile
    private var logLevel: Int = Log.DEBUG

    private val gson = GsonBuilder().disableHtmlEscaping().create()

    /** 堆栈过滤白名单，printStackTrace 只输出包含该字符串的帧。 */
    @Volatile
    private var stackTraceFilter: String = ""

    // ====================== 配置API ======================

    /**
     * 设置默认 Tag。
     *
     * 例：setDefaultTag("MyApp")
     */
    fun setDefaultTag(tag: String?) {
        defaultTag = if (tag.isNullOrEmpty()) "App" else tag
    }

    /**
     * 设置日志级别。
     *
     * 例：setLogLevel(Log.WARN)
     */
    fun setLogLevel(level: Int) {
        logLevel = level
    }

    /**
     * 日志总开关。
     *
     * 例：switchLog(false) -> 关闭所有日志输出
     */
    fun switchLog(logSwitch: Boolean) {
        logLevel = if (logSwitch) Log.VERBOSE else Log.ASSERT
    }

    /**
     * 初始化日志工具。
     *
     * 例：initLogUtils(true, Log.DEBUG, "MyApp")
     */
    fun initLogUtils(logSwitch: Boolean, level: Int, tag: String? = null) {
        switchLog(logSwitch)
        setLogLevel(level)
        setDefaultTag(tag)
    }

    fun getCurrentTagName(): String {
        return defaultTag
    }

    /**
     * 设置堆栈过滤包名。
     *
     * 例：setStackTraceFilter("com.yourpackage")
     */
    fun setStackTraceFilter(packageName: String?) {
        stackTraceFilter = packageName.orEmpty()
    }

    /**
     * 当前日志级别是否允许 DEBUG。
     *
     * 例：isDebugLevel() -> true
     */
    fun isDebugLevel(): Boolean = logLevel <= Log.DEBUG

    // 日志输出 =================================================

    fun verbose(args: Any?) = verbose(defaultTag, args)

    fun verbose(tag: String?, vararg args: Any?) {
        if (Log.VERBOSE < logLevel) return
        assembleLog(Log.VERBOSE, tag, *args)
    }

    fun debug(vararg args: Any?) = debug(defaultTag, *args)

    fun debug(tag: String?, vararg args: Any?) {
        if (Log.DEBUG < logLevel) return
        assembleLog(Log.DEBUG, tag, *args)
    }

    fun info(vararg args: Any?) = info(defaultTag, *args)

    fun info(tag: String?, vararg args: Any?) {
        if (Log.INFO < logLevel) return
        assembleLog(Log.INFO, tag, *args)
    }

    fun warn(vararg args: Any?) = warn(defaultTag, *args)

    fun warn(tag: String?, vararg args: Any?) {
        if (Log.WARN < logLevel) return
        assembleLog(Log.WARN, tag, *args)
    }

    fun error(vararg args: Any?) = error(defaultTag, *args)

    fun error(tag: String?, vararg args: Any?) {
        if (Log.ERROR < logLevel) return
        assembleLog(Log.ERROR, tag, *args)
    }

    /**
     * 打印当前堆栈（只输出包含 [stackTraceFilter] 的帧）。
     *
     * 例：printStackTrace("TAG", true)
     */
    fun printStackTrace(tag: String?, info: Boolean) {
        val traceElements = Thread.currentThread().stackTrace ?: return
        val sb = StringBuilder()
        for (element in traceElements) {
            if (element == null) continue
            val elementStr = element.toString()
            if (stackTraceFilter.isEmpty() || elementStr.contains(stackTraceFilter)) {
                sb.append(elementStr).append("\n")
            }
        }
        if (info) info(tag, sb.toString()) else debug(tag, sb.toString())
    }

    // ====================== 核心日志组装、超长分段 ======================

    private fun assembleLog(level: Int, tag: String?, vararg args: Any?) {
        val realTag = if (tag.isNullOrEmpty()) defaultTag else tag
        recursionLog(level, realTag, convertJson(*args))
    }

    private fun recursionLog(level: Int, tag: String, json: String) {
        if (json.isEmpty()) return

        // 超过总量限制，截断并提示
        if (json.length > LOG_TOTAL_LIMIT) {
            printLog(level, tag, json.substring(0, LOG_TOTAL_LIMIT) + "... [truncated]")
            return
        }

        var remaining = json
        while (remaining.length > LOG_MAX_LENGTH) {
            val chunk = remaining.substring(0, LOG_MAX_LENGTH)
            printLog(level, tag, chunk)
            remaining = remaining.substring(LOG_MAX_LENGTH)
        }
        if (remaining.isNotEmpty()) {
            printLog(level, tag, remaining)
        }
    }

    private fun printLog(level: Int, tag: String, msg: String) {
        val prefix = getMethodName()
        val fullMsg = prefix + msg
        when (level) {
            Log.VERBOSE -> Log.v(tag, fullMsg)
            Log.DEBUG -> Log.d(tag, fullMsg)
            Log.INFO -> Log.i(tag, fullMsg)
            Log.WARN -> Log.w(tag, fullMsg)
            Log.ERROR -> Log.e(tag, fullMsg)
        }
    }

    private fun convertJson(vararg args: Any?): String {
        return try {
            if (args.isEmpty()) return "Log incorrect output format"
            val sb = StringBuilder()
            args.forEachIndexed { index, obj ->
                if (obj == null) {
                    sb.append("null")
                } else if (obj is String) {
                    sb.append(obj)
                } else {
                    sb.append(gson.toJson(obj))
                }
                if (index < args.size - 1) sb.append(",")
            }
            sb.toString()
        } catch (e: Exception) {
            e.toString()
        }
    }

    // 私有方法 —— 堆栈 =================================================

    /**
     * 定位调用者方法名。
     * 过滤：栈采集方法、VMStack、Logger、LogUtils，拿到业务代码栈帧
     * 例：-> "(MainActivity.kt:42) onCreate() -> "
     */
    private fun getMethodName(): String {
        val elements = Thread.currentThread().stackTrace ?: return ""
        val stackOffset = getStackOffset(elements)
        if (stackOffset < 0) return ""
        val element = elements[stackOffset]
        return "(${element.fileName}:${element.lineNumber}) ${element.methodName}() -> "
    }

    /**
     * 找到第一个不在 LogUtils / Logger 内部的栈帧。
     *
     * 例：-> 3
     */
    private fun getStackOffset(traceElements: Array<StackTraceElement>): Int {
        val logUtilsClsName = LogUtils::class.java.name
        val loggerClsName = Logger::class.java.name
        // 和老Java for(int i=3; i<traceElements.length; i++) 完全一致
        for (i in 3 until traceElements.size) {
            val element = traceElements[i] ?: continue
            val className = element.className
            // 找到不是日志工具类的栈帧，就是业务调用点
            if (className != logUtilsClsName && className != loggerClsName) {
                return i
            }
        }
        return -1
    }
}