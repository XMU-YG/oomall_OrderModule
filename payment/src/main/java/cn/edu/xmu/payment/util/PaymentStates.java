package cn.edu.xmu.payment.util;
/**
 * 支付状态
 * @author Yuting Zhong@3333
 */
public enum PaymentStates {
    UNPAY(0,"未支付"),
    PAYED(1,"已支付"),
    FAILED_PAY(2,"支付失败");

    private int code;
    private String description;

    PaymentStates(int code,String description){
        this.code=code;
        this.description=description;
    }

    public int getCode(){return this.code;}

    public String getDescription(){return description;}
}
