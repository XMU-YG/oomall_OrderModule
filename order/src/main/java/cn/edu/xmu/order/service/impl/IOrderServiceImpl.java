package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.order.service.OrderService;
import cn.edu.xmu.produce.order.IOrderService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService(version ="1.0-SNAPSHOT") // 注意这里的Serivce引用的是dubbo的包
public class IOrderServiceImpl implements IOrderService {

    @Autowired
    private OrderService orderService;

    @Override
    public List<Long> getOrderItemIdList(List<Long> skuIds, Long customerId) {
        return orderService.getOrderItemIdList(skuIds,customerId);
    }

    @Override
    public String getOrderItemById(Long orderItemId) {
        return JacksonUtil.toJson(orderService.getOrderItemById(orderItemId));
    }

    @Override
    public String getOrderByOrderItemId(Long orderItemId) {
        return JacksonUtil.toJson(orderService.getOrderByItemId(orderItemId));
    }

    @Override
    public boolean changeOrderState(Long orderId, Byte state) {
        return false;
    }

    @Override
    public String createAfterSaleOrder(Long shopId, String orderVoJson) {
        return JacksonUtil.toJson(orderService.createAfterSaleOrder(shopId,orderVoJson));
    }

    @Override
    public void classifyOrder(Long orderId) {
        orderService.classifyOrder(orderId);
    }
}
