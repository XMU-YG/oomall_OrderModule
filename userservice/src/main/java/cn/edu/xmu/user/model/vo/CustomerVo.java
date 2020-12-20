package cn.edu.xmu.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "用户信息视图对象")
public class CustomerVo {
    @ApiModelProperty(value = "用户姓名")
    private String realname;

    @Pattern(regexp = "[+]?[0-9*#]+",
            message = "手机号码格式不正确")
    @ApiModelProperty(value = "用户手机号")
    private String mobile;

    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "Email 格式不正确")
    @ApiModelProperty(value = "用户 Email 地址")
    private String email;
}
