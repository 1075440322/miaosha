package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVo;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


/**
 * Created by Administrator on 2019/1/20.
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials="true")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"用户名或密码不能为空");
        }
        UserModel userModel = userService.validateLogin(telphone, this.EncodeByMd5(password));

        //将登录凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);


        return CommonReturnType.create(null);
    }


    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telphone")String telphone,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="name")String name,
                                     @RequestParam(name="gender")Byte gender,
                                     @RequestParam(name="age")Integer age,
                                     @RequestParam(name="password")String password
                                     ) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {


        //验证手机号和对应otpcode相符合
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(otpCode,inSessionOtpCode)){
            throw  new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"验证码不一致");
        }

        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("ByPhone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);



        return CommonReturnType.create(null);
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;

    }




    @RequestMapping(value = "/getOtp",method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone) {

        // 按照一定的方式生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        // 将otp验证码同对应的用户手机号相关联    在分布式的处理中将用户的手机号和验证码放到reids中
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        System.out.println("  telphone=  "+telphone+"  &otpCode=  "+otpCode);

        return CommonReturnType.create(null);
    }



    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id")Integer id) throws BussinessException {
        // 调用service服务获取对应id的用户对象返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if (userModel == null) {
            //userModel.setEncrptPassword("123");
            throw new BussinessException(EmBussinessError.USER_NOT_EXIST);

        }

        //将核心领域对象转化为可供前端使用的对象
        UserVo userVo = this.convertFromModel(userModel);
        return CommonReturnType.create(userVo);
    }


    private UserVo convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userModel, userVo);

        return userVo;
    }
    public static void main(String[] args){
        System.out.println(Boolean.valueOf(true));
        Boolean boo = Boolean.valueOf(true);
        System.out.println(Boolean.valueOf(boo));

    }

}
