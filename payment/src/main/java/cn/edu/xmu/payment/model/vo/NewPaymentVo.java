package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 新支付Vo
 * @author Yuting Zhong
 * @date 2020/12/6
 */

@Data
public class NewPaymentVo {

    @Pattern(regexp = "^00?[1-2]",message = "字段不合法")
    private String paymentPattern;

    private Long price;

    public Payment createPayment(){
        Payment payment=new Payment();

        payment.setAmount(price);
        payment.setPaymentPattern(paymentPattern);
        return payment;
    }

    public String getPaymentPattern() {
        return paymentPattern;
    }

    public void setPaymentPattern(String paymentPattern) {
        this.paymentPattern = paymentPattern;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }


}
