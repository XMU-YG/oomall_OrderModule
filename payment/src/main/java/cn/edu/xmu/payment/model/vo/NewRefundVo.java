package cn.edu.xmu.payment.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.edu.xmu.payment.model.bo.Refund;

import java.time.LocalDateTime;


@Data
public class NewRefundVo {
    //@ApiModelProperty(name = "退款金额", value = "amount", required = true)
    private Long amount;

    public Refund createRefund(){
        Refund refund=new Refund();
        refund.setAmount(this.amount);

        return refund;
    }

    public Long getAmount(){
        return this.amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

}
