package com.miaoshaproject.error;

/**
 * Created by Administrator on 2019/1/21.
 */
public enum EmBussinessError implements CommonError {

    // 通用错误类型00001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOWN_ERROR(10002,"未知错误"),

    // 20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAILE(20002,"用户名或密码错误"),
    USER_NOT_LOGIN(20003,"用户还未登录"),

    //30000开头为商品信息相关错误
    ITEM_NOT_EXIST(30001,"商品不存在"),

    //40000开头为交易信息相关错误
    STOCK_NOT_ENOUGH(40001,"库存不足"),


    ;

    private EmBussinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;

    }

    private int errCode;
    private String errMsg;


    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
