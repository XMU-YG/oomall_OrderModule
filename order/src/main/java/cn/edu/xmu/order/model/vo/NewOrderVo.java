package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class NewOrderVo {
    public Long getPresaleId() {
        return presaleId;
    }

    public Long getGrouponId() {
        return grouponId;
    }

    @NotEmpty
    private List<NewOrderItemVo> orderItems;
    @NotBlank
    private String consignee;
    @NotNull
    private Long regionId;
    @NotBlank
    private String address;
    @NotBlank
    private String mobile;
    @NotBlank
    private String message;
    @NotNull
    private Long couponId;
    @NotNull
    private Long presaleId;
    @NotNull
    private Long grouponId;

    public OrderPo createOrderPo(){
        OrderPo orderPo=new OrderPo();
        orderPo.setConsignee(this.consignee);
        orderPo.setRegionId(this.regionId);
        orderPo.setMessage(this.message);
        orderPo.setAddress(this.address);
        orderPo.setMobile(this.mobile);
        orderPo.setPresaleId(this.presaleId);
        orderPo.setCouponId(this.couponId);
        orderPo.setGrouponId(this.grouponId);

        return orderPo;
    }
    public List<OrderItemPo> createOrderItemsPo(){
        ArrayList<OrderItemPo> orderItemPos=new ArrayList<>(this.orderItems.size());
        for (NewOrderItemVo newOrderItem : this.orderItems) {
            OrderItemPo orderItemPo=newOrderItem.createOrderItemPo();
            orderItemPos.add(orderItemPo);
        }
        return orderItemPos;
    }

}
