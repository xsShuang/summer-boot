
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

package top.summerboot.orm.association;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import top.summerboot.orm.association.annotation.MDS;
import top.summerboot.orm.association.util.JoinUtil;
import top.summerboot.orm.association.util.QueryUtil;
import top.summerboot.orm.association.util.ReflexUtil;
import top.summerboot.orm.dto.PageDTO;
import top.summerboot.orm.service.BaseDao;
import top.summerboot.orm.util.SpringBeanUtil;
import top.summerboot.orm.wrapper.WrapperFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xieshuang
 * @date 2020-11-22 11:44
 */
public class AssociationQuery<E> {

    private Object query;

    public AssociationQuery(Class<E> eClass) {
        this.eClass = eClass;
    }

    private final Class<E> eClass;

    public IPage<E> voPage(PageDTO pageQuery) {
        return voPage(pageQuery, new WrapperFactory<>().create(pageQuery, eClass));
    }

    public IPage<E> voPage(PageDTO pageQuery, Object query) {
        return voPage(pageQuery, new WrapperFactory<>().create(query, eClass));
    }

    public IPage<E> voPage(PageDTO pageQuery, Object query, boolean association) {
        return voPage(pageQuery, new WrapperFactory<>().create(query, eClass), association);
    }

    public IPage<E> voPage(PageDTO pageQuery, boolean association) {
        return voPage(pageQuery, new WrapperFactory<>().create(pageQuery, eClass), association);
    }

    public IPage<E> voPage(PageDTO pageQuery, QueryWrapper queryWrapper, boolean association) {
        return voPage(pageQuery, queryWrapper, QueryUtil.generateSql(eClass, pageQuery), association);
    }

    public IPage<E> voPage(PageDTO pageQuery, QueryWrapper queryWrapper) {
        return voPage(pageQuery, queryWrapper, QueryUtil.generateSql(eClass, pageQuery), true);
    }

    public IPage<E> voPage(PageDTO pageQuery, QueryWrapper queryWrapper, String sql, boolean association) {
        this.query = pageQuery;
        BaseDao baseDao;
        MDS ds = eClass.getAnnotation(MDS.class);
        if (ds == null) {
            baseDao = SpringBeanUtil.getBean("defaultDaoImpl", BaseDao.class);
        } else {
            baseDao = SpringBeanUtil.getBean(ds.value() + "DaoImpl", BaseDao.class);
        }
        IPage<JSONObject> selectPage = baseDao.selectPageBySql(PageDTO.page(pageQuery), sql, queryWrapper);
        IPage<E> voPage = new Page<>();
        BeanUtils.copyProperties(selectPage, voPage);
        List<E> voList = selectPage.getRecords().stream().map(jsonObject -> JSON.toJavaObject(jsonObject, eClass)).collect(Collectors.toList());
        if (association) {
            JoinUtil joinUtil = new JoinUtil(voList, query);
            joinUtil.relationObjProcessing();
        }
        voPage.setRecords(voList);
        return voPage;
    }

    public List<E> voList(Object query) {
        return voList(new WrapperFactory<>().create(query, eClass), QueryUtil.generateSql(eClass, query), true);
    }

    public List<E> voList(Object query, boolean association) {
        return voList(new WrapperFactory<>().create(query, eClass), QueryUtil.generateSql(eClass, query), association);
    }

    public List<E> voList(QueryWrapper queryWrapper) {
        return voList(null, queryWrapper);
    }

    public List<E> voList(QueryWrapper queryWrapper, boolean association) {
        return voList(null, queryWrapper, association);
    }

    public List<E> voList(Object query, QueryWrapper queryWrapper) {
        this.query = query;
        return voList(queryWrapper, QueryUtil.generateSql(eClass, query), true);
    }

    public List<E> voList(Object query, QueryWrapper queryWrapper, boolean association) {
        this.query = query;
        return voList(queryWrapper, QueryUtil.generateSql(eClass, query), association);
    }

