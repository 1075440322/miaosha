package com.miaoshaproject.service;

import com.miaoshaproject.service.model.PromoMode;

/**
 * Created by Administrator on 2019/2/26.
 */
public interface PromoService {


    //根据ItemId

    PromoMode getPromoByItemId(Integer itemId);
}
