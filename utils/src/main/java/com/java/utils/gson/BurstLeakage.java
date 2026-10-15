package com.java.utils.gson;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Introduce: 阻断泄漏.
 * @Author: lvww
 * @Date: 2023/11/21
 * @Description: 给敏感字段打上这个注解，控制它在序列化 / 反序列化时是否参与，防止敏感数据被写进 JSON 或被外部 JSON 覆盖
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface BurstLeakage {

    /** 序列化时是否生效。true 表示包含，false 表示忽略。默认 true。 */
    boolean serialize() default true;

    /** 反序列化时是否生效。true 表示接受，false 表示忽略。默认 true。 */
    boolean deserialize() default true;
}
