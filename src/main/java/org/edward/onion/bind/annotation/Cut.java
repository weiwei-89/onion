package org.edward.onion.bind.annotation;

import org.edward.onion.bind.model.CONVERT;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cut {
    /**
     * 标签
     * @return
     */
    String tag();

    /**
     * 是否可用
     * @return
     */
    boolean available() default true;

    /**
     * 是否忽略空字符串
     * @return
     */
    boolean ignoreEmptyString() default true;

    /**
     * 是否转换
     * @return
     */
    boolean convert() default false;

    /**
     * 转换定义
     * @return
     */
    Class<? extends Enum> convertDefination() default CONVERT.class;

    /**
     * 转换的key
     * @return
     */
    String convertKey() default "";

    /**
     * 转换的value
     * @return
     */
    String convertValue() default "";
}