package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDO;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;

import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Administrator on 2019/1/20.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;


    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;


    @Override
    public UserModel getUserById(Integer id) {

        // 调用userdomapper获取到对应的用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);

        if (userDO == null){
            return null;
        }

        //通过用户的Id获取用的的加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(id);

        return this.convertFromDataObject(userDO, userPasswordDO);
    }

    //验证数据并保存数据
    @Override
    @Transactional
    public void register(UserModel userModel) throws BussinessException {
        if (userModel == null) {
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
        }


        ValidationResult validationResult = validator.validate(userModel);
        if(validationResult.isHasErrors()) {
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
        }


        //保存userDo
        UserDO userDO = this.convertFromModel(userModel);
        try{
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException ex){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"手机号已重复!");
        }


        userModel.setId(userDO.getId());


        //保存UserPassword
        UserPasswordDO userPasswordDO = this.convertFromPasswordModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);



    }

    //用户登录
    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BussinessException {
        //通过用户的手机获取用户的信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO == null) {
            throw new BussinessException(EmBussinessError.USER_LOGIN_FAILE);
        }

        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = this.convertFromDataObject(userDO, userPasswordDO);


        //比对用户信息内加密的密码是否和传输进来的密码相匹配
        if (!encrptPassword.equals(userModel.getEncrptPassword())){
            throw new BussinessException(EmBussinessError.USER_LOGIN_FAILE);
        }
        return userModel;

    }

    //转换成密码vo模型
    private UserPasswordDO convertFromPasswordModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setUserId(userModel.getId());
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        return userPasswordDO;

    }

    //转换成vo模型
    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;


    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }

        return userModel;

    }
}
