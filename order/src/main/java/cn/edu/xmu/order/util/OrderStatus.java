package cn.edu.xmu.order.util;
/**
 * 订单状态码
 * created by Gang Ye 2020/12/11
 */
public enum OrderStatus {


    WAIT_FOR_PAID(1, "待付款"),
    WAIT_FOR_RECEIVE(2, "待收货"),
    FINISHED(3, "已完成"),
    CANCELED(4,"已取消"),
    NEW_ORDER(11,"新订单"),
    WAIT_FOR_PAID_FINAL_PAYMENT(12, "待支付尾款"),
    PAID_SUCCEED(21, "付款完成"),
    WAIT_FOR_GROUP(22, "待成团"),
    UNGROUP(23,"未成团"),
    SHIPPED(24, "已发货");


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
