/*
 *
 * Copyright 2022 xieshuang(https://github.com/xsShuang/summer-boot)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.summerboot.orm.wrapper;

import java.lang.annotation.*;

/**
 * @author xieshuang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryCondition {

    /**
     * 查询条件枚举类
     * DEFAULT，不进行查询
     */
    enum Condition {
        DEFAULT, EQ, IN, LIKE, GE, LE, SIN, LIN, IIN, GT, LT, NE, LEFT_LIKE, RIGHT_LIKE
    }

    /**
     * Sort为排序字段，
     * DEFAULT,不进行排序
     * DESC，倒叙
     * ASC，顺序
     * AUTO，根据值进行排序，当值为数值型时，0代表顺序，其余都为倒叙
     * 当值为string类型时，asc和ASC为正序，其他都为逆序
     * 其余情况均为逆序
     */
    enum Sort {
        DEFAULT, DESC, ASC, AUTO
    }

    /**
     * 查询条件
     *
     * @return
     */
    Condition condition() default Condition.EQ;

    /**
     * 数据库字段，默认为空，自动根据驼峰转下划线
     *
     * @return
     */
    String field() default "";

    /**
     * 排序说明
     *
     * @return
     */
    Sort sort() default Sort.DEFAULT;
}
