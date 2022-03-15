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

package top.summerboot.orm.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xieshuang
 * @date 2021-02-22 13:51
 */
public interface BaseDao {

    void ddl(@Param("sql") String sql);

    int insert(String tableName, Map<String, Object> map);

    int insert(Object entity);

    int insert(String tableName, Object entity);

    int update(String tableName, Wrapper updateWrapper);

    int delete(String tableName, Wrapper wrapper);

    int insertBySql(String sql);

    int updateBySql(String sql);

    int updateBySql(String sql, Wrapper queryWrapper);

    int deleteBySql(String sql);

    long selectCount(String tableName, Wrapper queryWrapper);

    long selectCountBySql(String sql, Wrapper queryWrapper);

    JSONObject selectOne(String tableName, Wrapper queryWrapper);

    JSONObject selectOneBySql(String sql, Wrapper queryWrapper);

    List<JSONObject> selectList(String tableName, Wrapper queryWrapper);

    List<JSONObject> selectListBySql(String sql, Wrapper queryWrapper);

    IPage<JSONObject> selectPage(IPage page, String sql, Wrapper queryWrapper);

    IPage<JSONObject> selectPageBySql(IPage page, String sql, Wrapper queryWrapper);

    Object getObjectBySql(String sql, Wrapper queryWrapper);
}
