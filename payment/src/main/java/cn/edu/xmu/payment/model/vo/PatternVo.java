package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.util.PaymentPatterns;
import lombok.Data;

/**
 * 模式vo
 * @author Yuting Zhong
 * @date 2020/12/12
 */
@Data
public class PatternVo {
    private String code;
    private String name;

    public PatternVo(PaymentPatterns paymentPatterns){
        code=paymentPatterns.getCode();
        name=paymentPatterns.getDescription();
    }
}
