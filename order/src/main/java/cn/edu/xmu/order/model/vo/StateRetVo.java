package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.OrderState;
import lombok.Data;

/**
 *状态返回Vo
 * @author Gang Ye
 */
@Data
public class StateRetVo {
    private int code;
    private String name;

    public StateRetVo(OrderState orderState){
        this.code=orderState.getCode();
        this.name=orderState.getName();
    }

}
