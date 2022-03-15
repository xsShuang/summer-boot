package top.summerboot.orm.association.annotation;

import java.lang.annotation.*;

/**
 * @author xieshuang
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MDS {

    /**
     * 数据源名称
     */
    String value();

}
