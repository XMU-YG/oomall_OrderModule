package cn.edu.xmu.produce.order;

/**
 * 支付需要订单模块接口
 * @author Yuting Zhong
 * created at 2020/12/13
 */
public interface IPOrderService {
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