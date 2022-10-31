package com.xunqi.gulimall.seckill.controller;

import com.atguigu.common.utils.R;

import com.xunqi.gulimall.seckill.service.SeckillService;
import com.xunqi.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-07-10 11:01
 **/

@Controller
public class SeckillController {


    @Autowired
    private SeckillService seckillService;



    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    @GetMapping(value = "/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckilInfo(@PathVariable("skuId") Long skuId) {

        SeckillSkuRedisTo to = seckillService.getSkuSeckilInfo(skuId);

        return R.ok().setData(to);
    }



}
