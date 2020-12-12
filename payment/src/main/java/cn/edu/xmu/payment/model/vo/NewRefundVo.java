package cn.edu.xmu.payment.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.edu.xmu.payment.model.bo.NewRefund;

import java.time.LocalDateTime;

/*
新建退款信息
传入退款金额
 */
@Data
public class NewRefundVo {
    //@ApiModelProperty(name = "退款金额", value = "amount", required = true)
    private Long amount;


    public void setAmount(Long amount) {
        this.amount = amount;
    }

    /*
    * 用前端传入的vo构建一个bo对象
    */
    public NewRefund createrefundbo(){
        NewRefund refundbo=new NewRefund();
        refundbo.setAmount(this.amount);
        refundbo.setGmtCreate(LocalDateTime.now());
        return refundbo;
    }
    public Long getAmount(){
        return this.amount;
    }

}
