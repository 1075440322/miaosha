package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.ItemModel;

import java.util.List;

/**
 * Created by Administrator on 2019/2/9.
 */
public interface ItemService {

    //创建商品
    ItemModel creatItem(ItemModel itemModel) throws BussinessException;

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    // 库存的扣减
    boolean decreaseStock(Integer itemId, Integer amount) throws BussinessException;

    // 商品销量增加
    void increaseSales(Integer itemId, Integer amount);
}
