package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.RefundPo;
import cn.edu.xmu.payment.model.vo.RefundVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台退款
 * @author Yuting Zhong
 * @date Ctreated in 2020/12/3
 *
 */
@Data
public class Refund implements VoObject {


    private Long id;

    private Long paymentId;

    private Long amount;

    private Byte state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long orderId;

    private Long aftersaleId;

    public Refund(){}

    public Refund(RefundPo po){
        this.id=po.getId();
        this.paymentId=po.getPaymentId();
        this.amount=po.getAmount();
        this.state=po.getState();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.orderId=po.getOrderId();
        this.aftersaleId=po.getAftersaleId();
    }

    @Override
    public Object createVo(){
        RefundVo refundVo=new RefundVo();

        refundVo.setId(id);
        refundVo.setPaymentId(paymentId);
        refundVo.setAmount(amount);
        refundVo.setState(state);
        refundVo.setGmtCreate(gmtCreate);
        refundVo.setGmtModified(gmtModified);
        refundVo.setOrderId(orderId);
        refundVo.setAftersaleId(aftersaleId);

        return refundVo;
    }
    @Override
    public Object createSimpleVo(){return null;}

    public RefundPo createPo(){
        RefundPo refundPo=new RefundPo();

        refundPo.setId(id);
        refundPo.setPaymentId(paymentId);
        refundPo.setAmount(amount);
        refundPo.setState(state);
        refundPo.setGmtCreate(gmtCreate);
        refundPo.setGmtModified(gmtModified);
        refundPo.setOrderId(orderId);
        refundPo.setAftersaleId(aftersaleId);

        return refundPo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setAftersaleId(Long aftersaleId) {
        this.aftersaleId = aftersaleId;
    }
}
