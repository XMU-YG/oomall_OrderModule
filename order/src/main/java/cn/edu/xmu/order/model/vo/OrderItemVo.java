package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.po.OrderItemPo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class OrderItemVo {
    @NotNull
    private Long skuId;
    @NotNull
    private int quantity;

    private Long couponActId;


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
