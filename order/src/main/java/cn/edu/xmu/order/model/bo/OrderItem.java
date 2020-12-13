package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrderItemPo;
import lombok.Data;
import org.springframework.cglib.core.TinyBitSet;

import java.time.LocalDateTime;

/**
 * OrderItem详细信息
 */
@Data
public class OrderItem implements VoObject {
    private Long goods_sku_id;
    private Integer quantity;
    private Long order_id;
    private Long id;
    private Long price;
    private Long discount;
    private String name;
    private Long coupon_id;
    private Long coupon_activity_id;
    private Long be_share_id;
<<<<<<< Updated upstream
    private Long shopId;
=======
<<<<<<< Updated upstream
=======
    //private Long shopId;
>>>>>>> Stashed changes


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

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(Long coupon_id) {
        this.coupon_id = coupon_id;
    }

    public Long getCoupon_activity_id() {
        return coupon_activity_id;
    }

    public void setCoupon_activity_id(Long coupon_activity_id) {
        this.coupon_activity_id = coupon_activity_id;
    }

    public Long getBe_share_id() {
        return be_share_id;
    }

    public void setBe_share_id(Long be_share_id) {
        this.be_share_id = be_share_id;
    }

<<<<<<< Updated upstream
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
=======

>>>>>>> Stashed changes
>>>>>>> Stashed changes

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
