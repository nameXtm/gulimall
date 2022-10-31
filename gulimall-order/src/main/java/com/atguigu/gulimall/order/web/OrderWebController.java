package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {


    @Autowired
    OrderService orderService;




    @GetMapping(value = "/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = orderService.confirmOrder();

        model.addAttribute("confirmOrderData",confirmVo);
        //展示订单确认的数据

        return "confirm";
    }

    //下单功能
    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping(value = "/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes attributes) {


       SubmitOrderResponseVo responseVo =  orderService.submitOrder(vo);

        if (responseVo.getCode() == 0) {
            return "pay";
        }else {
            return "redirect:http://order.localhost/toTrade";
        }

    }


}
