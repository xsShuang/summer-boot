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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author xieshuang
 * @date 2020-11-18 11:29
 */
public class ReflexUtil {

    /**
     * @param vClass          传入的类
     * @param annotationClass 要获取的注解
     * @param exclude         排除的父类
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Class<?> vClass, Class<A> annotationClass, Class<?> exclude) {
        A annotation = vClass.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        } else {
            Class<?> currentClass = vClass.getSuperclass();
            if (currentClass == null) {
                return null;
            }
            if (currentClass.equals(Object.class)) {
                return null;
            }
            while (currentClass != null) {
                if (currentClass.equals(exclude)) {
                    return null;
                } else {
                    annotation = currentClass.getAnnotation(annotationClass);
                    if (annotation != null) {
                        return annotation;
                    } else {
                        currentClass = currentClass.getSuperclass();
                    }
                }
            }
            return null;
        }
    }

    public static Class<?>[] getClassArray(Object value, String[] params, Map<String, Field> dFieldMap) {
        Class<?>[] classArray = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            classArray[i] = getClass(params[i], value, dFieldMap);
        }
        return classArray;
    }

    public static Object[] getValueArray(Object value, String[] params, Map<String, Field> dFieldMap, Object d) throws IllegalAccessException {
        Object[] valueArray = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            valueArray[i] = getObject(params[i], value, dFieldMap, d);
        }
        return valueArray;
    }

    public static Class<?> getClass(String param, Object value, Map<String, Field> dFieldMap) {
        if ("$$".equals(param)) {
            return value.getClass();
        } else if (param.startsWith("$")) {
            Field field = dFieldMap.get(StrUtil.removePrefix(param, "$"));
            return field.getClass();
        } else {
            String[] split = param.split("#");
            return getClassByStr(split[0]);
        }
    }

    public static Object getObject(String param, Object value, Map<String, Field> dFieldMap, Object d) throws IllegalAccessException {
        if ("$$".equals(param)) {
            return value;
        } else if (param.startsWith("$")) {
            Field field = dFieldMap.get(StrUtil.removePrefix(param, "$"));
            return field.get(d);
        } else {
            String[] split = param.split("#");
            return getObjectByStr(split[0], split[1]);
        }
    }

    public static <V> V create(Class<V> vClass) {
        try {
            return vClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Field> getAllFieldMap(Class<?> cls) {
        Field[] vFields = getAllFields(cls, null);
        Map<String, Field> fieldMap = new LinkedHashMap<>(vFields.length);
        for (Field field : vFields) {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }

    public static Map<String, Field> getAllFieldMap(Class<?> cls, Class<?> exclude) {
        Field[] vFields = getAllFields(cls, exclude);
        Map<String, Field> fieldMap = new LinkedHashMap<>(vFields.length);
        for (Field field : vFields) {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }

    public static Field[] getAllFields(final Class<?> cls, Class<?> exclude) {
        final List<Field> allFieldsList = getAllFieldsList(cls, exclude);
        return allFieldsList.toArray(new Field[0]);
    }

    public static List<Field> getAllAccessibleFieldList(final Class<?> cls, Class<?> exclude) {
        List<Field> allFieldsList = getAllFieldsList(cls, exclude);
        if (CollUtil.isNotEmpty(allFieldsList)) {
            for (Field field : allFieldsList) {
                field.setAccessible(true);
            }
        }
        return allFieldsList;
    }

    public static List<Field> getAllFieldsList(final Class<?> cls, Class<?> exclude) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));
            currentClass = currentClass.getSuperclass();
            if (currentClass != null && currentClass.equals(exclude)) {
                break;
            }
        }
        return allFields;
    }

    public static Class<?> getClassByStr(String typeName) {
        switch (typeName) {
            case "date":
                return Date.class;
            case "BigDecimal":
                return BigDecimal.class;
            case "String":
                return String.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "double":
                return double.class;
            case "boolean":
                return boolean.class;
            case "float":
                return float.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "char":
                return char.class;
            case "Integer":
                return Integer.class;
            case "Double":
                return Double.class;
            case "Float":
                return Float.class;
            case "Boolean":
                return Boolean.class;
            case "Byte":
                return Byte.class;
            case "Short":
                return Short.class;
            case "Long":
                return Long.class;
            case "Character":
                return Character.class;
            default:
                throw new IllegalArgumentException("找不到该类型的class");
        }
    }

    public static Object getObjectByStr(String typeName, String value) {
        switch (typeName) {
            case "date":
                return DateUtil.parse(value);
            case "BigDecimal":
                return new BigDecimal(value);
            case "String":
                return value;
            case "int":
                return Integer.parseInt(value);
            case "long":
                return Long.parseLong(value);
            case "double":
                return Double.parseDouble(value);
            case "boolean":
                return Boolean.parseBoolean(value);
            case "float":
                return Float.parseFloat(value);
            case "byte":
                return Byte.parseByte(value);
            case "short":
                return Short.parseShort(value);
            case "char":
                return value.charAt(0);
            case "Integer":
                return Integer.valueOf(value);
            case "Double":
                return Double.valueOf(value);
            case "Float":
                return Float.valueOf(value);
            case "Boolean":
                return Boolean.valueOf(value);
            case "Byte":
                return Byte.valueOf(value);
            case "Short":
                return Short.valueOf(value);
            case "Long":
                return Long.valueOf(value);
            default:
                throw new IllegalArgumentException("找不到该类型的class");
        }
    }
}
