package com.miaoshaproject.response;

/**
 * Created by Administrator on 2019/1/21.
 */
public class CommonReturnType {

    // 表明对应请求的返回处理结果"success"或"fail"
    private String status;

    // 若status=sucess,则data内返回前端需要的json数据
    // 若status=fail,则data内使用通用的错误码格式
    private Object data;

    // 定义一个通用的创建方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");

    }

    // private私有的方法  上面的的create方法来提供访问入口
    public static CommonReturnType create(Object result, String status) {
        CommonReturnType commonReturnType = new CommonReturnType();
        commonReturnType.setStatus(status);
        commonReturnType.setData(result);
        return commonReturnType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
