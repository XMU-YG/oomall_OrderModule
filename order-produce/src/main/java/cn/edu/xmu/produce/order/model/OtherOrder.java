package cn.edu.xmu.produce.order.model;

import lombok.Data;

@Data
public class OtherOrder {
    private Long shopId;
    private String orderSn;
    private Long orderId;
    private Long skuId;
    private String skuName;
}
