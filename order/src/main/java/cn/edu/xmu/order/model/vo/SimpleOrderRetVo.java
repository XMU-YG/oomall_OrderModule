package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.SimpleOrder;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel
public class SimpleOrderRetVo {
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
    private Long grouponId;
    private Long presaleId;
    private String shipmentSn;

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
        this.id = bo.getId();
        this.orderType= bo.getOrderType();
        this.pid= bo.getPid();
        this.originPrice= bo.getOriginPrice();
        this.subState= bo.getSubState();
        this.shopId= bo.getShopId();
        this.grouponId=bo.getGrouponId();
        this.presaleId=bo.getPresaleId();
        this.shipmentSn=bo.getShipmentSn();
    }
}
