package com.java.utils;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import androidx.annotation.Nullable;

import com.java.utils.log.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * @Introduce: 对象比较及转换工具类.
 * @Author: Lww
 * @Date: 2023/10/11
 * @Description: 基本类型转换、集合安全式增删改查、参数校验等
 */
public class ConvertUtils {

    public static String convertString(Object object) {
        return convertString(object, null);
    }

    public static Integer str2Int(@Nullable String src) {
        return str2Int(src, 0);
    }

    public static Integer str2Int(@Nullable String src, int defaultValue) {
        return str2Int(src, 10, defaultValue);
    }

    public static Long str2Long(@Nullable String src) {
        return str2Long(src, 0);
    }

    public static Long str2Long(@Nullable String src, long defaultValue) {
        return str2Long(src, 10, defaultValue);
    }

    public static Float str2Float(@Nullable String src) {
        return str2Float(src, 0);
    }

    public static Double str2Double(@Nullable String src) {
        return str2Double(src, 0);
    }

    public static Long int2ln(Integer arg) {
        return int2ln(arg, 0);
    }

    public static Long float2ln(Float arg) {
        return float2ln(arg, 0);
    }

    public static Long double2ln(Double arg) {
        return double2ln(arg, 0);
    }

    public static Integer ln2int(Long arg) {
        return ln2int(arg, 0);
    }

    public static Integer float2int(Float args) {
        return float2int(args, 0);
    }

    public static Integer double2int(Double args) {
        return double2int(args, 0);
    }

    public static String stringFormatTwo(String args) {
        return stringFormatTwo(args, "0.00");
    }

    /**
     * 转换字符串.
     *
     * @param object 任意类型
     * @return str value
     */
    public static String convertString(@Nullable Object object, String defaultValue) {
        try {
            assert !isNull(object);
            if (object instanceof String) return object.toString();
            return String.valueOf(object);
        } catch (AssertionError | ClassCastException exception) {
            Logger.e(exception.toString());
            return defaultValue;
        }
    }

    /**
     * 字符串转换整型.
     *
     * @param src          需要转换的字符
     * @param radix        进制类型
     * @param defaultValue 转换失败的默认值
     * @return int value
     */
    public static Integer str2Int(@Nullable String src, int radix, int defaultValue) {
        try {
            assert !isEmpty(src);
            return Integer.valueOf(src, radix);
        } catch (AssertionError | NumberFormatException throwable) {
            if (throwable instanceof AssertionError)
                Logger.e(throwable.toString());
            return defaultValue;
        }
    }

    /**
     * 字符串转换长整型.
     *
     * @param src          需要转换的字符
     * @param radix        进制类型
     * @param defaultValue 转换失败的默认值
     * @return long value
     */
    public static Long str2Long(@Nullable String src, int radix, long defaultValue) {
        try {
            assert !isEmpty(src);
            return Long.valueOf(src, radix);
        } catch (AssertionError | NumberFormatException throwable) {
            Logger.e(throwable.toString());
            return defaultValue;
        }
    }

    /**
     * 字符串转换浮点类型.
     *
     * @param src          需要转换的字符
     * @param defaultValue 转换失败的默认值
     * @return float value
     */
    public static Float str2Float(@Nullable String src, float defaultValue) {
        try {
            assert !isEmpty(src);
            return Float.valueOf(src);
        } catch (AssertionError | NumberFormatException throwable) {
            Logger.e(throwable.toString());
            return defaultValue;
        }
    }

    /**
     * 字符串转换双精度类型.
     *
     * @param src          需要转换的字符
     * @param defaultValue 转换失败的默认值
     * @return double value
     */
    public static Double str2Double(@Nullable String src, double defaultValue) {
        try {
            assert !isEmpty(src);
            return Double.parseDouble(src);
        } catch (AssertionError | NumberFormatException throwable) {
            Logger.e(throwable.toString());
            return defaultValue;
        }
    }

