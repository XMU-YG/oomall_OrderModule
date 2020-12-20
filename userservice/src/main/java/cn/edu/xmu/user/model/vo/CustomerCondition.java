package cn.edu.xmu.user.model.vo;

import lombok.Data;

@Data
public class CustomerCondition {
    private String userName;
    private String email;
    private String mobile;
    private Integer page;
    private Integer pageSize;

    public CustomerCondition(String userName, String email, String mobile, Integer page, Integer pageSize){
        this.userName=userName;
        this.email=email;
        this.mobile=mobile;
        this.page=page;
        this.pageSize=pageSize;
    }
}
