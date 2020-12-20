package cn.edu.xmu.user.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerChangeVo {
    @ApiModelProperty(value = "用户姓名")
    private String realname;

    private String gender;
    private LocalDate birthday;

}
