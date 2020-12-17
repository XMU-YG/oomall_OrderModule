package cn.edu.xmu.order_provider.goods.modol;

import lombok.Data;

/**
 * 商品模块返回的商品信息格式
 */
@Data
public class OrderGoods {

    private Long goods_sku_id;
    private Integer quantity;
    private Long price;
    private String name;
    private Long shopId;
    private boolean isSeckill;


}
