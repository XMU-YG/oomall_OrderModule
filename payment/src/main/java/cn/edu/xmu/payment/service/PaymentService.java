package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.model.bo.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.xmu.payment.dao.PaymentDao;

import java.util.List;
@Service
public class PaymentService {
    private Logger logger=LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentDao paymentDao;

    @Transactional
    public ReturnObject<List> getPaymentInfo(Long customerId, Long orderId){
        ReturnObject<List> ret = new ReturnObject<>(paymentDao.getPaymentInfo(customerId,orderId));
        return ret;
    }

    @Transactional
    public ReturnObject<List> admingetPaymentInfo(Long orderId,Long shopId){
        ReturnObject<List> ret = new ReturnObject<>(paymentDao.admingetPaymentInfo(orderId));
        return ret;
    }

    @Transactional
    public ReturnObject<List>getAftersalePayment(Long cusid,Long aftersaleId){
        ReturnObject<List> ret=new ReturnObject<>(paymentDao.getAftersalePayment(cusid, aftersaleId));
        return ret;
    }

    @Transactional
    public ReturnObject<List>admingetAftersalePayment(Long aftersaleId){
        ReturnObject<List> ret=new ReturnObject<>(paymentDao.admingetAftersalePayment(aftersaleId));
        return ret;
    }

    @Transactional
    public ReturnObject<List>findAllPatterns(){
        ReturnObject<List> ret=new ReturnObject<>(paymentDao.findAllPatterns());
        return ret;
    }

}
