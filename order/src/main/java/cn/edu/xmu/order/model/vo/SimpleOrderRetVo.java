package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.SimpleOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleOrderRetVo {
    private Long bo;
    private Long customerId;
    private Long shopId;
    private Long pid;
    private Byte orderType;
    private Byte state;
    private Byte subState;
    private LocalDateTime gmtCreate;
    private Long originPrice;
    private Long discountPrice;
    private Long freightPrice;

    /**
     * bo构造vo
     * @param bo
     */
    public SimpleOrderRetVo(SimpleOrder bo) {
        this.state= bo.getState();
        this.customerId= bo.getCustomerId();
        this.discountPrice= bo.getDiscountPrice();
        this.freightPrice= bo.getFreightPrice();
        this.gmtCreate= bo.getGmtCreate();
        this.bo = bo.getId();
        this.orderType= bo.getOrderType();
        this.pid= bo.getPid();
        this.originPrice= bo.getOriginPrice();
        this.subState= bo.getSubState();
        this.shopId= bo.getShopId();
    }
}
