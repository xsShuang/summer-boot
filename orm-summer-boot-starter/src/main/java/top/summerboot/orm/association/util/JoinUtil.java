package top.summerboot.orm.association.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.stringtemplate.v4.ST;
import top.summerboot.orm.association.annotation.JoinSelect;
import top.summerboot.orm.association.annotation.MDS;
import top.summerboot.orm.association.dto.JoinEntity;
import top.summerboot.orm.service.BaseDao;
import top.summerboot.orm.util.SpringBeanUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 关联处理工具类
 *
 * @author xieshuang
 * @date 2020-11-18 14:43
 */
@Slf4j
public class JoinUtil {

    private Object query;
    private List<?> objectList;

    public JoinUtil(List<?> objectList, Object query) {
        this.objectList = objectList;
        this.query = query;
    }

    public JoinUtil(List<?> objectList) {
        this.objectList = objectList;
    }

    /**
     * 关联对象处理方法
     * 批量查询关联对象并设置到集合中
     */
    public void relationObjProcessing() {
        if (CollUtil.isNotEmpty(objectList)) {
            Object object = objectList.get(0);
            Class<?> aClass = object.getClass();
            Map<String, Field> allFieldMap = ReflexUtil.getAllFieldMap(aClass);
            for (Map.Entry<String, Field> stringFieldEntry : allFieldMap.entrySet()) {
                JoinSelect joinSelect = stringFieldEntry.getValue().getAnnotation(JoinSelect.class);
                if (joinSelect != null) {
                    joinSelectProcessing(allFieldMap, joinSelect, stringFieldEntry);
                }
            }
        }
    }

