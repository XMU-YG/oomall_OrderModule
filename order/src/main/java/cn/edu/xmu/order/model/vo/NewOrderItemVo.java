package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.po.OrderItemPo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class NewOrderItemVo {
    @NotNull
    private Long skuId;
    @NotNull
    private int quantity;
    @NotNull
    private Long couponActId;

    public OrderItemPo createOrderItemPo(){
        OrderItemPo orderItemPo=new OrderItemPo();
        orderItemPo.setCouponActivityId(this.couponActId);
        orderItemPo.setQuantity(this.quantity);
        orderItemPo.setGoodsSkuId(this.skuId);

        return orderItemPo;
    }

}
