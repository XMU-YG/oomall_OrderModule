package cn.edu.xmu.payment.util;

import java.sql.Ref;
/**
 * 退款状态
 * @author Yuting Zhong@3333
 */
public enum RefundStates {
    UNREFUND(0,"未退款"),
    REFUNDED(1,"已退款"),
    FAILED_REFUND(2,"退款失败");


    private int code;
    private String description;

    RefundStates(int code,String description){
        this.code=code;
        this.description=description;
    }

    public int getCode(){return this.code;}

    public String getDescription(){return this.description;}
}
