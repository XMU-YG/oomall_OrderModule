package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.OrderRetVo;
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

    private Long grouponId;

    private Integer rebateNum;

    private LocalDateTime confirmTime;

    private String shipmentSn;

    private Byte state;

    private Byte subState;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
    private List<SimpleOrderItem> simpleOrderItemList;
    public Order(OrderPo po){
        this.customer=new Customer();
        this.shop=new Shop();
        this.id=po.getId();
        this.customer.setId(po.getCustomerId());
        this.shop.setId(po.getShopId());
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
        this.grouponId =po.getGrouponId();
        this.rebateNum=po.getRebateNum();
        this.confirmTime=po.getConfirmTime();
        this.shipmentSn=po.getShipmentSn();
        this.state=po.getState();
        this.subState =po.getSubstate();
        this.gmtCreate =po.getGmtCreate();
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


}
