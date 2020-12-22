package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.RefundPo;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import lombok.Data;
import cn.edu.xmu.payment.model.vo.RefundVo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class NewRefund implements VoObject{
    private Long id;
    private Long paymentId;
    private Long amount;
    private Long orderId;
    private Long aftersaleId;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;


    public NewRefund() {
    }
/**
 * 用bo对象创建po对象
 */
    public RefundPo createPo(){
        RefundPo po = new RefundPo();
        po.setId(id);
        po.setPaymentId(paymentId);
        po.setAmount(amount);
        po.setOrderId(orderId);
        po.setAftersaleId(aftersaleId);
        po.setState(state);
        po.setGmtCreate(gmtCreate);
        po.setGmtModified(gmtModified);
        return po;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getAftersaleId() {
        return aftersaleId;
    }

    public void setAftersaleId(Long aftersaleId) {
        this.aftersaleId = aftersaleId;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public Object createVo() {
        RefundVo retRefund=new RefundVo();

        retRefund.setId(id);
        retRefund.setPaymentId(paymentId);
        retRefund.setAmount(amount);
        retRefund.setState(state);
        retRefund.setGmtCreate(gmtCreate);
        retRefund.setGmtModified(gmtModified);
        retRefund.setOrderId(orderId);
        retRefund.setAftersaleId(aftersaleId);

        return retRefund;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