    public List<E> voList(Collection<?> ids) {
        String tableName;
        TableName annotation = ReflexUtil.getAnnotation(eClass, TableName.class, Model.class);
        if (annotation == null) {
            tableName = StrUtil.toUnderlineCase(eClass.getSimpleName());
        } else {
            tableName = annotation.value();
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in(tableName + ".id", ids);
        return voList(queryWrapper);
    }

    public List<E> voList(QueryWrapper queryWrapper, String sql, boolean association) {
        BaseDao baseDao;
        MDS ds = eClass.getAnnotation(MDS.class);
        if (ds == null) {
            baseDao = SpringBeanUtil.getBean("defaultDaoImpl", BaseDao.class);
        } else {
            baseDao = SpringBeanUtil.getBean(ds.value() + "DaoImpl", BaseDao.class);
        }
        List<JSONObject> objectList = baseDao.selectListBySql(sql, queryWrapper);
        List<E> voList = objectList.stream().map(jsonObject -> JSON.toJavaObject(jsonObject, eClass)).collect(Collectors.toList());
        if (association) {
            JoinUtil joinUtil = new JoinUtil(voList, query);
            joinUtil.relationObjProcessing();
        }
        return voList;
    }

    public E getVo(QueryWrapper queryWrapper, String sql, boolean association) {
        List<E> list = voList(queryWrapper, sql, association);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public E getVo(QueryWrapper queryWrapper) {
        return getVo(queryWrapper, QueryUtil.generateSql(eClass, null), true);
    }

    public E getVo(QueryWrapper queryWrapper, boolean association) {
        return getVo(queryWrapper, QueryUtil.generateSql(eClass, null), association);
    }

    public E getVo(QueryWrapper queryWrapper, Object query) {
        return getVo(queryWrapper, QueryUtil.generateSql(eClass, query), true);
    }

    public E getVo(QueryWrapper queryWrapper, Object query, boolean association) {
        return getVo(queryWrapper, QueryUtil.generateSql(eClass, query), association);
    }

    public E getVo(Object id, Object query) {
        return getVo(id, query, true);
    }

    public E getVo(Object id, Object query, boolean association) {
        this.query = query;
        String tableName;
        TableName annotation = ReflexUtil.getAnnotation(eClass, TableName.class, Model.class);
        if (annotation == null) {
            tableName = StrUtil.toUnderlineCase(eClass.getSimpleName());
        } else {
            tableName = annotation.value();
        }
        return getVo(id, tableName + ".id", query, association);
    }

    public E getVo(Object id) {
        String tableName;
        TableName annotation = ReflexUtil.getAnnotation(eClass, TableName.class, Model.class);
        if (annotation == null) {
            tableName = StrUtil.toUnderlineCase(eClass.getSimpleName());
        } else {
            tableName = annotation.value();
        }
        return getVo(id, tableName + ".id");
    }

    public E getVo(Object id, boolean association) {
        String tableName;
        TableName annotation = ReflexUtil.getAnnotation(eClass, TableName.class, Model.class);
        if (annotation == null) {
            tableName = StrUtil.toUnderlineCase(eClass.getSimpleName());
        } else {
            tableName = annotation.value();
        }
        return getVo(id, tableName + ".id", association);
    }

    public E getVo(Object id, String idName) {
        return getVo(id, idName, null);
    }

    public E getVo(Object id, String idName, boolean association) {
        return getVo(id, idName, null, association);
    }

    public E getVo(Object id, String idName, Object query) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(idName, id);
        return getVo(queryWrapper, QueryUtil.generateSql(eClass, query), true);
    }

    public E getVo(Object id, String idName, Object query, boolean association) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(idName, id);
        return getVo(queryWrapper, QueryUtil.generateSql(eClass, query), association);
    }


    public long count(Object query) {
        return count(new WrapperFactory<>().create(query, eClass), QueryUtil.generateSql(eClass, query));
    }

    public long count(QueryWrapper queryWrapper) {
        return count(null, queryWrapper);
    }

    public long count(Object query, QueryWrapper queryWrapper) {
        this.query = query;
        return count(queryWrapper, QueryUtil.generateSql(eClass, query));
    }

    public long count(QueryWrapper queryWrapper, String sql) {
        BaseDao baseDao;
        MDS ds = eClass.getAnnotation(MDS.class);
        if (ds == null) {
            baseDao = SpringBeanUtil.getBean("defaultDaoImpl", BaseDao.class);
        } else {
            baseDao = SpringBeanUtil.getBean(ds.value() + "DaoImpl", BaseDao.class);
        }
        sql = "select count(*)  from " + StrUtil.subAfter(sql, " from ", false);
        return baseDao.selectCountBySql(sql, queryWrapper);
    }
}
