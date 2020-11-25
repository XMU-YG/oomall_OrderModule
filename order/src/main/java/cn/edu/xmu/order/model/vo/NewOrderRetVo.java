package cn.edu.xmu.order.model.vo;

import java.time.LocalDateTime;

public class NewOrderRetVo {

    private Long id;

    private Long customerId;

    private Long shopId;

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

    private Long couponActivityId;

    private Long discountPrice;

    private Long originPrice;

    private Long presaleId;

    private Long grouponDiscount;

    private Integer rebateNum;

    private LocalDateTime confirmTime;

    private String shipmentSn;

    private Byte state;

    private Byte substate;

    private Byte beDeleted;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public void createdByVo(NewOrderVo vo){
        this.consignee=vo.getConsignee();
        this.address=vo.getAddress();
        this.mobile=vo.getMobile();
        this.message=vo.getMessage();
        this.regionId=vo.getRegionId();
        this.couponId=vo.getCouponId();
        this.presaleId=vo.getPresaleId();
    }
}
