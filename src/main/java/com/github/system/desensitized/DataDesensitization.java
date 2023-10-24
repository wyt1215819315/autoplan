package com.github.system.desensitized;

import com.github.system.desensitized.DesensitizedType;

import java.lang.annotation.*;

/**
 * 指定某一个字段是否需要脱敏
 * 已经注册为序列化配置，返回的实体中包含这个注解的都会被脱敏
 * DesensitizedType仅对string类型生效
 * int long会以normal方式填充0
 * 其余类型字段会直接填充为NULL
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataDesensitization {

    DesensitizedType value() default DesensitizedType.NORMAL;


}
