package cn.edu.xmu.payment.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.model.po.PaymentPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.mapper.PaymentPoMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Repository
public class PaymentDao {
    private  static  final Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    @Resource
    private PaymentPoMapper paymentPoMapper;

    public List<Payment> getPaymentInfo(Long customer_id,Long orderId){

        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria= example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<PaymentPo> paymentPos=paymentPoMapper.selectByExample(example); //得到满足条件的po对象

        List<Payment> ret=new ArrayList<>(paymentPos.size());

        for(PaymentPo pay:paymentPos){
            Payment User_payment=new Payment(pay);
            ret.add(User_payment);
        }
        return ret;
    }

    public List<Payment> admingetPaymentInfo(Long orderId){

        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria= example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<PaymentPo> paymentPos=paymentPoMapper.selectByExample(example); //得到满足条件的po对象

        List<Payment> ret=new ArrayList<>(paymentPos.size());
        for(PaymentPo pay:paymentPos){
            Payment Admin_payment=new Payment(pay);
            ret.add(Admin_payment);
        }
        return ret;
    }

    public List<Payment> getAftersalePayment(Long cusid, Long aftersaleId){

        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria= example.createCriteria();
        criteria.andAftersaleIdEqualTo(aftersaleId);
        List<PaymentPo> paymentPos=paymentPoMapper.selectByExample(example); //得到满足条件的po对象

        List<Payment> ret=new ArrayList<>(paymentPos.size());
        for(PaymentPo pay:paymentPos){
            Payment User_Info=new Payment(pay);
            ret.add(User_Info);
        }
        return ret;
    }

    public List<Payment> admingetAftersalePayment(Long aftersaleId){

        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria= example.createCriteria();
        criteria.andAftersaleIdEqualTo(aftersaleId);
        List<PaymentPo> paymentPos=paymentPoMapper.selectByExample(example); //得到满足条件的po对象

        List<Payment> ret=new ArrayList<>(paymentPos.size());
        for(PaymentPo pay:paymentPos){
            Payment Admin_Info=new Payment(pay);
            ret.add(Admin_Info);
        }
        return ret;
    }

    public List<Payment> findAllPatterns(){

        //List<Payment> PaymentPos = paymentPoMapper.select
        List<Payment> ret =null;

        return ret;

    }

    public PaymentPo getPaymentPoByPrimarykey(Long id){
        PaymentPo paymentPo=paymentPoMapper.selectByPrimaryKey(id);
        return paymentPo;
    }
}