    /**
     * Integer To Long.
     *
     * @param arg          要转换的值
     * @param defaultValue default value
     * @return Long value
     */
    public static Long int2ln(@Nullable Integer arg, long defaultValue) {
        try {
            assert !isNull(arg);
            BigDecimal bd = new BigDecimal(arg);
            return bd.longValue();
        } catch (AssertionError error) {
            Logger.e(error.toString());
            return defaultValue;
        }
    }

    /**
     * Float to Long.
     *
     * @param arg          float type
     * @param defaultValue default value
     * @return Long type
     */
    public static Long float2ln(@Nullable Float arg, long defaultValue) {
        try {
            assert !isNull(arg);
            BigDecimal bd = new BigDecimal(arg);
            return bd.longValue();
        } catch (AssertionError | NumberFormatException error) {
            Logger.e(error.toString());
            return defaultValue;
        }
    }

    /**
     * Double to Long.
     *
     * @param arg          double type
     * @param defaultValue default value
     * @return Long type
     */
    public static Long double2ln(@Nullable Double arg, long defaultValue) {
        try {
            assert !isNull(arg);
            BigDecimal bd = new BigDecimal(arg);
            return bd.longValue();
        } catch (AssertionError | NumberFormatException error) {
            Logger.e(error.toString());
            return defaultValue;
        }
    }


    /**
     * Long To Integer.
     *
     * @param arg          要转换的值
     * @param defaultValue default value
     * @return Integer value
     */
    public static Integer ln2int(@Nullable Long arg, int defaultValue) {
        try {
            assert !isNull(arg);
            BigDecimal bd = new BigDecimal(arg);
            return bd.intValue();
        } catch (AssertionError error) {
            Logger.e(error.toString());
            return defaultValue;
        }
    }

    /**
     * 浮点转换int
     *
     * @param args         float
     * @param defaultValue default value
     * @return Integer value
     */
    public static Integer float2int(Float args, int defaultValue) {
        try {
            assert !isNull(args);
            return Math.round(args);
        } catch (AssertionError error) {
            Logger.e(error.toString());
            return defaultValue;
        }
    }

    /**
     * 双精度转换int
     *
     * @param args         double
     * @param defaultValue default value
     * @return Integer value
     */
    public static Integer double2int(Double args, int defaultValue) {
        try {
            assert !isNull(args);
            return ln2int(Math.round(args), defaultValue);
        } catch (AssertionError error) {
            Logger.e(error.toString());
            return defaultValue;
        }
    }

    /**
     * 字符串比较（包括大小写）.
     *
     * @param args  this args
     * @param args1 this args1
     * @return true/false
     */
    public static boolean equals(String args, String args1) {
        if (isEmpty(args)) return isEmpty(args1);
        return !isEmpty(args1) && args.equals(args1);
    }

    /**
     * 对象比较.
     *
     * @param args  this args
     * @param args1 this args1
     * @return true/false
     */
    public static boolean equals(Object args, Object args1) {
        if (isNull(args)) return isNull(args1);
        return !isNull(args1) && Objects.equals(args, args1);
    }

    /**
     * Long类型比较
     *
     * @param args
     * @param args1
     * @return true/false
     */
    public static boolean equals(Long args, Long args1) {
        if (isNull(args)) return isNull(args1);
        return 0 == args.compareTo(args1);
    }

    /**
     * Integer类型比较
     *
     * @param args
     * @param args1
     * @return true/false
     */
    public static boolean equals(Integer args, Integer args1) {
        if (isNull(args)) return isNull(args1);
        return 0 == args.compareTo(args1);
    }

    /**
     * Double类型比较
     *
     * @param args
     * @param args1
     * @return true/false
     */
    public static boolean equals(Double args, Double args1) {
        if (isNull(args)) return isNull(args1);
        return 0 == args.compareTo(args1);
    }

