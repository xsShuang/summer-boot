package top.summerboot.orm.association.annotation;

import java.lang.annotation.*;

/**
 * @author xieshuang
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Select {

    /**
     * VO查询注解，定义查询的字段
     */
    String value();

    /**
     * 定义该字段是否需要进行查询，默认所有字段都会进行查询，不参与查询的需要添加此注解，并设置为false来排除
     */
    boolean exist() default true;

}
