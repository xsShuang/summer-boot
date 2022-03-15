package top.summerboot.orm.association.annotation;

import java.lang.annotation.*;

import static top.summerboot.orm.association.annotation.SetValue.SetType.NO_NULL;

/**
 * @author xieshuang
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SetValue {

    String field() default "";

    SetType setType() default NO_NULL;

    enum SetType {
        NULL,
        NO_NULL,
        NO_EMPTY
    }
}
