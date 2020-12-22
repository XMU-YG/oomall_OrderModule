package cn.edu.xmu.payment.service.impl;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order_provider.IFreightService;
import cn.edu.xmu.order_provider.IOrderService;
import cn.edu.xmu.order_provider.IPaymentService;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import cn.edu.xmu.payment.service.PaymentService;
import cn.edu.xmu.payment.service.RefundService;
import cn.edu.xmu.payment.util.PaymentPatterns;

import cn.edu.xmu.payment.util.RefundStates;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DubboService(version ="0.0.1")
public class RefundServiceImpl implements IPaymentService {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

   @DubboReference(version = "0.0.1",check = false)
   private IOrderService orderService;


    /**
     * 预售订单退款
     * @param shopId
     * @param customerId
     * @param pid
     * @param restPrice  预售尾款
     * @return  1:退款成功  0:无法匹配尾款支付  -1:创建退款失败  -2:预售订单支付有问题
     */
    @Override
    public Integer preOrderRefund(Long shopId,Long customerId,Long pid,Long restPrice){

        //退款金额为0  退款成功  不产生退款记录
        if(restPrice.equals(0))
            return 1;

        //查询两笔定金支付和尾款支付
        List<PaymentPo> paymentPos=paymentService.findOrderPayment(pid);
        if(paymentPos.isEmpty()||paymentPos==null){
            return -2;
        }

        if(paymentPos.size()!=2)
            return -2;

        Long firstPay=0L,nextPay=0L;
        Long firstPayId=0L,nextPayId=0L;
        firstPay=paymentPos.get(0).getAmount();
        firstPayId=paymentPos.get(0).getId();
        nextPay=paymentPos.get(1).getAmount();
        nextPayId=paymentPos.get(1).getAmount();

        Long paymentId;
        if(firstPay.equals(restPrice))
            paymentId=firstPayId;
        else if(nextPay.equals(restPrice))
            paymentId=nextPayId;
        else
            return -3;

        NewRefundVo vo=new NewRefundVo();
        vo.setAmount(restPrice);
        //创建退款
         ReturnObject refund=refundService.create(shopId,customerId,paymentId,vo);
         if(refund.getCode().equals(ResponseCode.OK))
             return 1;
         else
             return -1;
    }

    /**
     * 团购订单退款  只会有一笔支付  对那一笔支付进行退款
     * @param shopId
     * @param customerId
     * @param pid
     * @param amount
     * @return 1退款成功  0退款金额大于支付金额  -1退款创建失败  -2团购订单支付有问题
     */
    @Override
    public Integer couponRefund(Long shopId,Long customerId,Long pid,Long amount){
        //退款金额为0  退款成功 不产生退款记录
        if(amount.equals(0))
            return 1;

        //查询父订单支付
        List<PaymentPo> paymentPos=paymentService.findOrderPayment(pid);
        if(paymentPos.isEmpty()||paymentPos==null){
            return -2;
        }

        //支付多于一笔支付  不是团购支付
        if(paymentPos.size()!=1){
            return -2;
        }

        //退款金额大于支付金额
        if(amount.compareTo(paymentPos.get(0).getAmount())==1){
            return 0;
        }

        Long paymentId=paymentPos.get(0).getId();
        NewRefundVo vo=new NewRefundVo();
        vo.setAmount(amount);
        //创建支付
       ReturnObject refund=refundService.create(shopId,customerId,paymentId,vo);
       if(refund.getCode().equals(ResponseCode.OK))
           return 1;
       else
           return -1;
    }

    /**
     * 普通订单退款
     * @param shopId
     * @param customerId
     * @param pid
     * @param amount
     * @return 1退款成功 0退款金额大于支付金额 -1退款创建失败 -2 支付方面问题
     */
    @Override
    public Integer normalRefund(Long shopId,Long customerId,Long pid,Long amount){
        //退款金额为0 退款成功 但不生成退款记录
        if(amount.equals(0))
            return 1;

        //查询父订单支付
        List<PaymentPo> paymentPos=paymentService.findOrderPayment(pid);
        if(paymentPos.isEmpty()||paymentPos==null){
            return -2;
        }

        //如果有两次支付 分别拿到两次支付的id和金额   没有校验订单两次以上的支付  因为应该不会存在
        Long cashPay=0L,rebatePay=0L;
        Long cashPaymentId=0L,rebatePaymentId=0L;

        for(PaymentPo pay:paymentPos){
            if(pay.getPaymentPattern()== PaymentPatterns.NORMALPAY.getCode()) {
                cashPay=pay.getAmount();
                cashPaymentId=pay.getId();
            }
            else if(pay.getPaymentPattern()==PaymentPatterns.REBATEPAY.getCode()){
                rebatePay=pay.getAmount();
                rebatePaymentId=pay.getId();
            }
        }

        //退款金额应该小于支付金额
        if(amount.compareTo(cashPay+rebatePay)==1)
            return 0;

        //如果只有返点支付  就退回返点
        if(cashPaymentId.equals(0L)&&!rebatePaymentId.equals(0L)){
            NewRefundVo voRebate=new NewRefundVo();
            voRebate.setAmount(amount);
            //创建退款
          ReturnObject refundRebate=refundService.create(shopId,customerId,rebatePaymentId,voRebate);
          if(!refundRebate.getCode().equals(ResponseCode.OK))
              return -1;
        }else{//有现金支付 先进行返点退款  之后进行现金退款
            Long rebateAmount=0L,cashAmount=0L;
            //返点退款
            if(amount.compareTo(cashPay)==1)
            {
                rebateAmount=amount-cashPay;
                cashAmount=cashPay;

                NewRefundVo voRebate=new NewRefundVo();
                voRebate.setAmount(rebateAmount);
                //创建退款
               ReturnObject refundRebate=refundService.create(shopId,customerId,rebatePaymentId,voRebate);
               if(!refundRebate.getCode().equals(ResponseCode.OK))
                   return -1;
            }else{
                cashAmount=amount;
            }

            NewRefundVo voCash=new NewRefundVo();
            voCash.setAmount(cashAmount);
            //创建退款
           ReturnObject refundCash=refundService.create(shopId,customerId,cashPaymentId,voCash);
           if(!refundCash.getCode().equals(ResponseCode.OK))
               return -1;

        }
        return 1;
    }

    //售后退款
    /**
     * 售后退款
     * @param shopId
     * @param customerId
     * @param orderItemId
     * @param amount
     * @return  1退款成功 0退款金额大于支付金额 -1退款创建失败 -2 支付方面问题
     */
    @Override
    public Integer aftersaleRefund(Long shopId,Long customerId,Long orderItemId,Integer amount){
        Long pid= orderService.getOrderItemPid(orderItemId);
        //Long pid=0L;

        return normalRefund(shopId,customerId,pid,amount.longValue());
    }

}
