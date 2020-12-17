package cn.edu.xmu.order.model.bo;

import lombok.Data;

@Data
public class Customer {
    //顾客信息
    private Long customerId;

    private String customerUserName;

    private String customerRealName;
}