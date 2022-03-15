package top.summerboot.orm.association.annotation;

import java.lang.annotation.*;

/**
 * @author xieshuang
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinExpression {

    /**
     * 表达式值
     */
    String value();

    /**
     * 是否一直参与join
     */
    boolean allJoin() default false;

}
