package cn.edu.xmu.order_provider.model.order;

import lombok.Data;

/**
 * 订单给商品的DTO
 */
@Data
public class GoodsDTO {
    private Long skuId;
    private Long customerId;

}
