package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.util.OrderStatus;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 *状态返回Vo
 * @author Gang Ye
 */
@Data
@ApiModel
public class StateRetVo {
    private int code;
    private String name;

    public StateRetVo(OrderStatus orderStatus){
        this.code=orderStatus.getCode();
        this.name=orderStatus.getDescription();
    }

}
