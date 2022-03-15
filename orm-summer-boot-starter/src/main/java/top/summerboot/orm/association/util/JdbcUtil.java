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
