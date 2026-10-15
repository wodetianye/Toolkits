package com.java.utils.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.java.utils.log.Logger
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.Type

/**
 * @Introduce: Gson 解析工具类.
 * @Author: Lww
 * @Date: 2023/11/21
 * @Description: 对象序列化 / 反序列化、对象拷贝等功能.
 */
object GsonUtils {
    /** 基本类型名集合。 */
    private val BASIC_TYPES: Set<String> = hashSetOf(
        "String", "Boolean", "Byte", "Character",
        "Short", "Integer", "Long", "Float", "Double"
    )

    /** 基本类型与包装类型映射，用于 Bean 拷贝的类型兼容判断。 */
    private val PRIMITIVE_WRAPPER_MAP: Map<Class<*>, Class<*>> = hashMapOf(
        Int::class.javaPrimitiveType!! to Int::class.javaObjectType,
        Long::class.javaPrimitiveType!! to Long::class.javaObjectType,
        Double::class.javaPrimitiveType!! to Double::class.javaObjectType,
        Float::class.javaPrimitiveType!! to Float::class.javaObjectType,
        Boolean::class.javaPrimitiveType!! to Boolean::class.javaObjectType,
        Byte::class.javaPrimitiveType!! to Byte::class.javaObjectType,
        Char::class.javaPrimitiveType!! to Char::class.javaObjectType,
        Short::class.javaPrimitiveType!! to Short::class.javaObjectType
    )

    /** 序列化时包含 null 字段。 */
    private val GSON_WITH_NULL: Gson = GsonBuilder()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()

    /** 序列化时跳过 null 字段。 */
    private val GSON_WITHOUT_NULL: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .create()

    // 序列化 =================================================

    /**
     * 序列化（包含 null 字段）。
     *
     * 例：toJson(User("Tom")) -> "{\"name\":\"Tom\",\"age\":0}"
     */
    @JvmStatic
    fun toJson(obj: Any?): String {
        if (obj == null) return "null"
        return GSON_WITH_NULL.toJson(obj, obj.javaClass)
    }

    /**
     * 序列化（跳过 null 字段）。
     *
     * 例：toJsonNonNull(User("Tom")) -> "{\"name\":\"Tom\"}"
     */
    @JvmStatic
    fun toJsonNonNull(obj: Any?): String {
        if (obj == null) return "null"
        return GSON_WITHOUT_NULL.toJson(obj, obj.javaClass)
    }

    /**
     * 序列化数组。
     *
     * 例：toJson(arrayOf(user)) -> "[{...}]"
     */
    @JvmStatic
    fun <T> toJson(array: Array<T>?): String {
        if (array == null) return "null"
        return GSON_WITH_NULL.toJson(array, array.javaClass)
    }

    // 反序列化 =================================================

    /**
     * 反序列化为对象。
     *
     * 例：fromJson("{\"name\":\"Tom\"}", User::class.java) -> User(name="Tom")
     */
    @JvmStatic
    fun <T> fromJson(json: String?, clazz: Class<T>?): T? {
        if (json.isNullOrEmpty() || clazz == null) return null
        return runCatching { GSON_WITH_NULL.fromJson(json, clazz) }
            .onFailure { Logger.e("GsonUtils fromJson failed: ${it.message}") }
            .getOrNull()
    }

    /**
     * 反序列化为 List（指定元素类型）。
     *
     * 例：fromJson2List("[{\"name\":\"Tom\"}]", User::class.java) -> List<User>
     */
    @JvmStatic
    fun <T> fromJson2List(json: String?, clazz: Class<T>?): List<T> {
        if (json.isNullOrEmpty() || clazz == null) return emptyList()
        val type: Type = TypeToken.getParameterized(List::class.java, clazz).type
        return runCatching { GSON_WITH_NULL.fromJson<List<T>>(json, type) }
            .onFailure { Logger.e("GsonUtils fromJson2List failed: ${it.message}") }
            .getOrNull() ?: emptyList()
    }

    /**
     * 反序列化为 List（指定 Type）。
     *
     * 例：fromJson2ListWithType(json, type) -> List<T>
     */
    @JvmStatic
    fun <T> fromJson2ListWithType(json: String?, type: Type?): List<T> {
        if (json.isNullOrEmpty() || type == null) return emptyList()
        return runCatching { GSON_WITH_NULL.fromJson<List<T>>(json, type) }
            .onFailure { Logger.e("GsonUtils fromJson2ListWithType failed: ${it.message}") }
            .getOrNull() ?: emptyList()
    }

