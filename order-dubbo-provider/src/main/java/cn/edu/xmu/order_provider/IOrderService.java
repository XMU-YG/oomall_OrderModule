package cn.edu.xmu.order_provider;

import cn.edu.xmu.order_provider.model.order.GoodsDTO;
import cn.edu.xmu.order_provider.model.order.OtherDTO;

/**
 * 订单模块提供接口
 * @author Gang Ye
 * @version 1.0-SNAPSHOT
 */
public interface IOrderService {

    /**
     * 改变指定订单状态
     * @param orderId  订单Id
     * @param state  目标状态码
     * @return 是否成功
     * @author Gang Ye
     */
    public boolean changeOrderState(Long orderId,Byte state);

    /**
     * 创建售后订单
     * @param shopId
     * @param
     * @return orderId
     * @author Gang Ye
     */
    public Long createAfterSaleOrder(
                                     Long customerId,
                                     Long shopId,
                                     Long orderItemId,
                                     String consignee,
                                     Long regionId,
                                     String mobile,
                                     Integer quantity,
                                     String address);

    /**
     * 支付后分单
     * @param orderId
     * @author Gang Ye
     */
    public void classifyOrder(Long orderId);


    public OtherDTO getOtherDTO(Long orderItemId);


    public GoodsDTO getGoodsDTO(Long orderItemId);


    /**
     * 检查订单和用户的所属关系
     * @param userId
     * @param orderId
     * @return 1：订单属于该用户  0：订单不属于该用户  -1：订单不存在  -2：数据库错误
     */
    public String checkUserOrder(Long userId,Long orderId);

    /**
     * 检查订单和商店的所属关系
     * @param shopId
     * @param orderId
     * @return 1：订单属于该店铺  0：订单不属于该店铺  -1：订单不存在  -2：数据库错误
     */
    public String checkShopOrder(Long shopId,Long orderId);

    public boolean haveOrder(Long shopId);

    public boolean orderIsDone(Long orderItemId);

    /**
     *
     * @param orderId
     * @return 0查找失败 >0 userId
     */
    public Long getOrderUser(Long orderId);

    /**
     * 跟据orderItemId获得父订单id
     * @param orderItemId
     * @return  父订单id  -1:orderItemId不存在
     */
   // public Long getOrderItemPid(Long orderItemId);

    /**
     * 获得订单总价
     * @param orderId
     * @return -1 order不存在  其他 订单总价
     */
    public Long getOrderAmount(Long orderId);
}
