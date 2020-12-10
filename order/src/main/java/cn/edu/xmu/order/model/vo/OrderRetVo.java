package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Customer;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.Shop;
import cn.edu.xmu.order.model.bo.SimpleOrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRetVo {
    private Long id;

    //顾客信息
    private Customer customer;

    //店铺信息
    private Shop shop;

    private String orderSn;

    private Long pid;

    private String consignee;

    private Long regionId;

    private String address;

    private String mobile;

    private String message;

    private Byte orderType;

    private Long freightPrice;

    private Long couponId;

    private Long discountPrice;

    private Long originPrice;

    private Long presaleId;

    private Long grouponDiscount;

    private Integer rebateNum;

    private LocalDateTime confirmTime;

    private String shipmentSn;

    private Byte state;

    private Byte substate;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;
    private List<SimpleOrderItem> orderItems;

    public OrderRetVo(Order bo){
        this.id=bo.getId();
        this.customer=bo.getCustomer();
        this.shop=bo.getShop();
        this.orderSn=bo.getOrderSn();
        this.pid=bo.getPid();
        this.consignee=bo.getConsignee();
        this.regionId=bo.getRegionId();
        this.address=bo.getAddress();
        this.mobile=bo.getMobile();
        this.message=bo.getMessage();
        this.orderType=bo.getOrderType();
        this.freightPrice=bo.getFreightPrice();
        this.couponId=bo.getCouponId();
        this.discountPrice=bo.getDiscountPrice();
        this.originPrice=bo.getOriginPrice();
        this.presaleId=bo.getPresaleId();
        this.grouponDiscount=bo.getGrouponDiscount();
        this.rebateNum=bo.getRebateNum();
        this.confirmTime=bo.getConfirmTime();
        this.shipmentSn=bo.getShipmentSn();
        this.state=bo.getState();
        this.substate=bo.getSubstate();
        this.gmtCreated=bo.getGmtCreated();
        this.gmtModified=bo.getGmtModified();
        this.orderItems =bo.getSimpleOrderItemList();
    }
}
