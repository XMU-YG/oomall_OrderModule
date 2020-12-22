package cn.edu.xmu.payment.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.payment.model.po.RefundPo;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import cn.edu.xmu.payment.mapper.RefundPoMapper;
import cn.edu.xmu.payment.model.bo.NewRefund;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.Payment;


@Repository
public class RefundDao {
    private  static  final Logger logger = LoggerFactory.getLogger(RefundDao.class);

    @Resource
    private RefundPoMapper refundPoMapper;

    public ReturnObject<VoObject> createRefund(NewRefund refund){
        // 用传入的bo对象构建po

        RefundPo po=refund.createPo();
        // 一个生成paysn的函数  暂未更新数据库

        ReturnObject<VoObject> returnObject=null;

        int ret=refundPoMapper.insertSelective(po);

        if(ret==0){
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("新增失败：" + po.getOrderId()));
        }else{
            logger.debug("insertRefund: insert refund = " + po.toString());
            refund.setId(po.getId());
            returnObject=new ReturnObject (refund);
        }
        return returnObject;


    }
    /*public ReturnObject<VoObject> createrefund(Long id, NewRefundVo vo){

        Payment payment=new Payment(paymentDao.getPaymentPoByPrimarykey(id));

        ReturnObject<VoObject> ret=null;
        if(payment!=null){
            NewRefund newRefund=vo.createrefundbo(); //用前端传入的vo创建bo，写入了gmt，amount

            newRefund.setPaymentId(id); //写入payment_id

            newRefund.setOrderId(payment.getOrderId());

            newRefund.setAftersaleId(payment.getAftersaleId());

            newRefund.setState((byte) 0);

            ret=new ReturnObject<>(newRefund);
        }
        else{
            ret = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        return ret;
    }*/



}
