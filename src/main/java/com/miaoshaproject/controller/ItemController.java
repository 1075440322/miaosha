package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.ItemVo;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/2/9.
 */
@Controller
@RequestMapping("/item")
@CrossOrigin(allowCredentials="true")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType creatItem(@RequestParam(name="title")String title,
                                      @RequestParam(name="description")String description,
                                      @RequestParam(name="price")BigDecimal price,
                                      @RequestParam(name="stock")Integer stock,
                                      @RequestParam(name="imgUrl")String imgUrl) throws BussinessException {

        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        ItemModel itemModelForReturn = itemService.creatItem(itemModel);

        ItemVo itemVo = convertItemVoFromModel(itemModelForReturn);

        return CommonReturnType.create(itemVo);

    }

    //商品详情页浏览
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name="id")Integer id ) throws BussinessException {
        ItemModel itemModel = itemService.getItemById(id);
        if (itemModel == null) {
            throw new BussinessException(EmBussinessError.ITEM_NOT_EXIST);
        }
        ItemVo itemVo = convertItemVoFromModel(itemModel);



        return CommonReturnType.create(itemVo);

    }

    //商品信息列表
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType listItem() {
        List<ItemVo> itemVos = new ArrayList<>();
        List<ItemModel> itemModels = itemService.listItem();
        for (ItemModel itemMode: itemModels) {
             ItemVo itemVo = this.convertItemVoFromModel(itemMode);
             itemVos.add(itemVo);
        }

        return CommonReturnType.create(itemVos);

    }

    private ItemVo convertItemVoFromModel(ItemModel itemModel) {
        if(itemModel == null) {
            return null;
        }
        ItemVo itemVo = new ItemVo();
        BeanUtils.copyProperties(itemModel,itemVo);

        if (itemModel.getPromoMode() != null) {
            //有正在进行或即将进行的秒杀活动
            itemVo.setPromoState(itemModel.getPromoMode().getStauts());
            itemVo.setPromoId(itemModel.getPromoMode().getId());
            itemVo.setPromoPrice(itemModel.getPromoMode().getPromoItemPrice());
            itemVo.setStartDate(itemModel.getPromoMode().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH-mm-ss")));
        }else {
            itemVo.setPromoState(0);
        }
        return itemVo;
    }


}
