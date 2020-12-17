package cn.edu.xmu.order_provider.model.order;

import lombok.Data;

@Data
public class OtherDTO {
    private Long shopId;
    private String orderSn;
    private Long orderId;
    private Long skuId;
    private String skuName;
}
