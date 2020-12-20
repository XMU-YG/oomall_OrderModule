package cn.edu.xmu.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
public class LoginVo {
    @NotBlank(message = "必须输入用户名")
    @ApiModelProperty(name = "用户名", value = "testuser")
    private String userName;

    @NotBlank(message = "必须输入密码")
    @ApiModelProperty(name = "密码", value = "123456r")
    private String password;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
