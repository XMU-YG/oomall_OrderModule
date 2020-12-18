package cn.edu.xmu.payment.service.impl;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import cn.edu.xmu.payment.service.PaymentService;
import cn.edu.xmu.payment.service.RefundService;
import cn.edu.xmu.payment.util.RefundStates;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version ="0.0.1")
public class RefundServiceImpl {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

    //订单退款api  对子订单的现金进行退款
    /**
     * @author Yuting Zhong
     * @param shopId 店铺id
     * @param pid 父订单id
     * @param amount  退款金额
     * created at 2020/12/18
     */
    public boolean createOrderRefund(Long shopId,Long pid,Long amount){

        //查询父订单支付
        Long paymentId=paymentService.getOrderCashPaymentId(pid);

        //构造现金退款newRefunVo
        NewRefundVo vo=new NewRefundVo();
        vo.setAmount(amount);

        //调用service层退款
        ReturnObject refund=refundService.createRefund(shopId,paymentId,vo);
        if(refund.getCode().equals(ResponseCode.OK)){
            return true;
        }
        return false;
    }

}
