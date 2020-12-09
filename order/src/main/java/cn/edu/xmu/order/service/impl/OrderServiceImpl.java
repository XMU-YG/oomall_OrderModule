package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.order.model.po.OrderItemPo;

import java.util.List;

/**
 * 订单模块所用外部接口定义
 * @author Gang Ye
 */
public interface OrderServiceImpl {

    public Long calculateFreight(List<OrderItemPo> objects);

}
