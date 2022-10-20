package com.miaoshaproject.error;

/**
 * Created by Administrator on 2019/1/21.
 */
// 包装器业务异常实现
public class BussinessException extends Exception implements CommonError {

    private CommonError commonError;

    // 直接接受EmBussinessError的传参用于构造业务异常
    public BussinessException(CommonError commonError) {
        super();
        this.commonError = commonError;

    }

    // 接受自定义erroMsg的方式构造业务异常
    public BussinessException(CommonError commonError, String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }


    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
