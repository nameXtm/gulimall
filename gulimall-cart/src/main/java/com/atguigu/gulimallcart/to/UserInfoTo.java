package com.atguigu.gulimallcart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-06-30 17:35
 **/

@Data
@ToString
public class UserInfoTo {

    private Long userId;

    private String userKey;

    /**
     * 是否临时用户
     */
    private Boolean tempUser = false;

}
