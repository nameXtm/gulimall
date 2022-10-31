package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WmsFeignService {

    @PostMapping(value = "/ware/waresku/hasStock")
     R getSkuHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/waresku//fare")
    public R getFare(@RequestParam("addrId") long addrId);
}
