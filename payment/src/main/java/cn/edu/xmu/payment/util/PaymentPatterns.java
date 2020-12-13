package cn.edu.xmu.payment.util;
/**
 * 支付方式
 * @author Yuting Zhong@3333
 */
public enum PaymentPatterns {
    REBATEPAY("001","返点支付"),
    NORMALPAY("002","正常支付");

    private String code;
    private String description;

    PaymentPatterns(String code,String description){
        this.code=code;
        this.description=description;
    }

    public String getCode(){return this.code;}

    public String getDescription(){return this.description;}
}
