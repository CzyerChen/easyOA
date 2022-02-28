package easyoa.leavemanager.annotation;

import java.lang.annotation.*;

/**
 * Created by claire on 2019-06-21 - 10:58
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Log {
    public String value() default "";
}
