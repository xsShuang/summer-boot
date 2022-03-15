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

package top.summerboot.orm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xieshuang
 */
@ApiModel("排序对象")
@Data
public class OrderByDTO implements Serializable {

    private static final long serialVersionUID = -4642609161328593532L;
    @ApiModelProperty(value = "排序字段优先级顺序")
    private int sort;
    @ApiModelProperty(value = "排序字段")
    private String column;
    @ApiModelProperty(value = "排序类型，0顺序，其他倒序")
    private int orderByType;
}
