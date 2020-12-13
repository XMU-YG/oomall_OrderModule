package cn.edu.xmu.order.model.bo;

import lombok.Data;
import springfox.documentation.spring.web.json.Json;

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

    public Long getGoods_sku_id() {
        return goods_sku_id;
    }

    public void setGoods_sku_id(Long goods_sku_id) {
        this.goods_sku_id = goods_sku_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public boolean isSeckill() {
        return isSeckill;
    }

    public void setSeckill(boolean seckill) {
        isSeckill = seckill;
    }
}