    /**
     * 反序列化为 ArrayList（指定 Type）。
     *
     * 例：fromJson2ArrayList(json, type) -> ArrayList<T>
     */
    @JvmStatic
    fun <T> fromJson2ArrayList(json: String?, type: Type?): ArrayList<T> {
        if (json.isNullOrEmpty() || type == null) return ArrayList()
        return runCatching { GSON_WITH_NULL.fromJson<ArrayList<T>>(json, type) }
            .onFailure { Logger.e("GsonUtils fromJson2ArrayList failed: ${it.message}") }
            .getOrNull() ?: ArrayList()
    }

    /**
     * 反序列化为 List（数组方式）。
     *
     * 例：fromJsonList("[{...}]", Array<User>::class.java) -> List<User>
     */
    @JvmStatic
    fun <T> fromJsonList(json: String?, clazz: Class<Array<T>>?): List<T> {
        if (json.isNullOrEmpty() || clazz == null) return emptyList()
        return runCatching {
            GSON_WITH_NULL.fromJson<Array<T>>(json, clazz)?.toList() ?: emptyList()
        }.onFailure {
            Logger.e("GsonUtils fromJsonList failed: ${it.message}")
        }.getOrNull() ?: emptyList()
    }

    /**
     * 反序列化为数组。
     *
     * 例：fromJsonArray("[{...}]", Array<User>::class.java) -> Array<User>
     */
    @JvmStatic
    fun <T> fromJsonArray(json: String?, clazz: Class<Array<T>>?): Array<T>? {
        if (json.isNullOrEmpty() || clazz == null) return null
        return runCatching { GSON_WITH_NULL.fromJson<Array<T>>(json, clazz) }
            .onFailure { Logger.e("GsonUtils fromJsonArray failed: ${it.message}") }
            .getOrNull()
    }

    // 基本类型数组反序列化 =================================================

    /**
     * JSON 数组 → IntArray。
     *
     * 例：fromJson2IntArray("[1,2,3]") -> intArrayOf(1, 2, 3)
     */
    @JvmStatic
    fun fromJson2IntArray(json: String?): IntArray {
        if (json.isNullOrEmpty()) return IntArray(0)
        return runCatching { GSON_WITH_NULL.fromJson(json, IntArray::class.java) }
            .onFailure { Logger.e("GsonUtils fromJson2IntArray failed: ${it.message}") }
            .getOrNull() ?: IntArray(0)
    }

    /**
     * JSON 数组 → LongArray。
     *
     * 例：fromJson2LongArray("[1,2,3]") -> longArrayOf(1L, 2L, 3L)
     */
    @JvmStatic
    fun fromJson2LongArray(json: String?): LongArray {
        if (json.isNullOrEmpty()) return LongArray(0)
        return runCatching { GSON_WITH_NULL.fromJson(json, LongArray::class.java) }
            .onFailure { Logger.e("GsonUtils fromJson2LongArray failed: ${it.message}") }
            .getOrNull() ?: LongArray(0)
    }

    /**
     * JSON 数组 → DoubleArray。
     */
    @JvmStatic
    fun fromJson2DoubleArray(json: String?): DoubleArray {
        if (json.isNullOrEmpty()) return DoubleArray(0)
        return runCatching { GSON_WITH_NULL.fromJson(json, DoubleArray::class.java) }
            .onFailure { Logger.e("GsonUtils fromJson2DoubleArray failed: ${it.message}") }
            .getOrNull() ?: DoubleArray(0)
    }

    /**
     * JSON 数组 → FloatArray。
     */
    @JvmStatic
    fun fromJson2FloatArray(json: String?): FloatArray {
        if (json.isNullOrEmpty()) return FloatArray(0)
        return runCatching { GSON_WITH_NULL.fromJson(json, FloatArray::class.java) }
            .onFailure { Logger.e("GsonUtils fromJson2FloatArray failed: ${it.message}") }
            .getOrNull() ?: FloatArray(0)
    }

    /**
     * JSON 数组 → BooleanArray。
     */
    @JvmStatic
    fun fromJson2BooleanArray(json: String?): BooleanArray {
        if (json.isNullOrEmpty()) return BooleanArray(0)
        return runCatching { GSON_WITH_NULL.fromJson(json, BooleanArray::class.java) }
            .onFailure { Logger.e("GsonUtils fromJson2BooleanArray failed: ${it.message}") }
            .getOrNull() ?: BooleanArray(0)
    }

