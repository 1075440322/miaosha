package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.ItemStockDOMapper;
import com.miaoshaproject.dataobject.ItemDO;
import com.miaoshaproject.dataobject.ItemStockDO;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.PromoMode;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/2/9.
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;


    private ItemDO convertItemDoFromModel(ItemModel itemModel) {
        if (itemModel == null) {
           return null;

        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());

        return itemDO;

    }

    private ItemStockDO convertItemStockDoFromModel(ItemModel itemModel) {
        if (itemModel == null) {
           return null;

        }
        ItemStockDO itemStockDO = new ItemStockDO();
        BeanUtils.copyProperties(itemModel,itemStockDO);
        itemStockDO.setItemId(itemModel.getId());

        return itemStockDO;

    }

    @Override
    @Transactional
    public ItemModel creatItem(ItemModel itemModel) throws BussinessException {

        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //转化itemModel-->dataObject
        ItemDO itemDO = this.convertItemDoFromModel(itemModel);



        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());


        ItemStockDO itemStockDO = this.convertItemStockDoFromModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);


        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemModel> itemModels = new ArrayList<>();
        List<ItemDO> items = itemDOMapper.listItem();
        for (ItemDO itemDO :items) {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = this.convertModelFromDataobject(itemDO, itemStockDO );
            itemModels.add(itemModel);
        }

        return itemModels;
    }

    @Override
    public ItemModel getItemById(Integer id) {

        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null){
            return null;
        }
        //操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        //将dataobject-->model
        ItemModel itemModel = this.convertModelFromDataobject(itemDO, itemStockDO );

        //获取活动商品信息
        PromoMode promoMode = promoService.getPromoByItemId(itemModel.getId());
        if (promoMode != null  && promoMode.getStauts().intValue()!=3) {
            itemModel.setPromoMode(promoMode);
        }
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BussinessException {
        int affictedRow = itemStockDOMapper.decreaseStock(itemId,amount);
        if (affictedRow > 0) {

            // 更新数据库
            return true;
        }else {
            return false;
        }

    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId,amount);
    }

    private ItemModel convertModelFromDataobject(ItemDO itemDO ,ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;

    }
}
