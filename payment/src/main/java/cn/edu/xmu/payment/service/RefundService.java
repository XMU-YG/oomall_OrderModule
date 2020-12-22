package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.NewRefund;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import cn.edu.xmu.payment.dao.RefundDao;
@Service
public class RefundService {
    private Logger logger=LoggerFactory.getLogger(RefundService.class);

    @Autowired
    private RefundDao refundDao;

    @Autowired
    private PaymentDao paymentDao;

    @Transactional
    public ReturnObject<VoObject> createRefund(Long shopId,Long id, NewRefundVo vo){

        ReturnObject<VoObject> retobj=null;
        Payment payment=new Payment(paymentDao.getPaymentPoByPrimarykey(id));
        NewRefund newRefund=vo.createrefundbo(); //用前端传入的vo创建bo，写入了gmt，amount
        newRefund.setPaymentId(id); //得到支付单的id号
        newRefund.setAftersaleId(payment.getAftersaleId()); //得到支付单的售后订单号
        newRefund.setOrderId(payment.getOrderId());
        newRefund.setState((byte) 0);
        //newRefund.setState((byte) PaymentStates.PAYED.getCode());  这个要更新payment的支付状态的枚举类型后可以用
        //目前为止，bo的内容缺少paysn 传到Dao层后用函数生成

        retobj = refundDao.createRefund(newRefund);

        return retobj;
        /*
        // 根据支付单id查看该id是否存在
        if(!true){
            retobj=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"操作的资源id不存在");
        }
        //根据shopid校验该支付单是否为本商铺的支付
        if(!true){
            retobj=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"支付单不属于本店铺");
            logger.debug("findOrderRefundShop: fail: 支付单不属于本店铺");
        }
        // 校验退款金额是否大于支付单金额
        if(payment.getAmount()<vo.getAmount()){
            retobj=new ReturnObject<>(ResponseCode.REFUND_MORE,"退款金额超过支付金额");
        }

        else{
            NewRefund newRefund=vo.createrefundbo(); //用前端传入的vo创建bo，写入了gmt，amount
            newRefund.setPaymentId(payment.getId()); //得到支付单的id号
            newRefund.setAftersaleId(payment.getAftersaleId()); //得到支付单的售后订单号
            //newRefund.setState((byte) PaymentStates.PAYED.getCode());  这个要更新payment的支付状态的枚举类型后可以用
            //目前为止，bo的内容缺少paysn 传到Dao层后用函数生成
            //把bo对象传入Dao层，生成po对象并插入数据库

            retobj = refundDao.createRefund(newRefund);
        }
        */


    }

}
