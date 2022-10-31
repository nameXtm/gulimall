package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.AuthServerConstant.LOGIN_USER;

@Controller
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/reg.html")
    public String regPage(){
        return "reg";
    }

    @ResponseBody
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        //1、接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            //活动存入redis的时间，用当前时间减去存入redis的时间，判断用户手机号是否在60s内发送验证码
            long currentTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - currentTime < 60000) {
                //60s内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
            }
        }

        //2、验证码的再次效验 redis.存key-phone,value-code
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        String codeNum = String.valueOf(code);
        String redisStorage = codeNum + "_" + System.currentTimeMillis();

        //存入redis，防止同一个手机号在60秒内再次发送验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,
                redisStorage,10, TimeUnit.MINUTES);

        thirdPartFeignService.sendCode(phone, codeNum);

        return R.ok();
    }

    /**
     *
     * TODO: 重定向携带数据：利用session原理，将数据放在session中。
     * TODO:只要跳转到下一个页面取出这个数据以后，session里面的数据就会删掉
     * TODO：分布下session问题
     * RedirectAttributes：重定向也可以保留数据，不会丢失
     * 用户注册
     * @return
     */
    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVo vos, BindingResult result,
                           RedirectAttributes attributes ) {

        //如果有错误回到注册页面
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors", errors);

            //效验出错回到注册页面
            return "redirect:http://auth.localhost/reg.html";
        }

        //检验验证吗
        String code = vos.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
        if (!StringUtils.isEmpty(s)) {
            //截取字符串
            if (code.equals(s.split("_")[0])) {
                //删除验证码;令牌机制
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX+vos.getPhone());
                //验证码通过，真正注册，调用远程服务进行注册
                R register = memberFeignService.register(vos);
                if (register.getCode() == 0) {
                    //成功
                    return "redirect:http://auth.localhost/login.html";
                } else {
                    //失败
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", register.getData("msg",new TypeReference<String>(){}));
                    attributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.localhost/reg.html";
                }


            } else {
                //效验出错回到注册页面
                Map<String, String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                attributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.localhost/reg.html";
            }
        } else {
            //效验出错回到注册页面
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            attributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.localhost/reg.html redirect";

        }
    }
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session){
        //调用远程登录
        R login = memberFeignService.login(vo);
        if (login.getCode()==0){
            MemberResponseVo data = login.getData("data", new TypeReference<MemberResponseVo>() {});
            session.setAttribute(LOGIN_USER,data);
            Object loginUser = session.getAttribute("loginUser");
            String id = session.getId();
            Enumeration<String> attributeNames = session.getAttributeNames();
            System.out.println("SessionId"+id);
            System.out.println("attributeNames"+attributeNames);
            System.out.println("loginUser"+loginUser);
            return "redirect:http://localhost";
        }else {
            attributes.addFlashAttribute("error","登录异常，请联系15830317881");
            return "redirect:hhtp:auth.localhost/login";
        }


    }

}


