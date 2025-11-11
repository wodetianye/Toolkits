package com.java.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @Introduce: Android 时间工具类.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description :时间格式转换、时间比较、时间计算等
 */
object TimeUtils {

    // 常用时间格式
    const val FORMAT_YMD_HMS = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_YMD_HM = "yyyy-MM-dd HH:mm"
    const val FORMAT_YMD = "yyyy-MM-dd"
    const val FORMAT_HMS = "HH:mm:ss"
    const val FORMAT_HM = "HH:mm"
    const val FORMAT_CHINESE_YMD = "yyyy年MM月dd日"
    const val FORMAT_CHINESE_YMD_HM = "yyyy年MM月dd日 HH:mm"

    /**
     * 将时间戳转换为指定格式的时间字符串
     * @param timestamp 时间戳（毫秒）
     * @param pattern 时间格式，默认为 yyyy-MM-dd HH:mm:ss
     */
    fun timestampToString(timestamp: Long, pattern: String = FORMAT_YMD_HMS): String {
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 将时间字符串转换为时间戳
     * @param timeString 时间字符串
     * @param pattern 时间格式，默认为 yyyy-MM-dd HH:mm:ss
     */
    fun stringToTimestamp(timeString: String, pattern: String = FORMAT_YMD_HMS): Long {
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            val date = sdf.parse(timeString)
            date?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    /**
     * 将 Date 对象转换为指定格式的时间字符串
     */
    fun dateToString(date: Date, pattern: String = FORMAT_YMD_HMS): String {
        return timestampToString(date.time, pattern)
    }

    /**
     * 将时间字符串转换为 Date 对象
     */
    fun stringToDate(timeString: String, pattern: String = FORMAT_YMD_HMS): Date? {
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.parse(timeString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取当前时间的时间戳
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    /**
     * 获取当前时间的字符串
     */
    fun getCurrentTimeString(pattern: String = FORMAT_YMD_HMS): String {
        return timestampToString(getCurrentTimestamp(), pattern)
    }

    /**
     * 比较两个时间戳的大小
     * @return 1: time1 > time2, 0: time1 == time2, -1: time1 < time2
     */
    fun compareTimestamp(time1: Long, time2: Long): Int {
        return when {
            time1 > time2 -> 1
            time1 < time2 -> -1
            else -> 0
        }
    }

    /**
     * 比较两个时间字符串的大小
     */
    fun compareTimeString(time1: String, time2: String, pattern: String = FORMAT_YMD_HMS): Int {
        val timestamp1 = stringToTimestamp(time1, pattern)
        val timestamp2 = stringToTimestamp(time2, pattern)
        return compareTimestamp(timestamp1, timestamp2)
    }

    /**
     * 判断时间戳是否在指定范围内
     * @param timestamp 要判断的时间戳
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param includeBoundary 是否包含边界，默认包含
     */
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
     * 计算两个时间戳的时间差
     * @param unit 时间单位
     */
    fun getTimeDifference(
        startTime: Long,
        endTime: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS
    ): Long {
        val diff = endTime - startTime
        return when (unit) {
            TimeUnit.MILLISECONDS -> diff
            TimeUnit.SECONDS -> TimeUnit.MILLISECONDS.toSeconds(diff)
            TimeUnit.MINUTES -> TimeUnit.MILLISECONDS.toMinutes(diff)
            TimeUnit.HOURS -> TimeUnit.MILLISECONDS.toHours(diff)
            TimeUnit.DAYS -> TimeUnit.MILLISECONDS.toDays(diff)
            else -> diff
        }
    }

    /**
     * 将时间差转换为易读的字符串格式
     * 如：1天2小时30分钟
     */
    fun getReadableTimeDifference(startTime: Long, endTime: Long): String {
        val diff = endTime - startTime
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60

        return buildString {
            if (days > 0) append("${days}天")
            if (hours > 0) append("${hours}小时")
            if (minutes > 0) append("${minutes}分钟")
            if (seconds > 0 && days == 0L && hours == 0L) append("${seconds}秒")
            if (isEmpty()) append("0秒")
        }
    }

    /**
     * 获取相对时间描述（如：刚刚、x分钟前、x小时前等）
     */
    fun getRelativeTime(timestamp: Long): String {
        val currentTime = getCurrentTimestamp()
        val diff = currentTime - timestamp

        return when {
            diff < 0 -> "未来时间"
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${TimeUnit.MILLISECONDS.toHours(diff)}小时前"
            diff < 30 * 24 * 60 * 60 * 1000L -> "${TimeUnit.MILLISECONDS.toDays(diff)}天前"
            else -> timestampToString(timestamp, FORMAT_YMD)
        }
    }

    /**
     * 判断是否为同一天
     */
    fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 判断是否为今天
     */
    fun isToday(timestamp: Long): Boolean {
        return isSameDay(timestamp, getCurrentTimestamp())
    }

    /**
     * 判断是否为昨天
     */
    fun isYesterday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == yesterday.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == yesterday.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取指定时间戳的星期几
     */
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

    /**
     * 时间格式转换
     */
    fun convertFormat(timeString: String, fromPattern: String, toPattern: String): String {
        return try {
            val timestamp = stringToTimestamp(timeString, fromPattern)
            timestampToString(timestamp, toPattern)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}