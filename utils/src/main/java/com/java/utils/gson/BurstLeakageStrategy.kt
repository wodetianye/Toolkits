package com.java.utils.gson

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * @Introduce: @BurstLeakage 注解配套的 Gson 工厂.
 * @Author: Lww
 * @Date: 2023/11/21
 * @Description: 创建已注册 @BurstLeakage 排除策略的 Gson 实例，
 *               控制字段的序列化 / 反序列化行为。
 *
 * 用法：
 * <pre>
 * Gson gson = BurstLeakageGson.createDefault();
 * String json = gson.toJson(user);
 * </pre>
 *
 * Bean 字段上加注解：
 * <pre>
 * &#64;BurstLeakage(serialize = false)
 * public String password;
 * </pre>
 */
object BurstLeakageGson {

    // 公开方法 =================================================

    /**
     * 创建默认 Gson（包含 null 字段、禁用 HTML 转义、启用 @BurstLeakage）。
     *
     * 例：BurstLeakageGson.createDefault() -> Gson
     */
    @JvmStatic
    fun createDefault(): Gson = create(serializeNulls = true, prettyPrinting = false)

    /**
     * 创建 Gson。
     *
     * 例：BurstLeakageGson.create(true, false) -> Gson
     *
     * @param serializeNulls 是否序列化 null 字段
     * @param prettyPrinting 是否美化输出
     */
    @JvmStatic
    @JvmOverloads
    fun create(
        serializeNulls: Boolean = true,
        prettyPrinting: Boolean = false
    ): Gson {
        val builder = GsonBuilder()
            .disableHtmlEscaping()
            .addSerializationExclusionStrategy(BurstLeakageStrategy(true))
            .addDeserializationExclusionStrategy(BurstLeakageStrategy(false))

        if (serializeNulls) builder.serializeNulls()
        if (prettyPrinting) builder.setPrettyPrinting()

        return builder.create()
    }

    // @BurstLeakage 策略 =================================================

    /**
     * @BurstLeakage 排除策略。
     *
     * - [serialization] = true：用于序列化，读取 [BurstLeakage.serialize]
     * - [serialization] = false：用于反序列化，读取 [BurstLeakage.deserialize]
     */
    private class BurstLeakageStrategy(private val serialization: Boolean) : ExclusionStrategy {

        override fun shouldSkipField(f: FieldAttributes): Boolean {
            val annotation = f.getAnnotation(BurstLeakage::class.java) ?: return false
            return if (serialization) !annotation.serialize else !annotation.deserialize
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean = false
    }
}