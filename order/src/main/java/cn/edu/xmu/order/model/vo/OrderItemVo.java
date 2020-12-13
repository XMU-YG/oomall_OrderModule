package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.po.OrderItemPo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class OrderItemVo {
    @NotNull
    private Long skuId;
    @NotNull
    private int quantity;

    private Long couponActId;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getCouponActId() {
        return couponActId;
    }

    public void setCouponActId(Long couponActId) {
        this.couponActId = couponActId;
    }

    public OrderItemPo createOrderItemPo(){
        OrderItemPo orderItemPo=new OrderItemPo();
        if (this.couponActId!=null){
            orderItemPo.setCouponActivityId(this.couponActId);
        }
        orderItemPo.setQuantity(this.quantity);
        orderItemPo.setGoodsSkuId(this.skuId);

        return orderItemPo;
    }

}