    /**
     * JSON 数组 → ByteArray。
     */
    @JvmStatic
    fun fromJson2ByteArray(json: String?): ByteArray {
        if (json.isNullOrEmpty()) return ByteArray(0)
        return runCatching { GSON_WITH_NULL.fromJson(json, ByteArray::class.java) }
            .onFailure { Logger.e("GsonUtils fromJson2ByteArray failed: ${it.message}") }
            .getOrNull() ?: ByteArray(0)
    }

    /**
     * JSON 数组 → CharArray。
     */
    @JvmStatic
    fun fromJson2CharArray(json: String?): CharArray {
        if (json.isNullOrEmpty()) return CharArray(0)
        return runCatching { GSON_WITH_NULL.fromJson(json, CharArray::class.java) }
            .onFailure { Logger.e("GsonUtils fromJson2CharArray failed: ${it.message}") }
            .getOrNull() ?: CharArray(0)
    }

    /**
     * JSON 数组 → ShortArray。
     */
    @JvmStatic
    fun fromJson2ShortArray(json: String?): ShortArray {
        if (json.isNullOrEmpty()) return ShortArray(0)
        return runCatching { GSON_WITH_NULL.fromJson(json, ShortArray::class.java) }
            .onFailure { Logger.e("GsonUtils fromJson2ShortArray failed: ${it.message}") }
            .getOrNull() ?: ShortArray(0)
    }

    // 对象转换 =================================================

    /**
     * 对象转换（通过 JSON 中转）。
     *
     * 例：convert(User("Tom"), UserVo::class.java) -> UserVo(name="Tom")
     */
    @JvmStatic
    fun <T> convert(source: Any?, targetClass: Class<T>?): T? {
        if (source == null || targetClass == null) return null
        val json = toJson(source)
        return fromJson(json, targetClass)
    }

    /**
     * List 对象转换。
     *
     * 例：convertList(userList, UserVo::class.java) -> ArrayList<UserVo>
     */
    @JvmStatic
    fun <T> convertList(sourceList: List<*>?, targetClass: Class<T>?): ArrayList<T> {
        if (sourceList.isNullOrEmpty() || targetClass == null) return ArrayList()
        val json = toJson(sourceList)
        val type = TypeToken.getParameterized(ArrayList::class.java, targetClass).type
        return fromJson2ArrayList(json, type)
    }

    // Bean 拷贝 =================================================

    /**
     * Bean 拷贝（source 的 null 字段不覆盖 target）。
     *
     * 例：copyBean(source, target)
     */
    @JvmStatic
    @JvmOverloads
    fun copyBean(source: Any?, target: Any?, overwriteNull: Boolean = false) {
        if (source == null || target == null) {
            Logger.w("copyBean: source or target is null")
            return
        }

        val sourceFields = getDeclaredFields(source.javaClass)
        val targetFields = getDeclaredFields(target.javaClass)
        val targetFieldMap = createFieldMap(targetFields)

        for (sourceField in sourceFields) {
            val fieldName = sourceField.name
            val targetField = targetFieldMap[fieldName] ?: continue
            if (isFieldCopyable(sourceField, targetField)) {
                copyFieldValue(source, sourceField, target, targetField, overwriteNull)
            }
        }
    }

    // 反射工具 =================================================

    private fun getDeclaredFields(clazz: Class<*>): Array<Field> {
        val fields = mutableListOf<Field>()
        var current: Class<*>? = clazz
        while (current != null && current != Any::class.java) {
            fields.addAll(current.declaredFields)
            current = current.superclass
        }
        return fields.toTypedArray()
    }

    private fun createFieldMap(fields: Array<Field>): Map<String, Field> {
        val map = HashMap<String, Field>()
        for (field in fields) {
            map[field.name] = field
        }
        return map
    }

    private fun isFieldCopyable(sourceField: Field, targetField: Field): Boolean {
        return sourceField.type == targetField.type ||
                isAssignable(sourceField.type, targetField.type)
    }

    private fun isAssignable(sourceType: Class<*>, targetType: Class<*>): Boolean {
        return targetType.isAssignableFrom(sourceType) ||
                isWrapperTypeAssignable(sourceType, targetType)
    }

    private fun isWrapperTypeAssignable(sourceType: Class<*>, targetType: Class<*>): Boolean {
        val sourceWrapper = PRIMITIVE_WRAPPER_MAP[sourceType]
        val targetWrapper = PRIMITIVE_WRAPPER_MAP[targetType]
        return (sourceWrapper != null && sourceWrapper == targetType) ||
                (targetWrapper != null && targetWrapper == sourceType) ||
                (sourceWrapper != null && targetWrapper != null && sourceWrapper == targetWrapper)
    }

