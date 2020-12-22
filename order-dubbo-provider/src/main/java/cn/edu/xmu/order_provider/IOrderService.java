package cn.edu.xmu.order_provider;

import cn.edu.xmu.order_provider.model.order.GoodsDTO;
import cn.edu.xmu.order_provider.model.order.OtherDTO;

/**
 * 订单模块提供接口
 * @author Gang Ye
 * @version 0.0.1
 */
public interface IOrderService {

    /**
     * 这个有人用吗？？？
     * 改变指定订单状态
     * @param orderId  订单Id
     * @param state  目标状态码
     * @return 是否成功
     * @author Gang Ye
     */
    public boolean changeOrderState(Long orderId,Byte state);

    /**
     * 其他模块
     * 创建售后订单
     * @param customerId 顾客id
     * @param shopId 店铺id
     * @param orderItemId 订单明细id
     * @param consignee 收货人
     * @param regionId 地区id
     * @param mobile 电话
     * @param quantity 数量
     * @param address 地址
     * @return 订单id
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
     * @param orderId 订单号
     * @author Gang Ye
     */
    public void classifyOrder(Long orderId);

    /**
     * 其他模块
     * 根据itemId获得相应信息
     * @param orderItemId 订单明细id
     * @return OtherDTO
     */
    public OtherDTO getOtherDTO(Long orderItemId);

    /**
     * 商品模块
     * 根据itemId获得相应信息
     * @param orderItemId 订单明细id
     * @return GoodsDTO
     */
    public GoodsDTO getGoodsDTO(Long orderItemId);


    /**
     * 支付模块
     * 检查订单和用户的所属关系
     * @param userId 顾客id
     * @param orderId 订单号
     * @return 1：订单属于该用户  0：订单不属于该用户  -1：订单不存在  -2：数据库错误
     */
    public String checkUserOrder(Long userId,Long orderId);

    /**
     * 支付模块
     * 检查订单和商店的所属关系
     * @param shopId 店铺id
     * @param orderId 订单id
     * @return 1：订单属于该店铺  0：订单不属于该店铺  -1：订单不存在  -2：数据库错误
     */
    public String checkShopOrder(Long shopId,Long orderId);

    /**
     * 商品模块
     * 判断该店铺是否有订单
     * @param shopId 店铺id
     * @return boolean
     */
    public Boolean haveOrder(Long shopId);

    /**
     * 其他模块
     * 判断订单是否已完成
     * @param orderItemId 订单明细id
     * @return boolean
     */
    public Boolean orderIsDone(Long orderItemId);

    /**
     * 支付模块
     * 获得订单customer
     * @param orderId 订单id
     * @return 0查找失败 >0 userId
     */
    public Long getOrderUser(Long orderId);

    /**
     * 支付模块
     * 跟据orderItemId获得父订单id
     * @param orderItemId 订单明细id
     * @return  父订单id  -1:orderItemId不存在
     */
    public Long getOrderItemPid(Long orderItemId);

    /**
     * 支付模块
     * 获得订单总价
     * @param orderId 订单id
     * @return -1 order不存在  其他 订单总价
     */
    public Long getOrderAmount(Long orderId);

    /**
     * 支付模块
     * 判断订单是否可支付
     * @param orderId 订单
     * @return boolean
     */
    public Boolean ifOrderCanPay(Long orderId);
}
