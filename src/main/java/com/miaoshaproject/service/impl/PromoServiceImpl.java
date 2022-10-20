package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.PromoDOMapper;
import com.miaoshaproject.dataobject.PromoDO;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.PromoMode;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2019/2/26.
 */
@Service
public class PromoServiceImpl implements PromoService{
    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoMode getPromoByItemId(Integer itemId) {

        // 获取秒杀商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        //dataObject转化成Model
        PromoMode promoMode = convertFromDataObject(promoDO);
        if (promoMode == null) {
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或正在进行

        if (promoMode.getStartDate().isAfterNow()) {
            promoMode.setStauts(1);
        }else if (promoMode.getEndDate().isBeforeNow()){
            promoMode.setStauts(3);
        }else{
            promoMode.setStauts(2);
        }

     return promoMode;
    }

    private PromoMode convertFromDataObject(PromoDO promoDO) {
        if(promoDO == null){
            return null;
        }
        PromoMode promoMode = new PromoMode();
        BeanUtils.copyProperties(promoDO,promoMode);
        promoMode.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoMode.setStartDate(new DateTime(promoDO.getStartDate()));
        promoMode.setEndDate(new DateTime(promoDO.getEndDate()));

        return promoMode;
    }


}
