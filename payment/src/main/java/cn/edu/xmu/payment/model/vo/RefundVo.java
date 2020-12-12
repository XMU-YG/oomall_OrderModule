package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Refund;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefundVo {
    private Long id;
    private Long paymentId;
    private Long amount;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long orderId;
    private Long aftersaleId;

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
