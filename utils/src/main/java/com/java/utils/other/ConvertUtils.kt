package com.java.utils.other

import com.java.utils.log.Logger
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Hashtable
import java.util.Objects
import java.util.Stack
import kotlin.collections.iterator

/**
 * @Introduce: 对象比较及转换工具类.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 基本类型转换、集合安全式增删改查、参数校验等
 */
object ConvertUtils {

    // 类型转换 —— String =====================================================================

    /**
     * 任意对象 → String（默认 null）。
     *
     * 例：convertString(123) -> "123"
     */
    @JvmStatic
    fun convertString(obj: Any?): String? = convertString(obj, null)

    /**
     * 任意对象 → String（自定义默认值）。
     *
     * 例：convertString(null, "默认") -> "默认"
     */
    @JvmStatic
    fun convertString(obj: Any?, defaultValue: String?): String? {
        if (obj == null) return defaultValue
        if (obj is String) return obj
        return try {
            obj.toString()
        } catch (e: Exception) {
            Logger.e(e.toString())
            defaultValue
        }
    }

    // 类型转换 —— String → 数值 =====================================================================

    /**
     * String → Int（失败返回 0）。
     *
     * 例：str2Int("123") -> 123
     */
    @JvmStatic
    fun str2Int(src: String?): Int = str2Int(src, 0)

    /**
     * String → Int（失败返回 defaultValue）。
     *
     * 例：str2Int("abc", -1) -> -1
     */
    @JvmStatic
    fun str2Int(src: String?, defaultValue: Int): Int = str2Int(src, 10, defaultValue)

    /**
     * String → Int（指定进制，失败返回 defaultValue）。
     *
     * 例：str2Int("FF", 16, 0) -> 255
     */
    @JvmStatic
    fun str2Int(src: String?, radix: Int, defaultValue: Int): Int {
        if (isEmpty(src)) return defaultValue
        return src!!.toIntOrNull(radix) ?: defaultValue
    }

    /**
     * String → Long（失败返回 0）。
     *
     * 例：str2Long("123456789") -> 123456789L
     */
    @JvmStatic
    fun str2Long(src: String?): Long = str2Long(src, 0L)

    /**
     * String → Long（失败返回 defaultValue）。
     */
    @JvmStatic
    fun str2Long(src: String?, defaultValue: Long): Long = str2Long(src, 10, defaultValue)

    /**
     * String → Long（指定进制，失败返回 defaultValue）。
     */
    @JvmStatic
    fun str2Long(src: String?, radix: Int, defaultValue: Long): Long {
        if (isEmpty(src)) return defaultValue
        return src!!.toLongOrNull(radix) ?: defaultValue
    }

    /**
     * String → Float（失败返回 0）。
     *
     * 例：str2Float("1.5") -> 1.5f
     */
    @JvmStatic
    fun str2Float(src: String?): Float = str2Float(src, 0f)

    /**
     * String → Float（失败返回 defaultValue）。
     */
    @JvmStatic
    fun str2Float(src: String?, defaultValue: Float): Float {
        if (isEmpty(src)) return defaultValue
        return src!!.toFloatOrNull() ?: defaultValue
    }

    /**
     * String → Double（失败返回 0）。
     *
     * 例：str2Double("1.5") -> 1.5
     */
    @JvmStatic
    fun str2Double(src: String?): Double = str2Double(src, 0.0)

    /**
     * String → Double（失败返回 defaultValue）。
     */
    @JvmStatic
    fun str2Double(src: String?, defaultValue: Double): Double {
        if (isEmpty(src)) return defaultValue
        return src!!.toDoubleOrNull() ?: defaultValue
    }

    // 类型转换 —— 数值互转 =====================================================================

    /** Integer → Long（失败返回 0）。 */
    @JvmStatic
    fun int2ln(arg: Int?): Long = int2ln(arg, 0L)

    /** Integer → Long（失败返回 defaultValue）。 */
    @JvmStatic
    fun int2ln(arg: Int?, defaultValue: Long): Long = arg?.toLong() ?: defaultValue

    /** Float → Long（失败返回 0）。 */
    @JvmStatic
    fun float2ln(arg: Float?): Long = float2ln(arg, 0L)

