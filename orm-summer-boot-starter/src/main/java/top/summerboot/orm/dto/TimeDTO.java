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
import lombok.EqualsAndHashCode;
import top.summerboot.orm.wrapper.QueryCondition;

import java.util.Date;

/**
 * @author 谢霜
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("时间查询DTO")
@Data
public class TimeDTO extends PageDTO {

    private static final long serialVersionUID = 1589099864592572353L;

    @ApiModelProperty(value = "开始时间")
    @QueryCondition(condition = QueryCondition.Condition.GE, field = "create_time")
    private Date beginTime;

    @ApiModelProperty(value = "结束时间")
    @QueryCondition(condition = QueryCondition.Condition.LE, field = "create_time")
    private Date endTime;

    @ApiModelProperty(value = "时间排序，1倒叙，0正序")
    @QueryCondition(condition = QueryCondition.Condition.DEFAULT, field = "create_time", sort = QueryCondition.Sort.AUTO)
    private Integer timeSort;

    @ApiModelProperty(value = "id排序，1倒叙，0正序")
    @QueryCondition(condition = QueryCondition.Condition.DEFAULT, field = "id", sort = QueryCondition.Sort.AUTO)
    private Integer idSort;
}
