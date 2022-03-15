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

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xieshuang
 * @date 2021-04-24 11:55
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("通用查询DTO")
@Data
public class CommonQueryDTO extends TimeDTO implements CommonQueryInterface {

    private static final long serialVersionUID = 1519083810348925450L;
    @ApiModelProperty(value = "小于")
    private JSONObject le;
    @ApiModelProperty(value = "大于")
    private JSONObject ge;
    @ApiModelProperty(value = "等于")
    private JSONObject eq;
    @ApiModelProperty(value = "模糊")
    private JSONObject like;
    @ApiModelProperty(value = "不等于")
    private JSONObject ne;
    @ApiModelProperty(value = "in")
    private JSONObject in;
    @ApiModelProperty(value = "为空")
    private JSONObject isNull;
    @ApiModelProperty(value = "排序对象集合")
    private ArrayList<OrderByDTO> order;

    @Override
    public List<OrderByDTO> getOrderBy() {
        return order;
    }
}
