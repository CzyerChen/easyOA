package easyoa.leavemanager.annotation;

import easyoa.common.constant.LimitTypeEnum;

import java.lang.annotation.*;

/**
 * Created by claire on 2019-06-21 - 11:00
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limit {

    // 资源名称，用于描述接口功能
    String name() default "";

    // 资源 key
    String key() default "";

    // key prefix
    String prefix() default "";

    // 时间的，单位秒
    int period();

    // 限制访问次数
    int count();

    // 限制类型
    LimitTypeEnum limitType() default LimitTypeEnum.CUSTOMER;
}
