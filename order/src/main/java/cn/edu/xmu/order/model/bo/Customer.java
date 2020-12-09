package cn.edu.xmu.order.model.bo;

import lombok.Data;

@Data
public class Customer {
    //顾客信息
    private Long customerId;

    private String customerUserName;

    private String customerRealName;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerUserName() {
        return customerUserName;
    }

    public void setCustomerUserName(String customerUserName) {
        this.customerUserName = customerUserName;
    }

    public String getCustomerRealName() {
        return customerRealName;
    }

    public void setCustomerRealName(String customerRealName) {
        this.customerRealName = customerRealName;
    }
}
