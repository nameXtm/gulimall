package com.xunqi.gulimall.seckill.service;

import com.xunqi.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-07-09 19:29
 **/
public interface SeckillService {

    /**
     * 根据skuId查询商品是否参加秒杀活动
     *
     * @param skuId
     * @return
     */
    SeckillSkuRedisTo getSkuSeckilInfo(Long skuId);
}



