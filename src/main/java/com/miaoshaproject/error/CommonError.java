package com.miaoshaproject.error;

/**
 * Created by Administrator on 2019/1/21.
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);
}
