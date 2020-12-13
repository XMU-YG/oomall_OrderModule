package cn.edu.xmu.payment.util;
/**
 * 支付状态
 * @author Yuting Zhong@3333
 */
public enum PaymentStates {
    PAYED(0,"已支付"),
    NEEDPAY(1,"未支付"),
    FAIL(2,"支付失败");

    private int code;
    private String description;

    PaymentStates(int code,String description){
        this.code=code;
        this.description=description;
    }

    public int getCode(){return this.code;}

    public String getDescription(){return description;}
}
