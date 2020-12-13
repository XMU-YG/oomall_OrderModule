package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.util.PaymentStates;
import lombok.Data;

/**
 * 状态vo
 * @author Yuting Zhong
 * @date 2020/12/6
 */
@Data
public class StateVo {
    private int code;

    private String name;

    public StateVo(PaymentStates state){
        code=state.getCode();
        name=state.getDescription();
    }


}
