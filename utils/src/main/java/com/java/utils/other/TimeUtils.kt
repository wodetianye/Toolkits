package com.java.utils.other

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * @Introduce: Android 时间工具类.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description: 时间格式转换、时间比较、时间计算等
 */
object TimeUtils {
    const val FORMAT_YMD_HMS = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_YMD_HM = "yyyy-MM-dd HH:mm"
    const val FORMAT_YMD = "yyyy-MM-dd"
    const val FORMAT_HMS = "HH:mm:ss"
    const val FORMAT_HM = "HH:mm"
    const val FORMAT_CHINESE_YMD = "yyyy年MM月dd日"
    const val FORMAT_CHINESE_YMD_HM = "yyyy年MM月dd日 HH:mm"

    /** SimpleDateFormat 线程缓存，避免每次创建。 */
    private val sdfCache = object : ThreadLocal<MutableMap<String, SimpleDateFormat>>() {
        override fun initialValue(): MutableMap<String, SimpleDateFormat> = HashMap()
    }

    // 公开方法 —— 转换 =====================================================================

    /**
     * 时间戳（毫秒）→ 指定格式字符串。
     *
     * 例：1699851000000L -> "2023-11-13 15:30:00"
     */
    @JvmStatic
    @JvmOverloads
    fun timestampToString(timestamp: Long, pattern: String = FORMAT_YMD_HMS): String {
        return try {
            getSdf(pattern).format(Date(timestamp))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 时间字符串 → 时间戳（毫秒），失败返回 0。
     *
     * 例："2023-11-13 15:30:00" -> 1699851000000L
     */
    @JvmStatic
    @JvmOverloads
    fun stringToTimestamp(timeString: String?, pattern: String = FORMAT_YMD_HMS): Long {
        if (timeString.isNullOrEmpty()) return 0L
        return try {
            getSdf(pattern).parse(timeString)?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    /**
     * Date → 指定格式字符串。
     *
     * 例：Date(1699851000000L) -> "2023-11-13 15:30:00"
     */
    @JvmStatic
    @JvmOverloads
    fun dateToString(date: Date?, pattern: String = FORMAT_YMD_HMS): String {
        if (date == null) return ""
        return timestampToString(date.time, pattern)
    }

    /**
     * 时间字符串 → Date，失败返回 null。
     *
     * 例："2023-11-13 15:30:00" -> Date(1699851000000L)
     */
    @JvmStatic
    @JvmOverloads
    fun stringToDate(timeString: String?, pattern: String = FORMAT_YMD_HMS): Date? {
        if (timeString.isNullOrEmpty()) return null
        return try {
            getSdf(pattern).parse(timeString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 时间格式转换：旧格式字符串 → 新格式字符串。
     *
     * 例："2023-11-13 15:30:00" -> "2023年11月13日 15:30"
     */
    @JvmStatic
    fun convertFormat(timeString: String, fromPattern: String, toPattern: String): String {
        val timestamp = stringToTimestamp(timeString, fromPattern)
        return timestampToString(timestamp, toPattern)
    }

    // 公开方法 —— 当前时间 =====================================================================

    /** 当前时间戳（毫秒）。 */
    @JvmStatic
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()

    /**
     * 当前时间的字符串。
     *
     * 例：() -> "2023-11-13 15:30:00"
     */
    @JvmStatic
    @JvmOverloads
    fun getCurrentTimeString(pattern: String = FORMAT_YMD_HMS): String {
        return timestampToString(getCurrentTimestamp(), pattern)
    }

    // 公开方法 —— 比较 =====================================================================

    /**
     * 比较两个时间戳：>0 前者大，0 相等，<0 前者小。
     *
     * 例：1699851000000L, 1699852000000L -> -1
     */
    @JvmStatic
    fun compareTimestamp(time1: Long, time2: Long): Int = time1.compareTo(time2)

    /**
     * 比较两个时间字符串。
     *
     * 例："2023-11-13 15:30:00", "2023-11-13 16:30:00" -> -1
     */
    @JvmStatic
    @JvmOverloads
    fun compareTimeString(
        time1: String,
        time2: String,
        pattern: String = FORMAT_YMD_HMS
    ): Int {
        return compareTimestamp(
            stringToTimestamp(time1, pattern),
            stringToTimestamp(time2, pattern)
        )
    }

    /**
     * 判断时间戳是否在指定范围内。
     *
     * 例：1699851500000L, 1699851000000L, 1699852000000L -> true
     */
    @JvmStatic
    @JvmOverloads
    fun isTimeInRange(
        timestamp: Long,
        startTime: Long,
        endTime: Long,
        includeBoundary: Boolean = true
    ): Boolean {
        return if (includeBoundary) {
            timestamp in startTime..endTime
        } else {
            timestamp > startTime && timestamp < endTime
        }
    }

    /**
     * 判断两个时间戳是否为同一天。
     *
     * 例：1699851000000L, 1699852000000L -> true
     */
    @JvmStatic
    fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /** 判断是否为今天。 */
    @JvmStatic
    fun isToday(timestamp: Long): Boolean = isSameDay(timestamp, getCurrentTimestamp())

    /** 判断是否为昨天。 */
    @JvmStatic
    fun isYesterday(timestamp: Long): Boolean {
        val yesterday = getCurrentTimestamp() - TimeUnit.DAYS.toMillis(1)
        return isSameDay(timestamp, yesterday)
    }

    // 公开方法 —— 时间差 =====================================================================

    /**
     * 计算两个时间戳的差值（endTime - startTime），结果可能为负。
     * 如果只关心差值大小，用 [getAbsTimeDifference]。
     *
     * 例：1699851000000L, 1699854600000L, TimeUnit.MINUTES -> 60
     */
    @JvmStatic
    @JvmOverloads
    fun getTimeDifference(
        startTime: Long,
        endTime: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS
    ): Long {
        return unit.convert(endTime - startTime, TimeUnit.MILLISECONDS)
    }

    /**
     * 计算两个时间戳差值的绝对值。
     *
     * 例：1699854600000L, 1699851000000L, TimeUnit.MINUTES -> 60
     */
    @JvmStatic
    @JvmOverloads
    fun getAbsTimeDifference(
        startTime: Long,
        endTime: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS
    ): Long {
        return unit.convert(abs(endTime - startTime), TimeUnit.MILLISECONDS)
    }

    /**
     * 时间差 → 易读字符串，如 "1天2小时30分钟"。
     * 内部取绝对值，不受参数顺序影响。
     *
     * 例：1699851000000L, 1699948200000L -> "1天3小时0分钟"
     */
    @JvmStatic
    fun getReadableTimeDifference(startTime: Long, endTime: Long): String {
        val diff = abs(endTime - startTime)
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60

        return buildString {
            if (days > 0) append("${days}天")
            if (hours > 0) append("${hours}小时")
            if (minutes > 0) append("${minutes}分钟")
            // 只有不足 1 分钟时才显示秒
            if (days == 0L && hours == 0L && minutes == 0L && seconds > 0) {
                append("${seconds}秒")
            }
            if (isEmpty()) append("0秒")
        }
    }

    /**
     * 相对时间描述：刚刚 / x分钟前 / x小时前 / x天前 / 具体日期。
     * 小于 1 分钟（含轻微未来偏移）都算"刚刚"。
     *
     * 例：1699851000000L -> "3分钟前"（取决于当前时间）
     */
    @JvmStatic
    fun getRelativeTime(timestamp: Long): String {
        val diff = getCurrentTimestamp() - timestamp
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "刚刚"
            diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}分钟前"
            diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}小时前"
            diff < TimeUnit.DAYS.toMillis(30) -> "${TimeUnit.MILLISECONDS.toDays(diff)}天前"
            else -> timestampToString(timestamp, FORMAT_YMD)
        }
    }

    // 公开方法 —— 星期 =====================================================================

    /**
     * 获取指定时间戳的星期几。
     *
     * 例：1699851000000L -> "星期一"
     */
    @JvmStatic
    fun getDayOfWeek(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "星期日"
            Calendar.MONDAY -> "星期一"
            Calendar.TUESDAY -> "星期二"
            Calendar.WEDNESDAY -> "星期三"
            Calendar.THURSDAY -> "星期四"
            Calendar.FRIDAY -> "星期五"
            Calendar.SATURDAY -> "星期六"
            else -> ""
        }
    }

    /** 获取（或创建）当前线程对应格式的 SimpleDateFormat。 */
    private fun getSdf(pattern: String): SimpleDateFormat {
        val map = sdfCache.get()!!
        return map.getOrPut(pattern) {
            SimpleDateFormat(pattern, Locale.getDefault())
        }
    }
}