    /** Float → Long（失败返回 defaultValue）。 */
    @JvmStatic
    fun float2ln(arg: Float?, defaultValue: Long): Long {
        if (arg == null || arg.isNaN() || arg.isInfinite()) return defaultValue
        return BigDecimal.valueOf(arg.toDouble()).toLong()
    }

    /** Double → Long（失败返回 0）。 */
    @JvmStatic
    fun double2ln(arg: Double?): Long = double2ln(arg, 0L)

    /** Double → Long（失败返回 defaultValue）。 */
    @JvmStatic
    fun double2ln(arg: Double?, defaultValue: Long): Long {
        if (arg == null || arg.isNaN() || arg.isInfinite()) return defaultValue
        return BigDecimal.valueOf(arg).toLong()
    }

    /** Long → Int（失败返回 0）。 */
    @JvmStatic
    fun ln2int(arg: Long?): Int = ln2int(arg, 0)

    /** Long → Int（失败返回 defaultValue）。 */
    @JvmStatic
    fun ln2int(arg: Long?, defaultValue: Int): Int = arg?.toInt() ?: defaultValue

    /** Float → Int（四舍五入，失败返回 0）。 */
    @JvmStatic
    fun float2int(args: Float?): Int = float2int(args, 0)

    /** Float → Int（四舍五入，失败返回 defaultValue）。 */
    @JvmStatic
    fun float2int(args: Float?, defaultValue: Int): Int {
        if (args == null || args.isNaN() || args.isInfinite()) return defaultValue
        return Math.round(args)
    }

    /** Double → Int（四舍五入，失败返回 0）。 */
    @JvmStatic
    fun double2int(args: Double?): Int = double2int(args, 0)

    /** Double → Int（四舍五入，失败返回 defaultValue）。 */
    @JvmStatic
    fun double2int(args: Double?, defaultValue: Int): Int {
        if (args == null || args.isNaN() || args.isInfinite()) return defaultValue
        return Math.round(args).toInt()
    }

    // 类型转换 —— 格式化 =====================================================================

    /**
     * 字符串数字保留 2 位小数（失败返回 "0.00"）。
     *
     * 例：stringFormatTwo("1.5") -> "1.50"
     */
    @JvmStatic
    fun stringFormatTwo(args: String?): String = stringFormatTwo(args, "0.00")

