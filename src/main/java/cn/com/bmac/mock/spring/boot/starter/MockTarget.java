package cn.com.bmac.mock.spring.boot.starter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author CHAN
 * @since 2019-01-16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockTarget {

    /**
     * value()对应请求URI
     * @return
     */
    String value() default "";
}
