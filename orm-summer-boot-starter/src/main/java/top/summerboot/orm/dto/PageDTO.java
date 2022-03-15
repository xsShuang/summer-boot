package top.summerboot.orm.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xieshuang
 * @date 2019-04-24 17:55
 */
@EqualsAndHashCode
@Data
@Accessors(chain = true)
@ApiModel("分页DTO")
public class PageDTO implements Serializable {

    private static final long serialVersionUID = 634211779445429425L;
    @ApiModelProperty(value = "页码", required = true, example = "1")
    private Integer page;
    @ApiModelProperty(value = "条数", required = true, example = "10")
    private Integer size;

    public Page page() {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        return new Page(this.getPage(), this.getSize());
    }

    public static <T> Page<T> page(PageDTO dto) {
        if (dto == null) {
            return new Page<>(1, 10);
        } else {
            if (dto.getPage() == null) {
                dto.setPage(1);
            }
            if (dto.getSize() == null) {
                dto.setSize(10);
            }
            return new Page<>(dto.getPage(), dto.getSize());
        }
    }
}
