package top.summerboot.orm.association.dto;

import lombok.Data;

/**
 * 关联对象
 *
 * @author xieshuang
 * @date 2020-11-13 17:10
 */
@Data
public class JoinEntity<E> {

    /**
     * 主信息id
     */
    private String mainId;

    /**
     * 关联对象
     */
    private E entity;

}
