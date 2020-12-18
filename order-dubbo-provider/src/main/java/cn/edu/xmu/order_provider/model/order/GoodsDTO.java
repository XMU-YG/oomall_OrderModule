package cn.edu.xmu.order_provider.model.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单给商品的DTO
 */
@Data
public class GoodsDTO implements Serializable {
    private Long skuId;
    private Long customerId;

}
