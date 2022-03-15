package top.summerboot.orm.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import top.summerboot.orm.mapper.IMapper;

import java.util.List;
import java.util.Map;

/**
 * @author xieshuang
 * @date 2021-02-22 13:52
 */
public abstract class AbstractBaseDaoImpl implements BaseDao {

    protected static final String CREATE_TIME = "createTime";

    protected static final String UPDATE_TIME = "updateTime";

    @Autowired
    protected IMapper iMapper;

    @Override
    public void ddl(String sql) {
        iMapper.ddl(sql);
    }

    @Override
    public int insert(String tableName, Map<String, Object> map) {
        SQL sql = new SQL();
        sql.INSERT_INTO(tableName);
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            boolean b = !CREATE_TIME.equalsIgnoreCase(stringObjectEntry.getKey()) && !UPDATE_TIME.equalsIgnoreCase(stringObjectEntry.getKey());
            if (b) {
                sql.VALUES(StrUtil.toUnderlineCase(stringObjectEntry.getKey()), "'" + stringObjectEntry.getValue() + "'");
            }
        }
        return iMapper.insertBySql(sql.toString());
    }

    @Override
    public int insert(Object entity) {
        return 0;
    }

    @Override
    public int insert(String tableName, Object entity) {
        return 0;
    }

    @Override
    public int update(String tableName, Wrapper updateWrapper) {
        return iMapper.update(tableName, updateWrapper);
    }

    @Override
    public int delete(String tableName, Wrapper wrapper) {
        return iMapper.delete(tableName, wrapper);
    }

    @Override
    public int insertBySql(String sql) {
        return iMapper.insertBySql(sql);
    }

    @Override
    public int updateBySql(String sql) {
        return iMapper.updateBySql(sql, null);
    }

    @Override
    public int updateBySql(String sql, Wrapper queryWrapper) {
        return iMapper.updateBySql(sql, queryWrapper);
    }

    @Override
    public int deleteBySql(String sql) {
        return iMapper.deleteBySql(sql);
    }

    @Override
    public long selectCount(String tableName, Wrapper queryWrapper) {
        return iMapper.selectCount(tableName, queryWrapper);
    }

    @Override
    public long selectCountBySql(String sql, Wrapper queryWrapper) {
        return iMapper.selectCountBySql(sql, queryWrapper);
    }

    @Override
    public JSONObject selectOne(String tableName, Wrapper queryWrapper) {
        return iMapper.selectOne(tableName, queryWrapper);
    }

    @Override
    public JSONObject selectOneBySql(String sql, Wrapper queryWrapper) {
        return iMapper.selectOneBySql(sql, queryWrapper);
    }

    @Override
    public List<JSONObject> selectList(String tableName, Wrapper queryWrapper) {
        return iMapper.selectList(tableName, queryWrapper);
    }

    @Override
    public List<JSONObject> selectListBySql(String sql, Wrapper queryWrapper) {
        return iMapper.selectListBySql(sql, queryWrapper);
    }

    @Override
    public IPage<JSONObject> selectPage(IPage page, String sql, Wrapper queryWrapper) {
        return iMapper.selectPage(page, sql, queryWrapper);
    }

    @Override
    public IPage<JSONObject> selectPageBySql(IPage page, String sql, Wrapper queryWrapper) {
        return iMapper.selectPageBySql(page, sql, queryWrapper);
    }

    @Override
    public Object getObjectBySql(String sql, Wrapper queryWrapper) {
        return iMapper.getObjectBySql(sql, queryWrapper);
    }
}
