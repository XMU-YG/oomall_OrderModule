package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrderPo;

import java.time.LocalDateTime;
import java.util.List;

public class Order implements VoObject {

    private Long id;

    //顾客信息
    private Long customerId;

    private Long customerUserName;

    private Long customerRealName;

    //店铺信息
    private Long shopId;

    private Long shopName;

    private Long shopGmtCreate;

    private Long shopGmtModified;

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
    private List<SimpleOrderItem> simpleOrderItemList;

    public Order(OrderPo po){
        this.id=po.getId();
        //顾客信息
        this.customerId=po.getCustomerId();
        //店铺信息
        this.shopId=po.getShopId();
        this.orderSn=po.getOrderSn();
        this.pid=po.getPid();
        this.consignee=po.getConsignee();
        this.regionId=po.getRegionId();
        this.address=po.getAddress();
        this.mobile=po.getMobile();
        this.message=po.getMessage();
        this.orderType=po.getOrderType();
        this.freightPrice=po.getFreightPrice();
        this.couponId=po.getCouponId();
        this.discountPrice=po.getDiscountPrice();
        this.originPrice=po.getOriginPrice();
        this.presaleId=po.getPresaleId();
        this.grouponDiscount=po.getGrouponDiscount();
        this.rebateNum=po.getRebateNum();
        this.confirmTime=po.getConfirmTime();
        this.shipmentSn=po.getShipmentSn();
        this.state=po.getState();
        this.substate=po.getSubstate();
        this.gmtCreated=po.getGmtCreated();
        this.gmtModified=po.getGmtModified();
    }
    @Override
    public Object createVo() {
        return null;
    }
    @Override
    public Object createSimpleVo() {
        return null;
    }

    public void setCustomerUserName(Long customerUserName) {
        this.customerUserName = customerUserName;
    }

    public void setCustomerRealName(Long customerRealName) {
        this.customerRealName = customerRealName;
    }

    public void setShopName(Long shopName) {
        this.shopName = shopName;
    }

    public void setShopGmtCreate(Long shopGmtCreate) {
        this.shopGmtCreate = shopGmtCreate;
    }

    public void setShopGmtModified(Long shopGmtModified) {
        this.shopGmtModified = shopGmtModified;
    }
}
