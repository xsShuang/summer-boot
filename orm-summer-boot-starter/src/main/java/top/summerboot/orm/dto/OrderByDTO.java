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
