package cn.edu.xmu.user.model.vo;

import cn.edu.xmu.user.model.bo.Customer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerStateRetVo {
    @ApiModelProperty(value="用户状态码")
    private Integer code;

    @ApiModelProperty(value = "用户状态名")
    private String stateName;

    public CustomerStateRetVo(Customer.State state){
        this.code=state.getCode();
        this.stateName=state.getDescription();
    }
}
