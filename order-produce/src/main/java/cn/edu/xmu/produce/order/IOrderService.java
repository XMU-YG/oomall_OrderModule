package cn.edu.xmu.produce.order;

import cn.edu.xmu.produce.order.model.OtherOrder;

import java.util.List;

/**
 * 订单模块提供接口
 * @author Gang Ye
 * @version 1.0-SNAPSHOT
 */
public interface IOrderService {

    public List<Long> getOrderItemIdList(List<Long> skuId,Long customerId);

    /**
     * 获得OrderItem
     * @param orderItemId
     * @return OrderItemPo视图String
     */
    public String getOrderItemById(Long orderItemId);

    /**
     * 获得Order
     * @param orderItemId
     * @return OrderPo视图String
     */
    public String getOrderByOrderItemId(Long orderItemId);

    /**
     * 改变指定订单状态
     * @param orderId  订单Id
     * @param state  目标状态码
     * @return 是否成功
     */
    public boolean changeOrderState(Long orderId,Byte state);

    public String createAfterSaleOrder(Long shopId, String orderVoJson);

    public void classifyOrder(Long orderId);

    public OtherOrder getOrderDTOForOther(Long orderItemId);

}
