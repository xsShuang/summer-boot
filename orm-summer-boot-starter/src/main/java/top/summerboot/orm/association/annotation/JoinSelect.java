package top.summerboot.orm.association.annotation;

import java.lang.annotation.*;

/**
 * 关联查询注解
 *
 * @author xieshuang
 * @date 2022-01-13 10:04
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinSelect {

    /**
     * 自动构建的sql语句末尾添加的sql
     */
    String and() default "";

    /**
     * 关联信息表名
     */
    String relationName();

    /**
     * 中间表名
     */
    String middleTable() default "";

    /**
     * 表的主信息id，主信息对象的id字段，java对象字段
     */
    String mainId() default "id";

    /**
     * 表的附属信息id，数据库字段
     */
    String relationId() default "id";

    /**
     * 中间表的主信息id，数据库字段
     */
    String middleMainId() default "id";

    /**
     * 中间表的附属信息id，数据库字段
     */
    String middleRelationId() default "id";

    /**
     * 单个时的数据库字段
     */
    String field() default "";

    /**
     * 直接用sql查询
     */
    String sql() default "";

}
