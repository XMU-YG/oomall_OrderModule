package cn.edu.xmu.order.util;

public enum OrderType {

    NORMAL((byte)0,"普通订单"),
    PRESALE((byte)2,"预售订单"),
    GROUPON((byte)1,"团购订单")
    ;


    private Byte code;
    private String description;

    OrderType(Byte code, String description) {
        this.code = code;
        this.description = description;
    }

    public Byte getCode(){
        return this.code;
    }

    public String getDescription() {
        return description;
    }
}
