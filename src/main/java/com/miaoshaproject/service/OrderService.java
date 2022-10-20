package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.OrderModel;

/**
 * Created by Administrator on 2019/2/17.
 */
public interface OrderService {

    OrderModel creatOrder(Integer userId, Integer itemId, Integer amount) throws BussinessException;
}
