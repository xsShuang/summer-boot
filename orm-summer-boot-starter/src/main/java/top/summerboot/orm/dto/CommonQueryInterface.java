package top.summerboot.orm.dto;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author xieshuang
 * @date 2021-04-24 11:48
 */
public interface CommonQueryInterface {

    JSONObject getEq();

    JSONObject getLe();

    JSONObject getGe();

    JSONObject getLike();

    JSONObject getNe();

    JSONObject getIn();

    JSONObject getIsNull();

    List<OrderByDTO> getOrderBy();
}
