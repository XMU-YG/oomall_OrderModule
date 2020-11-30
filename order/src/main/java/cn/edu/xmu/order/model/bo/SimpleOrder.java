package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrderPo;

import java.time.LocalDateTime;

/**
 * BO对象，包含订单概要信息
 * author Gang Ye
 * create 2020/11/26
 * modify 2020/11/26 by Gang Ye
 */
public class SimpleOrder implements VoObject {
    private Long id;
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
     * 由OrderPo构造BO
     * @param orderPo
     */
    public SimpleOrder(OrderPo orderPo) {
        this.state=orderPo.getState();
        this.customerId=orderPo.getCustomerId();
        this.discountPrice=orderPo.getDiscountPrice();
        this.freightPrice=orderPo.getFreightPrice();
        this.gmtCreate=orderPo.getGmtCreated();
        this.id=orderPo.getId();
        this.orderType=orderPo.getOrderType();
        this.pid=orderPo.getPid();
        this.originPrice=orderPo.getOriginPrice();
        this.subState=orderPo.getSubstate();
        this.shopId=orderPo.getShopId();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
