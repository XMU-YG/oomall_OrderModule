package cn.edu.xmu.order_provider;

import cn.edu.xmu.order_provider.model.GoodsDTO;
import cn.edu.xmu.order_provider.model.OrderVo;
import cn.edu.xmu.order_provider.model.OtherDTO;

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
     * @param orderVo
     * @return orderId
     * @author Gang Ye
     */
    public Long createAfterSaleOrder(Long shopId, OrderVo orderVo);

    /**
     * 支付后分单
     * @param orderId
     * @author Gang Ye
     */
    public void classifyOrder(Long orderId);


    public OtherDTO getOrderDTO(Long orderItemId);


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

}
