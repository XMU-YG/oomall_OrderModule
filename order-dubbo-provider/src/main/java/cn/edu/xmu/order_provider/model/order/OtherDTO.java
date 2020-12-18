package cn.edu.xmu.order_provider.model.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OtherDTO implements Serializable {
    private Long shopId;
    private String orderSn;
    private Long orderId;
    private Long skuId;
    private String skuName;
}
