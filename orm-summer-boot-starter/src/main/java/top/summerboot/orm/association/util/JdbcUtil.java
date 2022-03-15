package top.summerboot.orm.association.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.springframework.core.env.Environment;
import top.summerboot.orm.association.annotation.MDS;
import top.summerboot.orm.util.SpringBeanUtil;

import java.util.Objects;

/**
 * @author xieshuang
 * @date 2021-02-22 16:21
 */
public class JdbcUtil {

    public static DbType getDbType(MDS annotation) {
        if (annotation == null) {
            String property = SpringBeanUtil.getBean(Environment.class).getProperty("spring.datasource.url");
            if (StrUtil.isNotBlank(property)) {
                assert property != null;
                return JdbcUtils.getDbType(property);
            }
            return JdbcUtils.getDbType(Objects.requireNonNull(SpringBeanUtil.getBean(Environment.class).getProperty("spring.datasource.dynamic.datasource.master.url")));
        }
        return JdbcUtils.getDbType(Objects.requireNonNull(SpringBeanUtil.getBean(Environment.class).getProperty("spring.datasource.dynamic.datasource." + annotation.value() + ".url")));
    }
}
