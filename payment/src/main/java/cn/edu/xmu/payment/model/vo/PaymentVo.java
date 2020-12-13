package cn.edu.xmu.payment.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentVo {
    private Long id;

    private Long orderId;

    private Long aftersaleId;

    private Long amount;

    private Long actualAmount;

    private LocalDateTime payTime;

    private String paymentPattern;

    private Byte state;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public void setId(Long id){this.id=id;}

    public void setOrderId(Long orderId){this.orderId=orderId;}

    public void setAftersaleId(Long aftersaleId){this.aftersaleId=aftersaleId;}

    public void setAmount(Long amount){this.amount=amount;}

    public void setActualAmount(Long actualAmount){this.actualAmount=actualAmount;}

    public void setPayTime(LocalDateTime payTime){this.payTime=payTime;}

    public void setPaymentPattern(String paymentPattern){this.paymentPattern=paymentPattern;}

    public void setState(Byte state){this.state=state;}

    public void setBeginTime(LocalDateTime beginTime){this.beginTime=beginTime;}

    public void setEndTime(LocalDateTime endTime){this.endTime=endTime;}

    public void setGmtCreate(LocalDateTime gmtCreate){this.gmtCreate=gmtCreate;}

    public void setGmtModified(LocalDateTime gmtModified){this.gmtModified=gmtModified;}
}
