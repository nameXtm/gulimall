package com.atguigu.gulimallcart.service;

import com.atguigu.gulimallcart.vo.CartItemVo;
import com.atguigu.gulimallcart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVo getCartItem(Long skuId);

    CartVo getCart() throws ExecutionException, InterruptedException;

    void checkItem(Long skuId, Integer checked);

    void deleteIdCartInfo(Integer skuId);

    void changeItemCount(Long skuId, Integer num);

    List<CartItemVo> getUserCartItems();
}
