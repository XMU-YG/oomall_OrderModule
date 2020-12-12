package cn.edu.xmu.payment.model.vo;

import lombok.Data;
import cn.edu.xmu.payment.model.bo.Payment;

/**
 * 支付方式VO
 */
@Data
public class PatternVo {
    private String patterncode;
    private String name;

    public PatternVo(Payment.Pattern pattern){
        patterncode=pattern.getPaymentPattern();
        name=pattern.getName();
    }

}
