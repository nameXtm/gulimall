package com.atguigu.gulimall.order.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> threadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null) {

            threadLocal.set(attribute);

            return true;
        }else {
            //没登录
            session.setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.localhost/login.html");
            return false;
        }

    }
}
