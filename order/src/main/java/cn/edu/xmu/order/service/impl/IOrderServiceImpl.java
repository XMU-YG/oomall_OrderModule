package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.order_provider.IOrderService;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.order.service.OrderService;
import cn.edu.xmu.order_provider.model.GoodsDTO;
import cn.edu.xmu.order_provider.model.OrderVo;
import cn.edu.xmu.order_provider.model.OtherDTO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version ="0.0.1") // 注意这里的Serivce引用的是dubbo的包
public class IOrderServiceImpl implements IOrderService {

    @Autowired
    private OrderService orderService;


    @Override
    public boolean changeOrderState(Long orderId, Byte state) {
        return false;
    }

    @Override
    public Long createAfterSaleOrder(Long shopId, OrderVo orderVo) {
        return null;
    }

    //@Override
    public String createAfterSaleOrder(Long shopId, String orderVoJson) {
        return JacksonUtil.toJson(orderService.createAfterSaleOrder(shopId,orderVoJson));
    }

    @Override
    public void classifyOrder(Long orderId) {
        orderService.classifyOrder(orderId);
    }

    @Override
    public OtherDTO getOrderDTO(Long orderItemId) {
        return orderService.getOrderDTOForOther(orderItemId);
    }

    @Override
    public GoodsDTO getGoodsDTO(Long orderItemId) {
        return null;
    }

    @Override
    public String checkUserOrder(Long userId, Long orderId) {
        return null;
    }

    @Override
    public String checkShopOrder(Long shopId, Long orderId) {
        return null;
    }
}
