package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.vo.PaymentVo;
import cn.edu.xmu.payment.model.vo.RefundVo;

import io.lettuce.core.StrAlgoArgs;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Payment implements VoObject {


    private Long id;

    private Long orderId;

    private Long aftersaleId;

    private Long amount;

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getAftersaleId() {
        return aftersaleId;
    }

    public Long getActualAmount() {
        return actualAmount;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public String getPaymentPattern() {
        return paymentPattern;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public Byte getState() {
        return state;
    }

    public String getPaymentSn() {
        return paymentSn;
    }

    private Long actualAmount;

    private LocalDateTime payTime;

    private String paymentPattern;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Byte state;

    private String paymentSn;

    public Payment() {
    }
    public Payment(PaymentPo po){
        this.id=po.getId();
        this.orderId=po.getOrderId();
        this.aftersaleId=po.getAftersaleId();
        this.amount=po.getAmount();
        this.actualAmount=po.getActualAmount();
        this.payTime=po.getPayTime();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.paymentSn=po.getPaySn();
        this.state=po.getState();
        this.paymentPattern=po.getPaymentPattern();



    }
    @Override
    public Object createVo(){
        PaymentVo paymentVo=new PaymentVo();

        paymentVo.setId(id);
        paymentVo.setOrderId(orderId);
        paymentVo.setAftersaleId(aftersaleId);
        paymentVo.setAmount(amount);
        paymentVo.setActualAmount(actualAmount);
        paymentVo.setPayTime(payTime);
        paymentVo.setPaymentPattern(paymentPattern);
        paymentVo.setState(state);
        paymentVo.setBeginTime(beginTime);
        paymentVo.setEndTime(endTime);
        paymentVo.setGmtCreate(gmtCreate);
        paymentVo.setGmtModified(gmtModified);

        return paymentVo;
    }

    @Override
    public Object createSimpleVo(){return null;}

    public PaymentPo createPo(){
        PaymentPo paymentPo=new PaymentPo();

        paymentPo.setId(id);
        paymentPo.setOrderId(orderId);
        paymentPo.setAftersaleId(aftersaleId);
        paymentPo.setAmount(amount);
        paymentPo.setActualAmount(actualAmount);
        paymentPo.setPayTime(payTime);
        paymentPo.setPaymentPattern(paymentPattern);
        paymentPo.setState(state);
        paymentPo.setBeginTime(beginTime);
        paymentPo.setEndTime(endTime);
        paymentPo.setGmtCreate(gmtCreate);
        paymentPo.setGmtModified(gmtModified);
        paymentPo.setPaySn(paymentSn);

        return paymentPo;
    }

    public void setId(Long id){this.id=id;}

    public void setOrderId(Long orderId){this.orderId=orderId;}

    public void setAftersaleId(Long aftersaleId){this.aftersaleId=aftersaleId;}

    public void setAmount(Long amount){ this.amount=amount; }

    public void setActualAmount(Long actualAmount){this.actualAmount=actualAmount;}

    public void setPaymentPattern(String paymentPattern){this.paymentPattern=paymentPattern;}

    public void setGmtCreate(LocalDateTime gmtCreate){this.gmtCreate=gmtCreate;}

    public void setGmtModified(LocalDateTime gmtModified){this.gmtModified = gmtModified;}

    public void setBeginTime(LocalDateTime beginTime){this.beginTime=beginTime;}

    public void setEndTime(LocalDateTime endTime){this.endTime=endTime;}

    public void setPayTime(LocalDateTime payTime){this.payTime=payTime;}

    public void setState(Byte state){this.state=state;}

    public void setPaymentSn(){this.paymentSn= Common.genSeqNum();}

    public Long getAmount(){return this.amount;}

    public LocalDateTime getGmtCreate(){return this.gmtCreate;}
}
