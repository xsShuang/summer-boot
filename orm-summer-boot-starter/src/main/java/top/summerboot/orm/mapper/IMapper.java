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

package top.summerboot.orm.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import static com.baomidou.mybatisplus.core.toolkit.Constants.WRAPPER;

/**
 * @author xieshuang
 * @since 2020/4/15 18:40
 */
@Mapper
public interface IMapper {

    void ddl(@Param("sql") String sql);

    int insert(@Param("tableName") String tableName, @Param("map") Map<String, Object> map);

    int update(@Param("tableName") String tableName, @Param(WRAPPER) Wrapper updateWrapper);

    int delete(@Param("tableName") String tableName, @Param(WRAPPER) Wrapper wrapper);

    int insertBySql(@Param("sql") String sql);

    int updateBySql(@Param("sql") String sql, @Param(WRAPPER) Wrapper queryWrapper);

    int deleteBySql(@Param("sql") String sql);

    JSONObject selectOne(@Param("tableName") String tableName, @Param(WRAPPER) Wrapper queryWrapper);

    List<JSONObject> selectList(@Param("tableName") String tableName, @Param(WRAPPER) Wrapper queryWrapper);

    Long selectCount(@Param("tableName") String tableName, @Param(WRAPPER) Wrapper queryWrapper);

    IPage<JSONObject> selectPage(@Param("page") IPage page, @Param("tableName") String tableName, @Param(WRAPPER) Wrapper queryWrapper);

    JSONObject selectOneBySql(@Param("sql") String sql, @Param(WRAPPER) Wrapper queryWrapper);

    List<JSONObject> selectListBySql(@Param("sql") String sql, @Param(WRAPPER) Wrapper queryWrapper);

    Long selectCountBySql(@Param("sql") String sql, @Param(WRAPPER) Wrapper queryWrapper);

    IPage<JSONObject> selectPageBySql(@Param("page") IPage page, @Param("sql") String sql, @Param(WRAPPER) Wrapper queryWrapper);

    Object getObjectBySql(@Param("sql") String sql, @Param(WRAPPER) Wrapper queryWrapper);
}