    /**
     * Float类型比较
     *
     * @param args
     * @param args1
     * @return true/false
     */
    public static boolean equals(Float args, Float args1) {
        if (isNull(args)) return isNull(args1);
        return 0 == args.compareTo(args1);
    }

    /**
     * 字符串比较（忽略大小写）.
     *
     * @param args
     * @param args1
     * @return true/false
     */
    public static boolean equalsIgnoreCase(String args, String args1) {
        return !args.equalsIgnoreCase(args1);
    }

    /**
     * 字符穿判空.
     *
     * @param str current str
     * @return true/false
     */
    public static boolean isNull(String str) {
        return null == str;
    }

    /**
     * 字符穿判空（包含长度判断）.
     *
     * @param str current str
     * @return true/false
     */
    public static boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }

    /**
     * 对象判空.
     *
     * @param obj
     * @return true/false
     */
    public static boolean isNull(Object obj) {
        return Objects.isNull(obj);
    }

    /**
     * 对象判空.
     *
     * @param obj
     * @return <T>obj
     */
    public static <T> T isNullRequire(T obj) {
        return Objects.requireNonNull(obj, "T obj is null");
    }

    /**
     * 对象判空.
     *
     * @param obj
     * @return <T>obj
     */
    public static <T> T isNullRequire(T obj, String throwContent) {
        return Objects.requireNonNull(obj, throwContent);
    }

    /**
     * 对象判空.
     *
     * @param obj
     * @return true/false
     */
    public static boolean isEmpty(Object obj) {
        if (obj instanceof String)
            return isEmpty(convertString(obj));
        return isNull(obj);
    }


    /**
     * 数组只判空不判断长度.
     *
     * @param objects object 数组
     * @return true/false
     */
    public static <T> boolean isNull(T[] objects) {
        return null == objects;
    }

    /**
     * 数组判空.
     *
     * @param objects object 数组
     * @return true/false
     */
    public static <T> boolean isEmpty(T[] objects) {
        return null == objects || 0 == objects.length;
    }

    /**
     * 数组判空(byte不支持泛型不知道为什么).
     *
     * @param objects byte 数组
     * @return true/false
     */
    public static boolean isEmpty(byte[] objects) {
        return null == objects || 0 == objects.length;
    }

    /**
     * List集合只判空不判断长度.
     *
     * @param objects object 数组
     * @return true/false
     */
    public static boolean isNull(List<?> objects) {
        return null == objects;
    }

    /**
     * List集合判空.
     *
     * @param objects object 数组
     * @return true/false
     */
    public static boolean isEmpty(List<?> objects) {
        return null == objects || objects.isEmpty();
    }

    /**
     * Map集合判空只判断对象不判断长度.
     *
     * @param map map集合
     * @param <T> map key type
     * @param <R> map value type
     * @return true/false
     */
    public static <T, R> boolean isNull(Map<T, R> map) {
        return null == map;
    }

    /**
     * Map集合判空.
     *
     * @param map map集合
     * @param <T> map key type
     * @param <R> map value type
     * @return true/false
     */
    public static <T, R> boolean isEmpty(Map<T, R> map) {
        return isNull(map) || map.isEmpty();
    }

    /**
     * 是否包含指定类型
     *
     * @param list 指定的集合
     * @param t    指定的元素
     * @param <T>  元素类型
     * @return true/false
     */
    public static <T> boolean isContain(List<T> list, T t) {
        if (isEmpty(list)) return false;
        return list.contains(t);
    }

    /**
     * 是否包含指定类型
     *
     * @param stack 指定的集合
     * @param t     指定的元素
     * @param <T>   元素类型
     * @return true/false
     */
    public static <T> boolean isContain(Stack<T> stack, T t) {
        if (isEmpty(stack)) return false;
        return stack.contains(t);
    }

    /**
     * 是否包含指定的Key
     *
     * @param objects 集合
     * @param key     指定的Key
     * @param <K>     元素Key的类型
     * @param <V>     元素Value的类型
     * @return true/false
     */
    public static <K, V> boolean isContain(Hashtable<K, V> objects, K key) {
        if (isEmpty(objects)) return false;
        return objects.containsKey(key);
    }

    /**
     * 是否包含指定的Key
     *
     * @param objects 集合
     * @param key     指定的Key
     * @param <K>     元素Key的类型
     * @param <V>     元素Value的类型
     * @return true/false
     */
    public static <K, V> boolean isContain(Map<K, V> objects, K key) {
        if (isEmpty(objects)) return false;
        return objects.containsKey(key);
    }

    /**
     * 是否包含元素，且返回Key对应的Value
     *
     * @param hashtable this hashtable
     * @param key       元素的Key
     * @param <K>       Key的类型
     * @param <V>       Value的类型
     * @return Key对应的Value
     */
    public static <K, V> V containToValue(Hashtable<K, V> hashtable, K key) {
        if (isEmpty(hashtable)) return null;
        if (hashtable.containsKey(key)) {
            return hashtable.get(key);
        }
        return null;
    }

    /**
     * 是否包含元素，且返回Key对应的Value
     *
     * @param map this map
     * @param key 元素的Key
     * @param <K> Key的类型
     * @param <V> Value的类型
     * @return Key对应的Value
     */
    public static <K, V> V containToValue(Map<K, V> map, K key) {
        if (isEmpty(map)) return null;
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    /**
     * 是否包含元素的Key，且返回Value对应的Key
     *
     * @param map   this map
     * @param value 元素的value
     * @param <K>   Key的类型
     * @param <V>   Value的类型
     * @return Key对应的Value
     */
    public static <K, V> K containToKey(Map<K, V> map, V value) {
        if (isEmpty(map)) return null;
        if (map.containsValue(value)) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (entry.getValue().equals(value)) return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 添加元素
     *
     * @param list this list
     * @param args 被添加元素的集合
     * @param <T>  元素类型
     * @return 添加后的list集合
     */
    public static <T> List<T> push(List<T> list, T... args) {
        if (isNull(list)) list = new ArrayList<>();
        for (T t : args) {
            if (isEmpty(t)) continue;
            if (isContain(list, t)) list.remove(t);
            list.add(t);
        }
        return list;
    }

    /**
     * 添加元素
     *
     * @param stack this stack
     * @param args  被添加元素的集合
     * @param <T>   元素类型
     * @return 添加后的stack集合
     */
    @SafeVarargs
    public static <T> Stack<T> push(Stack<T> stack, T... args) {
        if (isNull(stack)) stack = new Stack<>();
        for (T t : args) {
            stack.push(t);
        }
        return stack;
    }

    /**
     * 添加元素
     *
     * @param map this map
     * @param k   增加元素的Key
     * @param v   增加元素的Value
     * @param <K> 元素Key的类型
     * @param <V> 元素Value的类型
     * @return 增加后的集合
     */
    public static <K, V> Map<K, V> push(Map<K, V> map, K k, V v) {
        if (isNull(map)) map = new HashMap<>();
        map.put(k, v);
        return map;
    }

    /**
     * 添加元素
     *
     * @param hashtable this hashtable
     * @param k         增加元素的Key
     * @param v         增加元素的Value
     * @param <K>       元素Key的类型
     * @param <V>       元素Value的类型
     * @return 增加后的集合
     */
    public static <K, V> Hashtable<K, V> push(Hashtable<K, V> hashtable, K k, V v) {
        if (isNull(hashtable)) hashtable = new Hashtable<>();
        hashtable.put(k, v);
        return hashtable;
    }

    /**
     * 弹出指定元素
     *
     * @param list this list
     * @param t    元素实体
     * @param <T>  元素类型
     * @return 移除后的集合
     */
    public static <T> List<T> remove(List<T> list, T t) {
        if (!isEmpty(list)) list.remove(t);
        return list;
    }

    /**
     * 弹出指定元素
     *
     * @param stack this stack
     * @param t     元素实体
     * @param <T>   元素类型
     * @return 移除后的集合
     */
    public static <T> Stack<T> remove(Stack<T> stack, T t) {
        if (!isEmpty(stack)) stack.remove(t);
        return stack;
    }

    /**
     * 弹出指定位置的元素
     *
     * @param list  this list
     * @param index 指定的下标
     * @return 移除后集合
     */
    public static List<?> remove(List<?> list, int index) {
        if (!isEmpty(list)) list.remove(index);
        return list;
    }

    /**
     * 弹出指定位置的元素
     *
     * @param stack this stack
     * @param index 指定的下标
     * @return 移除后集合
     */
    public static Stack<?> remove(Stack<?> stack, int index) {
        if (!isEmpty(stack)) stack.remove(index);
        return stack;
    }

    /**
     * 弹出指定元素
     *
     * @param map this map
     * @param k   被移除元素的Key
     * @param <K> 元素Key类型
     * @param <V> 元素类型
     * @return 移除后的map
     */
    public static <K, V> Map<K, V> remove(Map<K, V> map, K k) {
        if (!isEmpty(map)) map.remove(k);
        return map;
    }

    /**
     * 弹出指定元素
     *
     * @param hashtable this hashtable
     * @param k         被移除元素的Key
     * @param <K>       元素Key类型
     * @param <V>       元素类型
     * @return 移除后的hashtable
     */
    public static <K, V> Hashtable<K, V> remove(Hashtable<K, V> hashtable, K k) {
        if (!isEmpty(hashtable)) hashtable.remove(k);
        return hashtable;
    }

    /**
     * 获取集合最后一个元素且返回该元素 但不做移除动作
     *
     * @param list this list
     * @param <T>  元素类型
     * @return 获取到的元素
     */
    public static <T> T peek(List<T> list) {
        if (!isEmpty(list)) return list.get(list.size() - 1);
        return null;
    }

    /**
     * 获取集合最后一个元素且返回该元素 但不做移除动作
     *
     * @param stack this stack
     * @param <T>   元素类型
     * @return 获取到的元素
     */
    public static <T> T peek(Stack<T> stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        try {
            return stack.peek();
        } catch (Exception e) {
            Logger.e(e.toString());
            return null;
        }
    }

    /**
     * 弹出集合的最后一个元素
     *
     * @param stack this stack
     * @param <T>   元素类型
     * @return 被移除的元素实体
     */
    public static <T> T pop(Stack<T> stack) {
        if (!isEmpty(stack)) return stack.pop();
        return null;
    }

    /**
     * 弹出集合的最后一个元素
     *
     * @param list this list
     * @param <T>  元素类型
     * @return 被移除的元素实体
     */
    public static <T> T pop(List<T> list) {
        if (isEmpty(list)) return null;
        // TODO: 2025/3/25 甲方不支持35的API，仓库编译会报错
//        if (Build.VERSION.SDK_INT >= 35) {
//            return list.removeLast();
//        } else
        {
            T t = list.get(list.size() - 1);
            list.remove(t);
            return t;
        }
    }

    /**
     * 弹出集合的最后一个元素
     *
     * @param hashtable this list
     * @param k         被移除元素的Key
     * @param <K>       元素Key类型
     * @param <V>       元素类型
     * @return 被移除的元素实体
     */
    public static <K, V> V pop(Hashtable<K, V> hashtable, K k) {
        if (!isEmpty(hashtable)) return hashtable.remove(k);
        return null;
    }

    /**
     * 获取元素在List集合最后出现的位置
     *
     * @param list 指定list集合
     * @param t    指定元素
     * @param <T>  元素类型
     * @return 返回下标
     */
    public static <T> int lastIndexOf(List<T> list, T t) {
        if (isEmpty(list)) return -1;
        return list.lastIndexOf(t);
    }

    /**
     * 获取元素在Stack集合最后出现的位置
     *
     * @param stack 指定list集合
     * @param t     指定元素
     * @param <T>   元素类型
     * @return 返回下标
     */
    public static <T> int lastIndexOf(Stack<T> stack, T t) {
        if (isEmpty(stack)) return -1;
        return stack.lastIndexOf(t);
    }

    /**
     * 根据下标查询元素
     *
     * @param list  指定list集合
     * @param index 指定元素
     * @param <T>   元素类型
     * @return 返回元素
     */
    public static <T> T get(List<T> list, int index) {
        if (isEmpty(list)) return null;
        return list.get(index);
    }

    /**
     * 根据下标查询元素
     *
     * @param stack 指定list集合
     * @param index 指定元素
     * @param <T>   元素类型
     * @return 返回元素
     */
    public static <T> T get(Stack<T> stack, int index) {
        if (isEmpty(stack)) return null;
        return stack.get(index);
    }

    /**
     * 清空集合
     *
     * @param list 指定list集合
     */
    public static <T> void clear(List<T> list) {
        if (isEmpty(list)) return;
        list.clear();
    }

    /**
     * 清空集合
     *
     * @param stack 指定list集合
     */
    public static <T> void clear(Stack<T> stack) {
        if (isEmpty(stack)) return;
        stack.removeAllElements();
    }

    /**
     * 清空集合
     *
     * @param map 指定map集合
     */
    public static <K, V> void clear(Map<K, V> map) {
        if (isEmpty(map)) return;
        map.clear();
    }

    /**
     * 清空集合
     *
     * @param hashtable 指定hashtable集合
     */
    public static <K, V> void clear(Hashtable<K, V> hashtable) {
        if (isEmpty(hashtable)) return;
        hashtable.clear();
    }

    /**
     * 比较大小 Long类型
     *
     * @param arg  要比较的元素
     * @param args 被比较的元素
     * @return 0: arg = args、1: arg > args、-1：arg < args
     */
    public static int compare(Long arg, Long args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return arg.compareTo(args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 比较大小 Long类型
     *
     * @param arg  要比较的元素
     * @param args 被比较的元素
     * @return true: arg > args
     */
    public static boolean compareGreater(Long arg, Long args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return 0 < arg.compareTo(args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 比较大小 Integer类型
     *
     * @param arg  要比较的元素
     * @param args 被比较的元素
     * @return 0: arg = args、1: arg > args、-1：arg < args
     */
    public static int compare(Integer arg, Integer args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return arg.compareTo(args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 比较大小 Integer类型
     *
     * @param arg  要比较的元素
     * @param args 被比较的元素
     * @return true: arg > args
     */
    public static boolean compareGreater(Integer arg, Integer args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return 0 < arg.compareTo(args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 比较大小 Double类型
     *
     * @param arg  要比较的元素
     * @param args 被比较的元素
     * @return 0: arg = args、1: arg > args、-1：arg < args
     */
    public static int compare(Double arg, Double args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return arg.compareTo(args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 比较大小 Double类型
     *
     * @param arg  要比较的元素
     * @param args 被比较的元素
     * @return true: arg > args
     */
    public static boolean compareGreater(Double arg, Double args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return 0 < arg.compareTo(args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 比较大小 Float类型
     *
     * @param arg  要比较的元素
     * @param args 被比较的元素
     * @return 0: arg = args、1: arg > args、-1：arg < args
     */
    public static int compare(Float arg, Float args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return arg.compareTo(args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 比较大小 Float类型
     *
     * @param arg  要比较的元素
     * @param args 被比较的元素
     * @return true: arg > args
     */
    public static boolean compareGreater(Float arg, Float args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return 0 < arg.compareTo(args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 比较对象大小.
     *
     * @param obj1 要比较的对象
     * @param obj2 要比较的对象
     * @param c    比较器，一般用于指明对比属性
     * @param <T>  对象泛型
     * @return left > right 返回1 , left < right 返回-1 , left = right 返回0.
     */
    public static <T> int compare(T obj1, T obj2, Comparator<? super T> c) {
        return Objects.compare(obj1, obj2, c);
    }

    /**
     * 计算数值差.
     *
     * @param arg  减数元素
     * @param args 被减数元素
     * @return 差值
     */
    public static long numberDiff(Long arg, Long args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return Math.abs(arg - args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 计算数值差.
     *
     * @param arg  减数元素
     * @param args 被减数元素
     * @return 差值
     */
    public static int numberDiff(Integer arg, Integer args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return Math.abs(arg - args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 计算数值差.
     *
     * @param arg  减数元素
     * @param args 被减数元素
     * @return 差值
     */
    public static double numberDiff(Double arg, Double args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return Math.abs(arg - args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 计算数值差.
     *
     * @param arg  减数元素
     * @param args 被减数元素
     * @return 差值
     */
    public static float numberDiff(Float arg, Float args) {
        try {
            assert (!isNull(arg) && !isNull(args));
            return Math.abs(arg - args);
        } catch (AssertionError error) {
            throw new RuntimeException("Argument is null");
        }
    }

    /**
     * 判断一个String能否转为int
     */
    public static boolean isInteger(String str) {
        if (isEmpty(str)) {
            return false;
        }
        int len = str.length();
        if (len == 1) {
            for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
                cp = Character.codePointAt(str, i);
                if (!Character.isDigit(cp)) {
                    return false;
                }
            }
            return true;
        }
        char start = str.charAt(0);
        if (start == '+' || start == '-') {
            str = str.substring(1);
        }
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            if (!Character.isDigit(cp)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验参数.
     *
     * @param method 方法名字
     * @param args   校验的参数
     */
    public static void checkParam(String method, Object... args) {
        if (isEmpty(args)) throw new RuntimeException(method + " 参数异常可能为空，无法继续执行");
        int size = args.length;
        for (int i = 0; i < args.length; i++) {
            if (isEmpty(args[i]))
                throw new RuntimeException(method + " 第" + (i + 1) + "参数异常，无法继续执行");
        }
    }

    /**
     * 字符串中的数字转为粗体
     *
     * @param str 被加粗的字符
     * @return 富文本对象
     */
    public static SpannableString digitToBold(String str) {
        if (TextUtils.isEmpty(str)) {
            return new SpannableString("");
        }
        SpannableString spanStr = new SpannableString(str);
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i)) || Objects.equals(str.charAt(i), ':')) {
                spanStr.setSpan(new StyleSpan(Typeface.BOLD), i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        return spanStr;
    }

    /**
     * 城市信息经纬度转换
     *
     * @param input 城市信息输入
     * @return 返回转换后信息
     */
    public static double transCityLatAndLon(final double input) {
        final double scaleFactor = 1000000.0;
        return input / scaleFactor;
    }

    /**
     * 坐标转换
     *
     * @param input 信息输入
     * @return 返回转换后信息
     */
    public static double transProjectionLatAndLon(final double input) {
        final double scaleFactor = 3600000.0;
        return input / scaleFactor;
    }

    /**
     * 将米转换为公里
     *
     * @param meters 以米为单位的距离
     * @return 以公里为单位的距离
     */
    public static float convertMetersToKilometers(final long meters) {
        return meters / 1000.0f; // 使用 1000.0f 确保结果是 float 类型
    }

    // 将字符串数字保留2位小数输出
    public static String stringFormatTwo(String value, String defaultValue) {
        String numberStr = value;
        try {
            DecimalFormat df = new DecimalFormat("0.00"); // 保留2位小数
            double number = Double.parseDouble(numberStr);
            String result = df.format(number);
            return result;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }

    }
}