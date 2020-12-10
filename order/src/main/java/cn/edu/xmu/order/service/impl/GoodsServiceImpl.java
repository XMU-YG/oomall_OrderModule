package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.order.model.bo.OrderItem;
import cn.edu.xmu.order.model.po.OrderItemPo;

import java.util.List;

public interface GoodsServiceImpl {
    public List<Object> findGoodsInfo(List<Object> objects);

    public OrderItem findGoodsBySkuId(Long SkuId);

    public Integer getGoodsStockBySkuId(Long skuId);

    public String findShopById(Long id);

    public boolean checkPresaleSkuId(Long presaleId,List<Long>skuId);

    public boolean checkGrouponSkuId(Long grouponId,List<Long>skuId);

    public Long calculateGrouponDiscount(Long grouponId,Long skuId);
}