    private fun copyFieldValue(
        source: Any,
        sourceField: Field,
        target: Any,
        targetField: Field,
        overwriteNull: Boolean
    ) {
        try {
            if (Modifier.isFinal(targetField.modifiers)) return

            sourceField.isAccessible = true
            targetField.isAccessible = true

            val value = sourceField.get(source)
            if (value == null) {
                if (overwriteNull) targetField.set(target, null)
                return
            }

            // 基本类型 / String / 包装类 直接 set
            if (isBasicTypeOrString(value.javaClass)) {
                targetField.set(target, value)
                return
            }

            // 集合：深拷贝
            if (value is Collection<*>) {
                targetField.set(target, deepCopyCollection(value))
                return
            }

            // Map：深拷贝
            if (value is Map<*, *>) {
                targetField.set(target, deepCopyMap(value))
                return
            }

            // 数组：引用类型深拷贝，基本类型直接共享
            if (value.javaClass.isArray) {
                val componentType = value.javaClass.componentType
                if (componentType != null && !componentType.isPrimitive) {
                    val array = value as Array<*>
                    targetField.set(target, array.map { deepCopyValue(it) }.toTypedArray())
                } else {
                    targetField.set(target, value)
                }
                return
            }

            // 复杂对象：递归拷贝
            var targetValue = targetField.get(target)
            if (targetValue == null) {
                targetValue = createInstance(targetField.type)
                targetField.set(target, targetValue)
            }
            if (targetValue != null) {
                copyBean(value, targetValue, overwriteNull)
            }
        } catch (e: Exception) {
            Logger.e("Failed to copy field: ${sourceField.name}, ${e.message}")
        }
    }

    // 深拷贝 =================================================

    /**
     * 深拷贝集合（List / Set）。
     *
     * 例：[User("Tom")] -> [User("Tom")]（新对象，不共享引用）
     */
    private fun deepCopyCollection(source: Collection<*>): Collection<Any?> {
        val result: MutableCollection<Any?> = when (source) {
            is Set<*> -> LinkedHashSet(source.size)
            else -> ArrayList(source.size)
        }
        for (item in source) {
            result.add(deepCopyValue(item))
        }
        return result
    }

    /**
     * 深拷贝 Map。
     *
     * 例：{"a": User("Tom")} -> {"a": User("Tom")}（新对象）
     */
    private fun deepCopyMap(source: Map<*, *>): Map<Any?, Any?> {
        val result = LinkedHashMap<Any?, Any?>(source.size)
        for ((k, v) in source) {
            result[deepCopyValue(k)] = deepCopyValue(v)
        }
        return result
    }

    /**
     * 深拷贝单个值（递归处理集合 / Map / 数组 / 普通对象）。
     */
    private fun deepCopyValue(value: Any?): Any? {
        if (value == null) return null

        // 基本类型 / String / 包装类 直接返回
        if (isBasicTypeOrString(value.javaClass)) return value

        // 集合
        if (value is Collection<*>) return deepCopyCollection(value)

        // Map
        if (value is Map<*, *>) return deepCopyMap(value)

        // 数组：只处理引用类型数组，基本类型数组直接返回
        if (value.javaClass.isArray) {
            val componentType = value.javaClass.componentType
            if (componentType != null && componentType.isPrimitive) {
                return value
            }
            val array = value as Array<*>
            return array.map { deepCopyValue(it) }.toTypedArray()
        }

        // 普通对象：新建实例后递归拷贝
        val newInstance = createInstance(value.javaClass) ?: return value
        copyBean(value, newInstance, true)
        return newInstance
    }

    //  实例化 =================================================

    private fun createInstance(clazz: Class<*>): Any? {
        if (clazz.isInterface || Modifier.isAbstract(clazz.modifiers)) {
            Logger.e("Cannot instantiate interface or abstract class: ${clazz.name}")
            return null
        }
        return runCatching { clazz.getDeclaredConstructor().newInstance() }
            .onFailure { Logger.e("Create instance failed: ${it.message}") }
            .getOrNull()
    }

    private fun isBasicTypeOrString(clazz: Class<*>): Boolean {
        return clazz.isPrimitive ||
                BASIC_TYPES.contains(clazz.simpleName) ||
                clazz == String::class.java
    }
}