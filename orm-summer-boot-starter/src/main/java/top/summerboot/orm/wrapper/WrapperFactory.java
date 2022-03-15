package top.summerboot.orm.wrapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.summerboot.orm.association.util.ReflexUtil;
import top.summerboot.orm.dto.CommonQueryInterface;
import top.summerboot.orm.dto.OrderByDTO;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xieshuang
 * @date 2019-07-14 20:30
 */
public class WrapperFactory<T> {

    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
    private static final String EMPTY = "";
    private static final String SPOT = ".";
    private final Logger log = LoggerFactory.getLogger(WrapperFactory.class);

    /**
     * 根据 dto 生成 QueryWrapper
     *
     * @param dto
     * @return
     */
    public QueryWrapper<T> create(Object dto) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        create(wrapper, dto, null);
        return wrapper;
    }

    /**
     * 根据 dto 生成 QueryWrapper，并指定表名
     *
     * @param dto
     * @param tableName
     * @return
     */
    public QueryWrapper<T> create(Object dto, String tableName) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        create(wrapper, dto, tableName);
        return wrapper;
    }

    public QueryWrapper<T> create(Object dto, Class<?> vClass) {
        TableName annotation = ReflexUtil.getAnnotation(vClass, TableName.class, Model.class);
        String tableName;
        if (annotation == null) {
            tableName = StrUtil.toUnderlineCase(vClass.getSimpleName());
        } else {
            tableName = annotation.value();
        }
        return create(dto, tableName);
    }

    public void commonQuery(AbstractWrapper queryWrapper, CommonQueryInterface commonQuery, String tableName) {
        if (StrUtil.isNotBlank(tableName)) {
            tableName = tableName + ".";
        } else {
            tableName = "";
        }
        if (commonQuery.getEq() != null) {
            for (Map.Entry<String, Object> stringObjectEntry : commonQuery.getEq().entrySet()) {
                queryWrapper.eq(getColumn(tableName, stringObjectEntry.getKey()), stringObjectEntry.getValue());
            }
        }
        if (commonQuery.getNe() != null) {
            for (Map.Entry<String, Object> stringObjectEntry : commonQuery.getNe().entrySet()) {
                queryWrapper.ne(getColumn(tableName, stringObjectEntry.getKey()), stringObjectEntry.getValue());
            }
        }
        if (commonQuery.getLike() != null) {
            for (Map.Entry<String, Object> stringObjectEntry : commonQuery.getLike().entrySet()) {
                queryWrapper.like(getColumn(tableName, stringObjectEntry.getKey()), stringObjectEntry.getValue());
            }
        }
        if (commonQuery.getLe() != null) {
            for (Map.Entry<String, Object> stringObjectEntry : commonQuery.getLe().entrySet()) {
                if (stringObjectEntry.getKey().endsWith("Date") || stringObjectEntry.getKey().endsWith("Time")) {
                    if (stringObjectEntry.getValue() instanceof String) {
                        try {
                            DateTime parse = DateUtil.parse((String) stringObjectEntry.getValue());
                            stringObjectEntry.setValue(parse);
                        } catch (Exception ignored) {

                        }
                    }
                    // 1000000000000L 转成成时间是 2001-09-09 09:46:40
                    // 为了做到自动转换毫米和秒，任何超过这个数值的将被认为是毫秒
                    // 小于这个数值的被认为是秒
                    // 如果有功能会涉及到的时间范围查询是小于2001-09-09 09:46:40
                    // 请采用秒
                    if (stringObjectEntry.getValue() instanceof Long) {
                        long time = (Long) stringObjectEntry.getValue();
                        if (time > 1000000000000L) {
                            stringObjectEntry.setValue(new Date(time));
                        } else if (time > 100000000L) {
                            stringObjectEntry.setValue(new Date(time * 1000));
                        }
                    }
                }
                queryWrapper.le(getColumn(tableName, stringObjectEntry.getKey()), stringObjectEntry.getValue());
            }
        }
        if (commonQuery.getGe() != null) {
            for (Map.Entry<String, Object> stringObjectEntry : commonQuery.getGe().entrySet()) {
                if (stringObjectEntry.getKey().endsWith("Date") || stringObjectEntry.getKey().endsWith("Time")) {
                    if (stringObjectEntry.getValue() instanceof String) {
                        try {
                            DateTime parse = DateUtil.parse((String) stringObjectEntry.getValue());
                            stringObjectEntry.setValue(parse);
                        } catch (Exception ignored) {

                        }
                    }
                    if (stringObjectEntry.getValue() instanceof Long) {
                        long time = (Long) stringObjectEntry.getValue();
                        if (time > 1000000000000L) {
                            stringObjectEntry.setValue(new Date(time));
                        } else if (time > 100000000L) {
                            stringObjectEntry.setValue(new Date(time * 1000));
                        }
                    }
                }
                queryWrapper.ge(getColumn(tableName, stringObjectEntry.getKey()), stringObjectEntry.getValue());
            }
        }
        if (commonQuery.getIn() != null) {
            for (Map.Entry<String, Object> stringObjectEntry : commonQuery.getIn().entrySet()) {
                queryWrapper.in(getColumn(tableName, stringObjectEntry.getKey()), stringObjectEntry.getValue());
            }
        }
        if (commonQuery.getIsNull() != null) {
            for (Map.Entry<String, Object> stringObjectEntry : commonQuery.getIn().entrySet()) {
                queryWrapper.isNull(getColumn(tableName, stringObjectEntry.getKey()));
            }
        }
        if (CollUtil.isNotEmpty(commonQuery.getOrderBy())) {
            commonQuery.getOrderBy().sort(Comparator.comparing(OrderByDTO::getSort));
            for (OrderByDTO orderByDTO : commonQuery.getOrderBy()) {
                if (orderByDTO.getOrderByType() == 0) {
                    queryWrapper.orderByAsc(getColumn(tableName, orderByDTO.getColumn()));
                } else {
                    queryWrapper.orderByDesc(getColumn(tableName, orderByDTO.getColumn()));
                }
            }
        }
    }

    /**
     * 获取查询的列字段
     *
     * @param tableName
     * @param inputColumn
     * @return
     */
    public String getColumn(String tableName, String inputColumn) {
        if (inputColumn.contains(".")) {
            String[] split = inputColumn.split("\\.");
            return StrUtil.toUnderlineCase(split[0]) + "." + StrUtil.toUnderlineCase(split[1]);
        } else {
            return tableName + StrUtil.toUnderlineCase(inputColumn);
        }
    }

    public void create(QueryWrapper<T> wrapper, Object dto, String tableName) {
        Class<?> dtoClass = dto.getClass();
        Field[] declaredFields = getAllFields(dtoClass);
        for (Field field : declaredFields) {
            //打开私有访问
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(dto);
            } catch (IllegalAccessException e) {
                log.error("通过反射获取属性值出错：" + e);
            }
            QueryCondition query = field.getAnnotation(QueryCondition.class);
            if (query != null) {
                if (!query.condition().equals(QueryCondition.Condition.DEFAULT) && !isEmpty(value)) {
                    switch (query.condition()) {
                        case EQ:
                            wrapper.eq(getColumnName(field, tableName), value);
                            break;
                        case IN:
                            if (value instanceof Collection) {
                                wrapper.in(getColumnName(field, tableName), (Collection<?>) value);
                            } else if (value.getClass().isArray()) {
                                wrapper.in(getColumnName(field, tableName), value);
                            }
                            break;
                        case SIN:
                            wrapper.in(getColumnName(field, tableName), Arrays.asList(((String) value).split(",")));
                            break;
                        case LIN:
                            wrapper.in(getColumnName(field, tableName), Arrays.stream(((String) value).split(",")).map(Long::valueOf).toArray());
                            break;
                        case IIN:
                            wrapper.in(getColumnName(field, tableName), Arrays.stream(((String) value).split(",")).map(Integer::valueOf).toArray());
                            break;
                        case LIKE:
                            wrapper.like(getColumnName(field, tableName), value);
                            break;
                        case GE:
                            wrapper.ge(getColumnName(field, tableName), value);
                            break;
                        case LE:
                            wrapper.le(getColumnName(field, tableName), value);
                            break;
                        case GT:
                            wrapper.gt(getColumnName(field, tableName), value);
                            break;
                        case LT:
                            wrapper.lt(getColumnName(field, tableName), value);
                            break;
                        case NE:
                            wrapper.ne(getColumnName(field, tableName), value);
                            break;
                        case RIGHT_LIKE:
                            wrapper.likeRight(getColumnName(field, tableName), value);
                            break;
                        case LEFT_LIKE:
                            wrapper.likeLeft(getColumnName(field, tableName), value);
                            break;
                        default:
                    }
                }
                if (!query.sort().equals(QueryCondition.Sort.DEFAULT)) {
                    if (query.sort().equals(QueryCondition.Sort.DESC)) {
                        wrapper.orderByDesc(getColumnName(field, tableName));
                    } else if (query.sort().equals(QueryCondition.Sort.ASC)) {
                        wrapper.orderByAsc(getColumnName(field, tableName));
                    } else if (value != null) {
                        if ("asc".equals(value) || "ASC".equals(value) || value.equals(0)) {
                            wrapper.orderByAsc(getColumnName(field, tableName));
                        } else {
                            wrapper.orderByDesc(getColumnName(field, tableName));
                        }
                    }
                }
            }
        }
        try {
            if (dto instanceof CommonQueryInterface) {
                commonQuery(wrapper, (CommonQueryInterface) dto, tableName);
            }
        } catch (Exception e) {
            log.error("commonQuery处理时出错：" + e);
        }
    }

    private String getColumnName(Field field, String tableName) {
        QueryCondition annotation = field.getAnnotation(QueryCondition.class);
        if (annotation.field().isEmpty()) {
            if (StrUtil.isNotBlank(tableName)) {
                return tableName + SPOT + humpToLine(field.getName());
            }
            return humpToLine(field.getName());
        }
        if (StrUtil.isNotBlank(tableName)) {
            if (annotation.field().contains(SPOT)) {
                return annotation.field();
            }
            return tableName + SPOT + annotation.field();
        }
        return annotation.field();
    }

    private String humpToLine(String str) {
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Field[] getAllFields(final Class<?> cls) {
        final List<Field> allFieldsList = getAllFieldsList(cls);
        return allFieldsList.toArray(new Field[0]);
    }

    private static List<Field> getAllFieldsList(final Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if ((obj instanceof Collection)) {
            return ((Collection<?>) obj).isEmpty();
        }
        if ((obj instanceof String)) {
            return EMPTY.equals(((String) obj).trim());
        }
        return false;
    }
}
