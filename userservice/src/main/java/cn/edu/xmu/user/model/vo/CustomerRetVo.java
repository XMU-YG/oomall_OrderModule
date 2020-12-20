package cn.edu.xmu.user.model.vo;

import cn.edu.xmu.user.model.po.UserPo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "用户信息视图对象")
public class CustomerRetVo {
    private Long id;
    private String userName;
    private String name;
    private String mobile;
    private String email;
    private String gender;
    private LocalDate birthday;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public CustomerRetVo(UserPo po){
        this.id=po.getId();
        this.userName=po.getUserName();
        this.name=po.getRealName();
        this.email=po.getEmail();
        if(po.getGender()==0){
            this.gender="男";
        }
        else if(po.getGender()==1){
            this.gender="女";
        }
        this.birthday=po.getBirthday();
        this.state=po.getState();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
}
