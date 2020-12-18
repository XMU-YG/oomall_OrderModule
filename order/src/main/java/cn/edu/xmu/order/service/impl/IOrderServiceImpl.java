package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order_provider.IOrderService;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.order.service.OrderService;
import cn.edu.xmu.order_provider.model.order.GoodsDTO;
import cn.edu.xmu.order_provider.model.order.OrderVo;
import cn.edu.xmu.order_provider.model.order.OtherDTO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 订单提供接口 实现
 * @author Gang Ye
 */
@DubboService(version ="0.0.1") // 注意这里的Serivce引用的是dubbo的包
public class IOrderServiceImpl implements IOrderService {

    @Autowired
    private OrderService orderService;

    @Override
    public boolean changeOrderState(Long orderId, Byte state) {
        return false;
    }


    @Override
    public Long createAfterSaleOrder(Long shopId, String orderVoJson) {
        return orderService.createAfterSaleOrder(shopId,orderVoJson);
    }

    @Override
    public void classifyOrder(Long orderId) {
        orderService.classifyOrder(orderId);
    }

    @Override
    public OtherDTO getOtherDTO(Long orderItemId) {
        return orderService.getOrderDTOForOther(orderItemId);
    }

    @Override
    public GoodsDTO getGoodsDTO(Long orderItemId) {
        return orderService.getGoodsDTOForGoods(orderItemId);
    }

    @Override
    public String checkUserOrder(Long userId, Long orderId) {
        ReturnObject returnObject= orderService.verifyOrderByCustomerId(userId,orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)){
            return "1";
        }else if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
            return "-1";
        }else if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
            return "0";
        }else {
            return "-2";
        }
    }

    @Override
    public String checkShopOrder(Long shopId, Long orderId) {
        ReturnObject returnObject= orderService.verifyOrderByShopId(shopId,orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)){
            return "1";
        }else if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
            return "-1";
        }else if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
            return "0";
        }else {
            return "-2";
        }
    }
}
