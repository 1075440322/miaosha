package com.miaoshaproject.controller;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2019/2/20.
 */
@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials="true")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/createOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 获取用户的登录信息
        Boolean isLogin = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin.booleanValue()) {
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN);
        }

        // 获取用户信息
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        OrderModel orderModel = orderService.creatOrder(userModel.getId(),itemId,amount);

        return CommonReturnType.create(null);
    }

}
