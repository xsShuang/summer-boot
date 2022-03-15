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
