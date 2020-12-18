package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel
public class OrderVo {

    @NotEmpty(message = "商品不能为空")
    private List<OrderItemVo> orderItems;
    @NotBlank
    private String consignee;
    @NotNull
    private Long regionId;
    @NotBlank
    private String address;
    @NotBlank
    private String mobile;

    private String message;

    private Long couponId;

    private Long presaleId;

    private Long grouponId;


    public OrderPo createOrderPo(){
        OrderPo orderPo=new OrderPo();
        orderPo.setConsignee(this.consignee);
        orderPo.setRegionId(this.regionId);
        if (this.message!=null){
            orderPo.setMessage(this.message);
        }
        orderPo.setAddress(this.address);
        orderPo.setMobile(this.mobile);
        if (this.couponId!=null){
            orderPo.setCouponId(this.couponId);
        }
        if (this.presaleId!=null){
            orderPo.setPresaleId(this.presaleId);
        }
        if (this.grouponId!=null){
            orderPo.setGrouponId(this.grouponId);
        }

        return orderPo;
    }
    public List<OrderItemPo> createOrderItemsPo(){
        ArrayList<OrderItemPo> orderItemPos=new ArrayList<>(this.orderItems.size());
        for (OrderItemVo newOrderItem : this.orderItems) {
            OrderItemPo orderItemPo=newOrderItem.createOrderItemPo();
            orderItemPos.add(orderItemPo);
        }
        return orderItemPos;
    }

}
