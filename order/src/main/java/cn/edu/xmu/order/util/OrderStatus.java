package cn.edu.xmu.order.util;
/**
 * 订单状态码
 * created by snow 2020/12/02 12:52
 */
public enum OrderStatus {

    ORDER_CANCEL(0, "订单取消"),
    UNPAID_EARNEST(1, "待支付定金"),
    UNPAID(2, "待支付"),
    UNGROUP(3, "待参团"),
    EARNEST_PAID(4, "已支付定金"),
    UNPAID_FINAL_PAYMENT(5, "待支付尾款"),
    ORDER_CREATED(6, "创建订单"),
    PRESALE_TERMINATED(7, "预售中止"),
    GROUPING(8, "已参团"),
    GROUP_FAILED(9, "团购未达到门槛"),
    GROUP_SUCCEED(10, "已成团"),
    PAID_SUCCEED(11, "已支付"),
    FINAL_PAYMENT_PAID(12, "已支付尾款"),
    REFUNDED(13, "已退款"),
    ORDER_TERMINATED(14, "订单中止"),
    AFTER_SALE_UNSHIPPED(15, "售后单待发货"),
    SHIPPING(16, "发货中"),
    GOODS_ARRIVED(17, "到货"),
    CLIENT_RECEIVED(18, "已签收");

    private int code;
    private String description;
    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode(){
        return this.code;
    }

    public String getDescription() {
        return description;
    }

}
