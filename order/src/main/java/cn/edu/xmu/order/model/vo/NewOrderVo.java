package cn.edu.xmu.order.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class NewOrderVo {
    private List<NewOrderItemVo> orderItems;
    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
    private Long couponId;
    private Long presaleId;
    private Long grouponId;

    public Long getGrouponId() {
        return grouponId;
    }

    public String getAddress() {
        return address;
    }

    public String getConsignee() {
        return consignee;
    }

    public Long getRegionId() {
        return regionId;
    }

    public String getMobile() {
        return mobile;
    }

    public String getMessage() {
        return message;
    }

    public Long getCouponId() {
        return couponId;
    }

    public Long getPresaleId() {
        return presaleId;
    }

    public List<NewOrderItemVo> getOrderItems() {
        return orderItems;
    }
}
