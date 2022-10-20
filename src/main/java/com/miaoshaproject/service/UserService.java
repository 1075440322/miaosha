package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.UserModel;

/**
 * Created by Administrator on 2019/1/20.
 */
public interface UserService {

    // 通过用户Id获取用户对象的方法
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BussinessException;

    /**
     *
     * @param telphone  用户手机号
     * @param encrptPassword  用户加密后的密码
     * @throws BussinessException
     */
    UserModel validateLogin(String telphone, String encrptPassword) throws BussinessException;
}
