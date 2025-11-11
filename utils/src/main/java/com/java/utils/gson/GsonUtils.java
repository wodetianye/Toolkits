package com.java.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.java.utils.ConvertUtils;
import com.java.utils.log.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @Introduce: Gson解析工具类
 * @Author: Lww
 * @Date: 2023/11/21
 * @Description: 对象序列化/反序列化、对象copy等功能
 */
public class GsonUtils {

    // 使用Set提高查找性能
    private static final Set<String> BASIC_TYPES = Set.of(
            "String", "Boolean", "Byte", "Character", "Short",
            "Integer", "Long", "Float", "Double"
    );

    // 线程安全的Gson实例
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
//            .setDateFormat("yyyy-MM-dd")
//            .setPrettyPrinting()
            .create();

    private static final Gson GSON_NON_NULL = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    private GsonUtils() {
        // 工具类，防止实例化
    }

    /**
     * 序列化（包含null字段）
     */
    public static String toJson(Object object) {
        ConvertUtils.checkParam("toJson", object, GSON);
        return GSON.toJson(object, object.getClass());
    }

    /**
     * 序列化不包含null字段
     */
    public static String toJsonNonNull(Object object) {
        ConvertUtils.checkParam("toJsonNonNull", object, GSON_NON_NULL);
        return GSON_NON_NULL.toJson(object, object.getClass());
    }

    /**
     * 序列化数组
     */
    public static <T> String toJson(T[] array) {
        ConvertUtils.checkParam("toJson", array, GSON);
        return GSON.toJson(array, array.getClass());
    }

    /**
     * 反序列化（带参数检查）
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        ConvertUtils.checkParam("fromJson", json, GSON);
        return GSON.fromJson(json, clazz);
    }

    /**
     * 反序列化（无参数检查）
     */
    public static <T> T fromJsonV2(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 反序列化为List
     */
    public static <T> List<T> fromJson2List(String json, Class<T> clazz) {
        ConvertUtils.checkParam("fromJson2List", json, GSON);
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return GSON.fromJson(json, type);
    }

    /**
     * 反序列化为List
     */
    public static <T> List<T> fromJson2List(String json, Type type) {
        ConvertUtils.checkParam("fromJson2List", json, GSON);
        return GSON.fromJson(json, type);
    }

    /**
     * 反序列化为ArrayList
     */
    public static <T> ArrayList<T> fromJson2ArrayList(String json, Type type) {
        ConvertUtils.checkParam("fromJson2ArrayList", json, type, GSON);
        return GSON.fromJson(json, type);
    }

    /**
     * 反序列化为List（数组方式）
     */
    public static <T> List<T> fromJsonList(String json, Class<T[]> clazz) {
        ConvertUtils.checkParam("fromJsonList", json, GSON);
        T[] array = GSON.fromJson(json, clazz);
        return array != null ? Arrays.asList(array) : Collections.emptyList();
    }

    /**
     * 反序列化为数组
     */
    public static <T> T[] fromJsonArray(String json, Class<T[]> clazz) {
        ConvertUtils.checkParam("fromJsonArray", json, GSON);
        return GSON.fromJson(json, clazz);
    }

    /**
     * 对象转换
     */
    public static <T> T convertToT(Object source, Class<T> targetClass) {
        String json = toJson(source);
        return fromJson(json, targetClass);
    }

    /**
     * List对象转换
     */
    public static <T> ArrayList<T> fromJson2List(ArrayList<?> sourceList, Class<T> targetClass) {
        ConvertUtils.checkParam("fromJson2List", sourceList, GSON);
        String json = toJson(sourceList);
        Type type = TypeToken.getParameterized(ArrayList.class, targetClass).getType();
        return fromJson2List(json, type);
    }

    /**
     * Bean拷贝（支持相同字段名的对象拷贝）
     * 注意：不支持非静态内部类
     */
    public static void copyBean(Object source, Object target) {
        if (source == null || target == null) {
            Logger.w("copyBean: source or target is null");
            return;
        }

        Field[] sourceFields = getDeclaredFields(source.getClass());
        Field[] targetFields = getDeclaredFields(target.getClass());

        Map<String, Field> targetFieldMap = createFieldMap(targetFields);

        for (Field sourceField : sourceFields) {
            if (sourceField == null) continue;

            String fieldName = sourceField.getName();
            Field targetField = targetFieldMap.get(fieldName);

            if (targetField != null && isFieldCopyable(sourceField, targetField)) {
                copyFieldValue(source, sourceField, target, targetField);
            }
        }
    }

    /**
     * 获取类的所有字段（包括父类）
     */
    private static Field[] getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields.toArray(new Field[0]);
    }

    /**
     * 创建字段名到Field对象的映射
     */
    private static Map<String, Field> createFieldMap(Field[] fields) {
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            if (field != null) {
                fieldMap.put(field.getName(), field);
            }
        }
        return fieldMap;
    }

    /**
     * 判断字段是否可拷贝
     */
    private static boolean isFieldCopyable(Field sourceField, Field targetField) {
        return sourceField.getType().equals(targetField.getType()) ||
                isAssignable(sourceField.getType(), targetField.getType());
    }

    /**
     * 类型转换检查
     */
    private static boolean isAssignable(Class<?> sourceType, Class<?> targetType) {
        return targetType.isAssignableFrom(sourceType) ||
                isWrapperTypeAssignable(sourceType, targetType);
    }

    /**
     * 包装类型转换检查
     */
    private static boolean isWrapperTypeAssignable(Class<?> sourceType, Class<?> targetType) {
        // 基本类型和包装类型的转换检查
        Map<Class<?>, Class<?>> primitiveWrapperMap = Map.of(
                int.class, Integer.class,
                long.class, Long.class,
                double.class, Double.class,
                float.class, Float.class,
                boolean.class, Boolean.class,
                byte.class, Byte.class,
                char.class, Character.class,
                short.class, Short.class
        );

        Class<?> sourceWrapper = primitiveWrapperMap.get(sourceType);
        Class<?> targetWrapper = primitiveWrapperMap.get(targetType);

        return (sourceWrapper != null && sourceWrapper.equals(targetType)) ||
                (targetWrapper != null && targetWrapper.equals(sourceType)) ||
                (sourceWrapper != null && targetWrapper != null && sourceWrapper.equals(targetWrapper));
    }

    /**
     * 拷贝字段值
     */
    private static void copyFieldValue(Object source, Field sourceField,
                                       Object target, Field targetField) {
        try {
            sourceField.setAccessible(true);
            targetField.setAccessible(true);

            Object value = sourceField.get(source);
            if (value == null) {
                return;
            }

            // 如果是基本类型或String，直接设置值
            if (isBasicTypeOrString(value.getClass())) {
                targetField.set(target, value);
            } else {
                // 复杂类型递归拷贝
                Object targetValue = targetField.get(target);
                if (targetValue == null) {
                    targetValue = createInstance(targetField.getType());
                    targetField.set(target, targetValue);
                }
                copyBean(value, targetValue);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            Logger.e("Failed to copy field: " + sourceField.getName(), e);
        }
    }

    /**
     * 创建实例
     */
    private static Object createInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            throw new InstantiationException("Cannot instantiate interface or abstract class: " + clazz.getName());
        }
        return clazz.newInstance();
    }

    /**
     * 判断是否为基本类型或String
     */
    private static boolean isBasicTypeOrString(Class<?> clazz) {
        return clazz.isPrimitive() ||
                BASIC_TYPES.contains(clazz.getSimpleName()) ||
                clazz == String.class;
    }
}