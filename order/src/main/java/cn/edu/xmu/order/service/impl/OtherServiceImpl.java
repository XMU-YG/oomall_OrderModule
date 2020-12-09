package cn.edu.xmu.order.service.impl;


import cn.edu.xmu.order.model.po.OrderItemPo;

import java.util.List;

public interface OtherServiceImpl {

    public Integer calculateRebateNum(List<OrderItemPo> objects, Long customerId);

    public String findCustomerById(Long customerId);

    public Long getBeSharedIdBySkuId(Long skuId);

}
