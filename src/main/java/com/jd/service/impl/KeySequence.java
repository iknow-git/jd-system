
package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.*;

/**
 * 序列主键策略
 * <p>oracle</p>
 *
 * @author zashitou
 * @since 2017.4.20
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KeySequence {

    /**
     * 序列名
     */
    String value() default "";

    /**
     * 数据库类型，未配置默认使用注入 IKeyGenerator 实现，多个实现必须指定
     */
    DbType dbType() default DbType.OTHER;
}
