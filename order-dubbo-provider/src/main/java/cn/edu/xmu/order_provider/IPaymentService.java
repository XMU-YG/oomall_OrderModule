package cn.edu.xmu.order_provider;

/**
 * 支付模块提供接口
 *  @author Yuting Zhong
 * @version 1.0-SNAPSHOT
 */
public interface IPaymentService {
    /**
     * 预售订单退款
     * @param shopId 店铺id
     * @param customerId  顾客id
     * @param pid  父订单id
     * @param restPrice  预售尾款价格
     * @return  1:退款成功  -1：失败 -2:预售订单支付有问题  -3:找不到尾款支付
     */
    public Integer preOrderRefund(Long shopId,Long customerId,Long pid,Long restPrice);

    /**
     * 团购订单退款
     * @param shopId  店铺id
     * @param customerId  顾客id
     * @param pid  父订单id
     * @param amount  退款金额
     * @return
     */
    public Integer couponRefund(Long shopId,Long customerId,Long pid,Long amount);

    /**
     * 普通订单退款
     * @param shopId 店铺id
     * @param customerId  顾客id
     * @param pid  父订单id
     * @param amount  退款金额
     * @return
     */
    public Integer normalRefund(Long shopId,Long customerId,Long pid,Long amount);

    /**
     * 售后退款
     * @param shopId
     * @param customerId
     * @param orderItemId
     * @param amount
     * @return
     */
    public Integer aftersaleRefund(Long shopId,Long customerId,Long orderItemId,Integer amount);
}