    /**
     * 字符串数字保留 2 位小数（失败返回 defaultValue）。
     *
     * 例：stringFormatTwo("abc", "-") -> "-"
     */
    @JvmStatic
    fun stringFormatTwo(value: String?, defaultValue: String): String {
        if (isEmpty(value)) return defaultValue
        return try {
            DecimalFormat("0.00").format(value!!.toDouble())
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    // 比较 —— 相等判断 =====================================================================

    /** String 相等（区分大小写，null 安全）。 */
    @JvmStatic
    fun equals(args: String?, args1: String?): Boolean {
        if (args == null) return args1 == null
        return args == args1
    }

    /** Object 相等（null 安全）。 */
    @JvmStatic
    fun equals(args: Any?, args1: Any?): Boolean = Objects.equals(args, args1)

    /** Long 相等（null 安全）。 */
    @JvmStatic
    fun equals(args: Long?, args1: Long?): Boolean {
        if (args == null) return args1 == null
        return args.compareTo(args1!!) == 0
    }

    /** Int 相等（null 安全）。 */
    @JvmStatic
    fun equals(args: Int?, args1: Int?): Boolean {
        if (args == null) return args1 == null
        return args.compareTo(args1!!) == 0
    }

    /** Double 相等（null 安全）。 */
    @JvmStatic
    fun equals(args: Double?, args1: Double?): Boolean {
        if (args == null) return args1 == null
        return args.compareTo(args1!!) == 0
    }

    /** Float 相等（null 安全）。 */
    @JvmStatic
    fun equals(args: Float?, args1: Float?): Boolean {
        if (args == null) return args1 == null
        return args.compareTo(args1!!) == 0
    }

    /**
     * String 相等（忽略大小写，null 安全）。
     *
     * 例：equalsIgnoreCase("ABC", "abc") -> true
     */
    @JvmStatic
    fun equalsIgnoreCase(args: String?, args1: String?): Boolean {
        if (args == null) return args1 == null
        return args.equals(args1, ignoreCase = true)
    }

    // 判空 —— 统一入口 =====================================================================

    /**
     * 是否为 null（只判 null，不判空串/空集合）。
     *
     * 例：isNull(null) -> true
     */
    @JvmStatic
    fun isNull(obj: Any?): Boolean = obj == null

    /**
     * 是否为空（null / 空串 / 空集合 / 空 Map / 空数组）。
     *
     * 例：isEmpty("") -> true
     * 例：isEmpty(emptyList<Int>()) -> true
     */
    @JvmStatic
    fun isEmpty(obj: Any?): Boolean {
        return when (obj) {
            null -> true
            is String -> obj.isEmpty()
            is Collection<*> -> obj.isEmpty()
            is Map<*, *> -> obj.isEmpty()
            is Array<*> -> obj.isEmpty()
            else -> false
        }
    }

    /** 是否为空（ByteArray 单独提供）。 */
    @JvmStatic
    fun isEmpty(objects: ByteArray?): Boolean = objects == null || objects.isEmpty()

    /**
     * null 时抛 NPE。
     *
     * 例：requireNonNull(obj) -> 非 null 返回 obj，null 抛 NPE
     */
    @JvmStatic
    fun <T : Any> requireNonNull(obj: T?): T =
        obj ?: throw NullPointerException("T obj is null")

    /**
     * null 时抛 NPE（自定义信息）。
     */
    @JvmStatic
    fun <T : Any> requireNonNull(obj: T?, throwContent: String): T =
        obj ?: throw NullPointerException(throwContent)

    // 判空 —— 集合专用 =====================================================================

    /** List 是否为 null。 */
    @JvmStatic
    fun isNull(objects: List<*>?): Boolean = objects == null

    /** List 是否为空。 */
    @JvmStatic
    fun isEmpty(objects: List<*>?): Boolean = objects == null || objects.isEmpty()

    /** Map 是否为 null。 */
    @JvmStatic
    fun <T, R> isNull(map: Map<T, R>?): Boolean = map == null

    /** Map 是否为空。 */
    @JvmStatic
    fun <T, R> isEmpty(map: Map<T, R>?): Boolean = map == null || map.isEmpty()

    /** 数组是否为空。 */
    @JvmStatic
    fun <T> isEmpty(objects: Array<T>?): Boolean = objects == null || objects.isEmpty()

    // 集合 —— 包含判断 =====================================================================

    /** List 是否包含元素。 */
    @JvmStatic
    fun <T> isContain(list: List<T>?, t: T): Boolean = !isEmpty(list) && list!!.contains(t)

    /** Stack 是否包含元素。 */
    @JvmStatic
    fun <T> isContain(stack: Stack<T>?, t: T): Boolean = !isEmpty(stack) && stack!!.contains(t)

    /** Hashtable 是否包含 Key。 */
    @JvmStatic
    fun <K, V> isContain(objects: Hashtable<K, V>?, key: K): Boolean =
        !isEmpty(objects) && objects!!.containsKey(key)

    /** Map 是否包含 Key。 */
    @JvmStatic
    fun <K, V> isContain(objects: Map<K, V>?, key: K): Boolean =
        !isEmpty(objects) && objects!!.containsKey(key)

    /** Map 包含 Key 时返回对应 Value，否则 null。 */
    @JvmStatic
    fun <K, V> containToValue(map: Map<K, V>?, key: K): V? {
        if (isEmpty(map) || !map!!.containsKey(key)) return null
        return map[key]
    }

    /** Hashtable 包含 Key 时返回对应 Value，否则 null。 */
    @JvmStatic
    fun <K, V> containToValue(hashtable: Hashtable<K, V>?, key: K): V? {
        if (isEmpty(hashtable) || !hashtable!!.containsKey(key)) return null
        return hashtable[key]
    }

    /** Map 包含 Value 时返回对应 Key，否则 null。 */
    @JvmStatic
    fun <K, V> containToKey(map: Map<K, V>?, value: V): K? {
        if (isEmpty(map)) return null
        for ((k, v) in map!!) {
            if (Objects.equals(v, value)) return k
        }
        return null
    }

    // 集合 —— 增 =====================================================================

    /**
     * 添加元素（List，去重并移到末尾）。
     *
     * 例：push(listOf(1, 2), 2, 3) -> [1, 2, 3]
     */
    @JvmStatic
    fun <T> push(list: List<T>?, vararg args: T): List<T> {
        val result = if (list == null) mutableListOf() else ArrayList(list)
        for (t in args) {
            if (t == null) continue
            result.remove(t)
            result.add(t)
        }
        return result
    }

    /**
     * 添加元素（Stack）。
     *
     * 例：push(Stack(), 1, 2) -> 顶部为 2
     */
    @JvmStatic
    fun <T> push(stack: Stack<T>?, vararg args: T): Stack<T> {
        val result = stack ?: Stack()
        for (t in args) {
            if (t != null) result.push(t)
        }
        return result
    }

    /** 添加元素（Map）。 */
    @JvmStatic
    fun <K, V> push(map: MutableMap<K, V>?, k: K, v: V): MutableMap<K, V> {
        val result = map ?: HashMap()
        result[k] = v
        return result
    }

    /** 添加元素（Hashtable）。 */
    @JvmStatic
    fun <K, V> push(hashtable: Hashtable<K, V>?, k: K, v: V): Hashtable<K, V> {
        val result = hashtable ?: Hashtable()
        result[k] = v
        return result
    }

    // 集合 —— 删 =====================================================================

    /** 移除指定元素（List）。 */
    @JvmStatic
    fun <T> remove(list: MutableList<T>?, t: T): MutableList<T>? {
        if (!isEmpty(list)) list!!.remove(t)
        return list
    }

    /** 移除指定元素（Stack）。 */
    @JvmStatic
    fun <T> remove(stack: Stack<T>?, t: T): Stack<T>? {
        if (!isEmpty(stack)) stack!!.remove(t)
        return stack
    }

    /** 移除指定下标（List）。 */
    @JvmStatic
    fun remove(list: MutableList<*>?, index: Int): MutableList<*>? {
        if (!isEmpty(list) && index >= 0 && index < list!!.size) list.removeAt(index)
        return list
    }

    /** 移除指定下标（Stack）。 */
    @JvmStatic
    fun remove(stack: Stack<*>?, index: Int): Stack<*>? {
        if (!isEmpty(stack) && index >= 0 && index < stack!!.size) stack.removeAt(index)
        return stack
    }

    /** 移除指定 Key（Map）。 */
    @JvmStatic
    fun <K, V> remove(map: MutableMap<K, V>?, k: K): MutableMap<K, V>? {
        if (!isEmpty(map)) map!!.remove(k)
        return map
    }

    /** 移除指定 Key（Hashtable）。 */
    @JvmStatic
    fun <K, V> remove(hashtable: Hashtable<K, V>?, k: K): Hashtable<K, V>? {
        if (!isEmpty(hashtable)) hashtable!!.remove(k)
        return hashtable
    }

    // 集合 —— 查 =====================================================================

    /** 获取 List 最后一个元素（不移除）。 */
    @JvmStatic
    fun <T> peek(list: List<T>?): T? {
        if (isEmpty(list)) return null
        return list!![list.size - 1]
    }

    /** 获取 Stack 顶部元素（不移除）。 */
    @JvmStatic
    fun <T> peek(stack: Stack<T>?): T? {
        if (isEmpty(stack)) return null
        return stack!!.peek()
    }

    /**
     * 弹出 Stack 顶部元素。
     *
     * 例：pop(stack) -> 返回栈顶元素并移除
     */
    @JvmStatic
    fun <T> pop(stack: Stack<T>?): T? {
        if (isEmpty(stack)) return null
        return stack!!.pop()
    }

    /**
     * 弹出 List 最后一个元素。
     *
     * 例：pop(mutableListOf(1, 2, 3)) -> 返回 3，list 变成 [1, 2]
     */
    @JvmStatic
    fun <T> pop(list: MutableList<T>?): T? {
        if (isEmpty(list)) return null
        return list!!.removeAt(list.size - 1)
    }

    /** 弹出 Hashtable 指定 Key 的 Value。 */
    @JvmStatic
    fun <K, V> pop(hashtable: Hashtable<K, V>?, k: K): V? {
        if (isEmpty(hashtable)) return null
        return hashtable!!.remove(k)
    }

    /** 获取元素在 List 中最后出现的位置，不存在返回 -1。 */
    @JvmStatic
    fun <T> lastIndexOf(list: List<T>?, t: T): Int {
        if (isEmpty(list)) return -1
        return list!!.lastIndexOf(t)
    }

    /** 获取元素在 Stack 中最后出现的位置，不存在返回 -1。 */
    @JvmStatic
    fun <T> lastIndexOf(stack: Stack<T>?, t: T): Int {
        if (isEmpty(stack)) return -1
        return stack!!.lastIndexOf(t)
    }

    /** 获取 List 指定下标元素，越界返回 null。 */
    @JvmStatic
    fun <T> get(list: List<T>?, index: Int): T? {
        if (isEmpty(list) || index < 0 || index >= list!!.size) return null
        return list[index]
    }

    /** 获取 Stack 指定下标元素，越界返回 null。 */
    @JvmStatic
    fun <T> get(stack: Stack<T>?, index: Int): T? {
        if (isEmpty(stack) || index < 0 || index >= stack!!.size) return null
        return stack[index]
    }

    // 集合 —— 清空 =====================================================================

    /** 清空 List。 */
    @JvmStatic
    fun <T> clear(list: MutableList<T>?) {
        if (!isEmpty(list)) list!!.clear()
    }

    /** 清空 Stack。 */
    @JvmStatic
    fun <T> clear(stack: Stack<T>?) {
        if (!isEmpty(stack)) stack!!.clear()
    }

    /** 清空 Map。 */
    @JvmStatic
    fun <K, V> clear(map: MutableMap<K, V>?) {
        if (!isEmpty(map)) map!!.clear()
    }

    /** 清空 Hashtable。 */
    @JvmStatic
    fun <K, V> clear(hashtable: Hashtable<K, V>?) {
        if (!isEmpty(hashtable)) hashtable!!.clear()
    }

    // 比较 —— 数值大小

    /**
     * 比较 Long：arg > args 返回 1，相等返回 0，小于返回 -1。
     *
     * 例：compare(3L, 2L) -> 1
     */
    @JvmStatic
    fun compare(arg: Long?, args: Long?): Int {
        require(arg != null && args != null) { "Argument is null" }
        return arg.compareTo(args)
    }

    /** Long 比较：arg > args 返回 true。 */
    @JvmStatic
    fun compareGreater(arg: Long?, args: Long?): Boolean = compare(arg, args) > 0

    /** 比较 Int。 */
    @JvmStatic
    fun compare(arg: Int?, args: Int?): Int {
        require(arg != null && args != null) { "Argument is null" }
        return arg.compareTo(args)
    }

    /** Int 比较：arg > args 返回 true。 */
    @JvmStatic
    fun compareGreater(arg: Int?, args: Int?): Boolean = compare(arg, args) > 0

    /** 比较 Double。 */
    @JvmStatic
    fun compare(arg: Double?, args: Double?): Int {
        require(arg != null && args != null) { "Argument is null" }
        return arg.compareTo(args)
    }

    /** Double 比较：arg > args 返回 true。 */
    @JvmStatic
    fun compareGreater(arg: Double?, args: Double?): Boolean = compare(arg, args) > 0

    /** 比较 Float。 */
    @JvmStatic
    fun compare(arg: Float?, args: Float?): Int {
        require(arg != null && args != null) { "Argument is null" }
        return arg.compareTo(args)
    }

    /** Float 比较：arg > args 返回 true。 */
    @JvmStatic
    fun compareGreater(arg: Float?, args: Float?): Boolean = compare(arg, args) > 0

    /** 比较对象（自定义 Comparator）。 */
    @JvmStatic
    fun <T> compare(obj1: T?, obj2: T?, c: Comparator<in T>): Int =
        Objects.compare(obj1, obj2, c)

    // 计算 —— 数值差 =====================================================================

    /** 计算 Long 差值绝对值。 */
    @JvmStatic
    fun numberDiff(arg: Long?, args: Long?): Long {
        require(arg != null && args != null) { "Argument is null" }
        return Math.abs(arg - args)
    }

    /** 计算 Int 差值绝对值。 */
    @JvmStatic
    fun numberDiff(arg: Int?, args: Int?): Int {
        require(arg != null && args != null) { "Argument is null" }
        return Math.abs(arg - args)
    }

    /** 计算 Double 差值绝对值。 */
    @JvmStatic
    fun numberDiff(arg: Double?, args: Double?): Double {
        require(arg != null && args != null) { "Argument is null" }
        return Math.abs(arg - args)
    }

    /** 计算 Float 差值绝对值。 */
    @JvmStatic
    fun numberDiff(arg: Float?, args: Float?): Float {
        require(arg != null && args != null) { "Argument is null" }
        return Math.abs(arg - args)
    }

    // 单位转换 —— 坐标 =====================================================================

    /**
     * 城市经纬度整数 → 真实经纬度（除以 1e6）。
     *
     * 例：transCityLatAndLon(39123456.0) -> 39.123456
     */
    @JvmStatic
    fun transCityLatAndLon(input: Double): Double = input / 1000000.0

    /**
     * 投影坐标 → 经纬度（除以 3.6e6）。
     *
     * 例：transProjectionLatAndLon(141000000.0) -> 39.1666...
     */
    @JvmStatic
    fun transProjectionLatAndLon(input: Double): Double = input / 3600000.0

    // 单位转换 —— 长度 =====================================================================

    /**
     * 米 → 公里。
     *
     * 例：convertMetersToKilometers(1500) -> 1.5f
     */
    @JvmStatic
    fun convertMetersToKilometers(meters: Long): Float = meters / 1000.0f

    /**
     * 米 → 千米（公里）。
     *
     * 例：meters2Kilometers(1500) -> 1.5
     */
    @JvmStatic
    fun meters2Kilometers(meters: Double): Double = meters / 1000.0

    /**
     * 千米（公里）→ 米。
     *
     * 例：kilometers2Meters(1.5) -> 1500.0
     */
    @JvmStatic
    fun kilometers2Meters(kilometers: Double): Double = kilometers * 1000.0

    /**
     * 厘米 → 米。
     *
     * 例：centimeters2Meters(150.0) -> 1.5
     */
    @JvmStatic
    fun centimeters2Meters(centimeters: Double): Double = centimeters / 100.0

    /**
     * 米 → 厘米。
     *
     * 例：meters2Centimeters(1.5) -> 150.0
     */
    @JvmStatic
    fun meters2Centimeters(meters: Double): Double = meters * 100.0

    /**
     * 毫米 → 厘米。
     *
     * 例：millimeters2Centimeters(15.0) -> 1.5
     */
    @JvmStatic
    fun millimeters2Centimeters(millimeters: Double): Double = millimeters / 10.0

    /**
     * 厘米 → 毫米。
     *
     * 例：centimeters2Millimeters(1.5) -> 15.0
     */
    @JvmStatic
    fun centimeters2Millimeters(centimeters: Double): Double = centimeters * 10.0

    // 单位转换 —— 重量 =====================================================================

    /**
     * 克 → 千克（公斤）。
     *
     * 例：grams2Kilograms(1500.0) -> 1.5
     */
    @JvmStatic
    fun grams2Kilograms(grams: Double): Double = grams / 1000.0

    /**
     * 千克（公斤）→ 克。
     *
     * 例：kilograms2Grams(1.5) -> 1500.0
     */
    @JvmStatic
    fun kilograms2Grams(kilograms: Double): Double = kilograms * 1000.0

    /**
     * 千克（公斤）→ 吨。
     *
     * 例：kilograms2Tons(1500.0) -> 1.5
     */
    @JvmStatic
    fun kilograms2Tons(kilograms: Double): Double = kilograms / 1000.0

    /**
     * 吨 → 千克（公斤）。
     *
     * 例：tons2Kilograms(1.5) -> 1500.0
     */
    @JvmStatic
    fun tons2Kilograms(tons: Double): Double = tons * 1000.0

    /**
     * 克 → 斤（1 斤 = 500 克）。
     *
     * 例：grams2Jin(1000.0) -> 2.0
     */
    @JvmStatic
    fun grams2Jin(grams: Double): Double = grams / 500.0

    /**
     * 斤 → 克。
     *
     * 例：jin2Grams(2.0) -> 1000.0
     */
    @JvmStatic
    fun jin2Grams(jin: Double): Double = jin * 500.0

    /**
     * 斤 → 两（1 斤 = 10 两）。
     *
     * 例：jin2Liang(2.0) -> 20.0
     */
    @JvmStatic
    fun jin2Liang(jin: Double): Double = jin * 10.0

    /**
     * 两 → 斤。
     *
     * 例：liang2Jin(20.0) -> 2.0
     */
    @JvmStatic
    fun liang2Jin(liang: Double): Double = liang / 10.0

    /**
     * 克 → 两（1 两 = 50 克）。
     *
     * 例：grams2Liang(100.0) -> 2.0
     */
    @JvmStatic
    fun grams2Liang(grams: Double): Double = grams / 50.0

    /**
     * 两 → 克。
     *
     * 例：liang2Grams(2.0) -> 100.0
     */
    @JvmStatic
    fun liang2Grams(liang: Double): Double = liang * 50.0

    // 单位转换 —— 金额（元 / 角 / 分） =====================================================================

    /**
     * 分 → 元。
     *
     * 例：fen2Yuan(150) -> 1.5
     */
    @JvmStatic
    fun fen2Yuan(fen: Long): Double = fen / 100.0

    /**
     * 元 → 分。
     *
     * 例：yuan2Fen(1.5) -> 150
     */
    @JvmStatic
    fun yuan2Fen(yuan: Double): Long = Math.round(yuan * 100)

    /**
     * 角 → 元。
     *
     * 例：jiao2Yuan(15) -> 1.5
     */
    @JvmStatic
    fun jiao2Yuan(jiao: Long): Double = jiao / 10.0

    /**
     * 元 → 角。
     *
     * 例：yuan2Jiao(1.5) -> 15
     */
    @JvmStatic
    fun yuan2Jiao(yuan: Double): Long = Math.round(yuan * 10)

    /**
     * 分 → 角。
     *
     * 例：fen2Jiao(15) -> 1.5
     */
    @JvmStatic
    fun fen2Jiao(fen: Long): Double = fen / 10.0

    /**
     * 角 → 分。
     *
     * 例：jiao2Fen(1.5) -> 15
     */
    @JvmStatic
    fun jiao2Fen(jiao: Double): Long = Math.round(jiao * 10)

    /**
     * 分 → 元（格式化，保留 2 位小数）。
     *
     * 例：fen2YuanString(150) -> "1.50"
     */
    @JvmStatic
    fun fen2YuanString(fen: Long): String = stringFormatTwo(fen2Yuan(fen).toString())

    // 校验 —— 字符串 =====================================================================

    /**
     * 判断字符串是否为整数（支持 +/- 前缀）。
     *
     * 例：isInteger("123") -> true
     * 例：isInteger("-12") -> true
     * 例：isInteger("12.3") -> false
     */
    @JvmStatic
    fun isInteger(str: String?): Boolean {
        if (isEmpty(str)) return false
        val s = str!!
        val start = if (s[0] == '+' || s[0] == '-') 1 else 0
        if (start == s.length) return false
        for (i in start until s.length) {
            if (!Character.isDigit(s[i])) return false
        }
        return true
    }

    /**
     * 校验参数是否为空（为空抛 RuntimeException）。
     *
     * 例：checkParam("save", obj) -> obj 为空时抛异常
     */
    @JvmStatic
    fun checkParam(method: String, vararg args: Any?) {
        if (args.isEmpty()) {
            throw RuntimeException("$method 参数异常可能为空，无法继续执行")
        }
        for (i in args.indices) {
            if (isEmpty(args[i])) {
                throw RuntimeException("$method 第${i + 1}参数异常，无法继续执行")
            }
        }
    }
}