package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.vo.PaymentVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Payment implements VoObject {

    public enum State{
        PAYED(0,"已支付"),
        NEEDPAY(1,"未支付"),
        FAIL(2,"支付失败");

    private static final Map<Integer, State>  stateMap;

    static {
        stateMap=new HashMap();
        for(State enum1:values()){
            stateMap.put(enum1.code,enum1);
        }
    }

    private int code;
    private String description;

    State(int code,String description){
        this.code=code;
        this.description=description;
    }

    public static State getTypeByCode(Integer code){return stateMap.get(code);}

    public Integer getCode(){return code;}

    public String getDescription(){return description;}

    }

    public enum Pattern{
        REFUND_PAY("001","返点支付"),
        ORDIMARY_PAY("002","普通支付");


        private static final Map<String,Payment.Pattern> pattern_map;

        static{
            pattern_map=new HashMap();
            for(Payment.Pattern enum1:values()){
                pattern_map.put(enum1.name, enum1);
            }
        }

        private String patterncode;
        private String name;

        Pattern(String patterncode,String name){
            this.patterncode=patterncode;
            this.name=name;
        }

        public static Pattern getPattern(String patterncode){
            return pattern_map.get(patterncode);
        }

        public String getPaymentPattern(){
            return this.patterncode;
        }

        public String getName(){
            return this.name;
        }

    }

    private Long id;

    private Long orderId;

    private Long amount;

    private Long actualAmount;

    private LocalDateTime payTime;

    private String paymentPattern;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long aftersaleId;

    private State state=State.NEEDPAY;

    private Pattern paymentPatten=Pattern.ORDIMARY_PAY;

    public Payment(PaymentPo po){
        this.id=po.getId();
        this.orderId=po.getOrderId();
        this.amount=po.getAmount();
        this.actualAmount=po.getActualAmount();
        this.payTime=po.getPayTime();
        this.paymentPattern=po.getPaymentPattern();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.aftersaleId=po.getAftersaleId();

        if(null!=po.getState()){
            this.state=State.getTypeByCode(po.getState().intValue());
        }

    }

    public Long getOrderId(){
        return this.orderId;
    }

    public Long getAmount(){
        return this.amount;
    }

    public Long getAftersaleId(){
        return this.aftersaleId;
    }

    public Long getId(){
        return this.id;
    }



    @Override
    public Object createVo(){
        PaymentVo paymentVo=new PaymentVo();
        paymentVo.setId(id);
        paymentVo.setOrderId(orderId);
        paymentVo.setAftersaleId(aftersaleId);
        paymentVo.setAmount(amount);
        paymentVo.setActualAmount(actualAmount);
        paymentVo.setPayTime(payTime);
        paymentVo.setPaymentPattern(paymentPattern);
        paymentVo.setBeginTime(beginTime);
        paymentVo.setEndTime(endTime);
        paymentVo.setGmtCreate(gmtCreate);
        paymentVo.setGmtModified(gmtModified);

        return paymentVo;
    }

    @Override
    public Object createSimpleVo(){return null;}


}
