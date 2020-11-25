package cn.edu.xmu.order.model.vo;

import lombok.Data;

@Data
public class NewOrderItemVo {
    private Long skuId;//新增订单信息
    private int quantity;//新增订单信息
    private Long couponActId;//新增订单信息
    private Long OrderId;
    private String name;
    private Long price;
    private Long discount;
    private Long beSharedId;

    public NewOrderItemVo(){

    }

    public Long getCouponActId() {
        return couponActId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public int getQuantity() {
        return quantity;
    }
}