    private void joinSelectProcessing(Map<String, Field> allFieldMap, JoinSelect annotation, Map.Entry<String, Field> stringFieldEntry) {
        try {
            // 获取id字段
            Field field = allFieldMap.get(annotation.mainId());
            // 创建in查询sql
            String inIdSql = createInIdSql(field);
            // 创建and sql
            String andSql = createAndSql(annotation.and());
            if (StrUtil.isNotBlank(inIdSql)) {
                String selectSql = createSelectSql(annotation, inIdSql, andSql);
                log.debug("sql：" + selectSql);
                // 当前字段
                Field curFieldType = stringFieldEntry.getValue();
                BaseDao dao = getDao(stringFieldEntry.getValue());
                List<JSONObject> jsonObjectList = getJoinList(stringFieldEntry, selectSql, dao);
                // 集合List元素
                if (List.class.equals(curFieldType.getType())) {
                    // 当前集合的泛型类型
                    Type genericType = curFieldType.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) genericType;
                        // 得到泛型里的class类型对象
                        Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                        setListRelationObj(annotation, curFieldType, allFieldMap, objectList, actualTypeArgument, jsonObjectList);
                    }
                } else {
                    // 设置单个对象
                    setRelationObj(annotation, curFieldType, allFieldMap, objectList, curFieldType.getType(), jsonObjectList);
                }
            }
        } catch (Exception e) {
            log.error("处理关联信息时出错：", e);
        }
    }

    private List<JSONObject> getJoinList(Map.Entry<String, Field> stringFieldEntry, String selectSql, BaseDao baseDao) {
        List<JSONObject> jsonObjectList = baseDao.selectListBySql(selectSql, null);
        return jsonObjectList;
    }

    /**
     * 获取数据库查询 dao
     *
     * @param field
     * @return
     */
    public BaseDao getDao(Field field) {
        BaseDao baseDao;
        // 首先获取字段上的注解
        MDS ds = field.getAnnotation(MDS.class);
        if (ds == null) {
            // 字段上没有获取类上的注解
            ds = field.getType().getAnnotation(MDS.class);
            if (ds != null) {
                baseDao = SpringBeanUtil.getBean(ds.value() + "DaoImpl", BaseDao.class);
            } else {
                baseDao = SpringBeanUtil.getBean("defaultDaoImpl", BaseDao.class);
            }
        } else {
            baseDao = SpringBeanUtil.getBean(ds.value() + "DaoImpl", BaseDao.class);
        }
        return baseDao;
    }

    /**
     * 创建 查询sql语句
     *
     * @return
     */
    public String createSelectSql(JoinSelect annotation, String inIdSql, String andSql) {
        String selectSql;
        if (StrUtil.isNotBlank(annotation.sql())) {
            selectSql = StrUtil.format(annotation.sql(), inIdSql) + " " + andSql;
        } else {
            if (StrUtil.isNotBlank(annotation.middleTable())) {
                selectSql = StrUtil.format("SELECT\n" +
                                "\tmidd.{} AS id_alias,\n" +
                                "\trelation.* \n" +
                                "FROM\n" +
                                "\t{} midd\n" +
                                "\tJOIN {} relation ON midd.{} = relation.{}\n" +
                                "\tand midd.{} in({}) " + andSql
                        , annotation.middleMainId()
                        , annotation.middleTable()
                        , annotation.relationName()
                        , annotation.middleRelationId()
                        , annotation.relationId()
                        , annotation.middleMainId()
                        , inIdSql);
            } else {
                selectSql = StrUtil.format("select {} as id_alias, {}.* from {} where {} in({}) " + andSql
                        , annotation.relationId()
                        , annotation.relationName()
                        , annotation.relationName()
                        , annotation.relationId()
                        , inIdSql);
            }
        }
        return selectSql;
    }

    /**
     * 创建 and sql，用于需要添加一些特殊的条件或者sql语句到查询语句后面
     *
     * @param and
     * @return
     */
    public String createAndSql(String and) {
        String andSql = "";
        if (StrUtil.isNotBlank(and)) {
            try {
                ST st = new ST(and);
                st.add("query", query);
                andSql = st.render();
            } catch (Exception e) {
                log.error("字符串模板替换时出错：", e);
            }
        }
        return andSql;
    }

    /**
     * 创建用于 in 查询的 id sql
     *
     * @param field
     * @return
     * @throws IllegalAccessException
     */
    public String createInIdSql(Field field) throws IllegalAccessException {
        StringBuilder idSql = new StringBuilder();
        boolean one = true;
        for (Object o : objectList) {
            Object o1 = field.get(o);
            if (o1 != null) {
                if (one) {
                    one = false;
                } else {
                    // 如果不是第一个id，要在前面加个“,”
                    idSql.append(",");
                }
                // 如果是字符串，需要加“''”
                if (field.getType().isAssignableFrom(String.class)) {
                    idSql.append("'").append(o1).append("'");
                } else {
                    idSql.append(o1);
                }
            }
        }
        return idSql.toString();
    }

    /**
     * 设置关联对象
     *
     * @param annotation     关联查询注解
     * @param field          当前字段
     * @param dFieldMap      原始数据对象 字段Map
     * @param list           原始数据集合
     * @param eClass         需要转换成这个对象
     * @param jsonObjectList 查询出的数据集合
     * @param <E>
     * @throws IllegalAccessException
     */
    public <E> void setRelationObj(JoinSelect annotation, Field field, Map<String, Field> dFieldMap, List<?> list, Class<E> eClass, List<JSONObject> jsonObjectList) throws IllegalAccessException {
        if (eClass.isPrimitive() || eClass.equals(String.class)) {
            Map<String, List<Object>> primitiveOrSrtMap = getPrimitiveOrSrtMap(jsonObjectList, annotation.field());
            for (Object object : list) {
                Field id = dFieldMap.get(annotation.mainId());
                Object idValue = id.get(object);
                field.set(object, primitiveOrSrtMap.get(idValue.toString()).get(0));
            }
        } else {
            Map<String, List<JoinEntity<E>>> map = joinEntityListToMap(eClass, jsonObjectList);
            for (Object object : list) {
                Field id = dFieldMap.get(annotation.mainId());
                Object idValue = id.get(object);
                if (idValue != null) {
                    List<E> es = joinEntityListToEntityList(map.get(idValue.toString()));
                    if (CollUtil.isNotEmpty(es)) {
                        field.set(object, es.get(0));
                    }
                }
            }
        }
    }

    /**
     * 设置集合关联对象
     *
     * @param annotation     关联查询注解
     * @param field          当前字段
     * @param dFieldMap      原始数据对象 字段Map
     * @param list           原始数据集合
     * @param eClass         需要转换成这个对象
     * @param jsonObjectList 查询出的数据集合
     * @param <E>
     * @throws IllegalAccessException
     */
    public <E> void setListRelationObj(JoinSelect annotation, Field field, Map<String, Field> dFieldMap, List<?> list, Class<E> eClass, List<JSONObject> jsonObjectList) throws IllegalAccessException {
        if (eClass.isPrimitive() || eClass.equals(String.class)) {
            Map<String, List<Object>> primitiveOrSrtMap = getPrimitiveOrSrtMap(jsonObjectList, annotation.field());
            for (Object object : list) {
                Field id = dFieldMap.get(annotation.mainId());
                Object idValue = id.get(object);
                field.set(object, primitiveOrSrtMap.get(idValue.toString()));
            }
        } else {
            Map<String, List<JoinEntity<E>>> map = joinEntityListToMap(eClass, jsonObjectList);
            for (Object object : list) {
                Field id = dFieldMap.get(annotation.mainId());
                Object idValue = id.get(object);
                field.set(object, joinEntityListToEntityList(map.get(idValue.toString())));
            }
        }
    }

    /**
     * 将关联实体集合转为实体集合
     *
     * @param joinEntityList
     * @param <E>
     * @return
     */
    public <E> List<E> joinEntityListToEntityList(List<JoinEntity<E>> joinEntityList) {
        if (CollUtil.isEmpty(joinEntityList)) {
            return Collections.emptyList();
        }
        return joinEntityList.stream().map(JoinEntity::getEntity).collect(Collectors.toList());
    }

    /**
     * 将关联实体根据主信息id转化成map
     *
     * @param eClass
     * @param jsonObjectList
     * @param <E>
     * @return
     */
    public <E> Map<String, List<JoinEntity<E>>> joinEntityListToMap(Class<E> eClass, List<JSONObject> jsonObjectList) {
        if (CollUtil.isEmpty(jsonObjectList)) {
            return Collections.emptyMap();
        }
        List<JoinEntity<E>> joinEntityList = jsonObjectList.stream().map(jsonObject -> {
            JoinEntity<E> entity = new JoinEntity<>();
            entity.setMainId(jsonObject.get("id_alias") != null ? jsonObject.getString("id_alias") : jsonObject.getString("ID_ALIAS"));
            entity.setEntity(jsonObject.toJavaObject(eClass));
            return entity;
        }).collect(Collectors.toList());
        return joinEntityList.stream().collect(Collectors.groupingBy(JoinEntity::getMainId));
    }

    /**
     * 将关联实体根据主信息id转化成map
     *
     * @return
     */
    public Map<String, List<Object>> getPrimitiveOrSrtMap(List<JSONObject> jsonObjectList, String name) {
        if (CollUtil.isEmpty(jsonObjectList)) {
            return Collections.emptyMap();
        }
        List<JoinEntity<Object>> joinEntityList = jsonObjectList.stream().map(jsonObject -> {
            JoinEntity<Object> entity = new JoinEntity<>();
            entity.setMainId(jsonObject.get("id_alias") != null ? jsonObject.getString("id_alias") : jsonObject.getString("ID_ALIAS"));
            entity.setEntity(jsonObject.get(name) != null ? jsonObject.get(name) : jsonObject.get(name.toUpperCase()));
            return entity;
        }).collect(Collectors.toList());
        joinEntityList.removeIf(objectJoinEntity -> objectJoinEntity.getEntity() == null);
        if (CollUtil.isEmpty(joinEntityList)) {
            return Collections.emptyMap();
        }
        Map<String, List<JoinEntity<Object>>> collect = joinEntityList.stream().collect(Collectors.groupingBy(JoinEntity::getMainId));
        Map<String, List<Object>> listMap = new HashMap<>(collect.size());
        for (Map.Entry<String, List<JoinEntity<Object>>> stringListEntry : collect.entrySet()) {
            if (CollUtil.isNotEmpty(stringListEntry.getValue())) {
                List<Object> objects = stringListEntry.getValue().stream().map(JoinEntity::getEntity).collect(Collectors.toList());
                listMap.put(stringListEntry.getKey(), objects);
            }
        }
        return listMap;
    }
}
