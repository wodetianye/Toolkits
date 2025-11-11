package com.java.utils.gson;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Introduce: .
 * @Author: lvww
 * @Date: 2023/11/21
 * @Description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface BurstLeakage {
    boolean serialize() default true;//默认序列化生效

    boolean deserialize() default true;//默认反序列化生效
}
