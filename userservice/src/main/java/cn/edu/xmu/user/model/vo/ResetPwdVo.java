package cn.edu.xmu.user.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "重置密码对象")
public class ResetPwdVo {
    private String mobile;
    private String email;

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }
}
