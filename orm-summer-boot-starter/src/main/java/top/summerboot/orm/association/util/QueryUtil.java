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

package top.summerboot.orm.association.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.extern.slf4j.Slf4j;
import top.summerboot.orm.association.annotation.JoinExpression;
import top.summerboot.orm.association.annotation.JoinSelect;
import top.summerboot.orm.association.annotation.Select;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xieshuang
 * @date 2020-11-18 15:36
 */
@Slf4j
public class QueryUtil {

    public static <V> String generateSql(Class<V> vClass, Object query) {
        String tableName;
        TableName annotation = ReflexUtil.getAnnotation(vClass, TableName.class, Model.class);
        if (annotation == null) {
            tableName = StrUtil.toUnderlineCase(vClass.getSimpleName());
        } else {
            tableName = annotation.value();
        }
        Map<String, Field> allFieldMap = ReflexUtil.getAllFieldMap(vClass, Model.class);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select DISTINCT ");
        boolean one = true;
        for (Map.Entry<String, Field> stringFieldEntry : allFieldMap.entrySet()) {
            JoinSelect joinSelect = stringFieldEntry.getValue().getAnnotation(JoinSelect.class);
            if (joinSelect != null) {
                continue;
            }
            Select select = stringFieldEntry.getValue().getAnnotation(Select.class);
            TableField tableField = stringFieldEntry.getValue().getAnnotation(TableField.class);
            if (select != null) {
                if (!select.exist()) {
                    continue;
                }
                String field = select.value();
                if (one) {
                    stringBuilder.append(field);
                    one = false;
                } else {
                    stringBuilder.append(" ,");
                    stringBuilder.append(field);
                }
            } else if (tableField != null) {
                if (!tableField.exist()) {
                    continue;
                }
                String field = tableField.value();
                if (!tableField.value().contains(".")) {
                    field = tableName + "." + field;
                }
                if (one) {
                    stringBuilder.append(field);
                    one = false;
                } else {
                    stringBuilder.append(" ,");
                    stringBuilder.append(field);
                }
            } else {
                if (one) {
                    one = false;
                } else {
                    stringBuilder.append(" ,");
                }
                stringBuilder.append(tableName);
                stringBuilder.append(".");
                stringBuilder.append(StrUtil.toUnderlineCase(stringFieldEntry.getKey()));
            }
        }
        stringBuilder.append(" from ");
        stringBuilder.append(tableName);
        stringBuilder.append("\n");
        setJoinExpression(stringBuilder, query);
        return stringBuilder.toString();
    }

    public static void setJoinExpression(StringBuilder stringBuilder, Object query) {
        if (query != null) {
            List<String> stringList = new ArrayList<>();
            Class<?> dtoClass = query.getClass();
            Field[] declaredFields = ReflexUtil.getAllFields(dtoClass, null);
            for (Field field : declaredFields) {
                //??????????????????
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(query);
                } catch (IllegalAccessException e) {
                    log.error("????????????????????????????????????" + e);
                }
                JoinExpression joinExpression = field.getAnnotation(JoinExpression.class);
                if (joinExpression != null && (value != null || joinExpression.allJoin())) {
                    if (!stringList.contains(joinExpression.value())) {
                        stringBuilder.append(joinExpression.value());
                        stringBuilder.append("\n");
                        stringList.add(joinExpression.value());
                    }
                }
            }
        }
    }
}
