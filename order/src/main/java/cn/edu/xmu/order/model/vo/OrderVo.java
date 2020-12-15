package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderVo {

    //@NotEmpty
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

    public List<OrderItemVo> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemVo> orderItems) {
        this.orderItems = orderItems;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getPresaleId() {
        return presaleId;
    }

    public void setPresaleId(Long presaleId) {
        this.presaleId = presaleId;
    }

    public Long getGrouponId() {
        return grouponId;
    }

    public void setGrouponId(Long grouponId) {
        this.grouponId = grouponId;
    }

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
