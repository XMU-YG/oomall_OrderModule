package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.OrderRetVo;
import cn.edu.xmu.order.model.vo.SimpleOrderRetVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息的BO对象，包含订单的详细信息
 * author Gang Ye
 * create 2020/11/26
 * modify 2020/12/9 by Gang Ye
 *                      修改顾客，店铺信息为类对象
 */
@Data
public class Order implements VoObject {

    private Long id;

    private Customer customer;

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
    private List<SimpleOrderItem> simpleOrderItemList;

    public Order(OrderPo po){
        this.customer=new Customer();
        this.shop=new Shop();
        this.id=po.getId();
        this.customer.setCustomerId(po.getCustomerId());
        this.shop.setShopId(po.getShopId());

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
        this.gmtCreated=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
    @Override
    public OrderRetVo createVo() {
        return new OrderRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
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

    public Byte getOrderType() {
        return orderType;
    }

    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }

    public Long getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(Long freightPrice) {
        this.freightPrice = freightPrice;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Long discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Long getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(Long originPrice) {
        this.originPrice = originPrice;
    }

    public Long getPresaleId() {
        return presaleId;
    }

    public void setPresaleId(Long presaleId) {
        this.presaleId = presaleId;
    }

    public Long getGrouponDiscount() {
        return grouponDiscount;
    }

    public void setGrouponDiscount(Long grouponDiscount) {
        this.grouponDiscount = grouponDiscount;
    }

    public Integer getRebateNum() {
        return rebateNum;
    }

    public void setRebateNum(Integer rebateNum) {
        this.rebateNum = rebateNum;
    }

    public LocalDateTime getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(LocalDateTime confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getShipmentSn() {
        return shipmentSn;
    }

    public void setShipmentSn(String shipmentSn) {
        this.shipmentSn = shipmentSn;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Byte getSubstate() {
        return substate;
    }

    public void setSubstate(Byte substate) {
        this.substate = substate;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public List<SimpleOrderItem> getSimpleOrderItemList() {
        return simpleOrderItemList;
    }

    public void setSimpleOrderItemList(List<SimpleOrderItem> simpleOrderItemList) {
        this.simpleOrderItemList = simpleOrderItemList;
    }
}
