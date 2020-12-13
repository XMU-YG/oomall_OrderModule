package cn.edu.xmu.payment.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.po.PaymentPoExample;
import cn.edu.xmu.payment.model.po.RefundPo;
import cn.edu.xmu.payment.model.vo.NewPaymentVo;
import cn.edu.xmu.payment.service.PaymentService;
import cn.edu.xmu.payment.util.PaymentStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Repository
public class PaymentDao {

    @Autowired
    private PaymentPoMapper paymentPoMapper;

    private Logger logger= LoggerFactory.getLogger(PaymentDao.class);


    /**
     * 新建支付
     * CreateBy: Yuting Zhong 2020-12-9
     */
    public ReturnObject<VoObject> createPayment(Payment payment) {
        //处理传入的bo对象
        payment.setActualAmount(payment.getAmount());
        payment.setGmtCreate(LocalDateTime.now());
        payment.setBeginTime(payment.getGmtCreate());
        payment.setEndTime(payment.getGmtCreate().plusMinutes(30));
        payment.setPayTime(LocalDateTime.now());
        payment.setState((byte) PaymentStates.PAYED.getCode());
        payment.setPaymentSn();

        //创建po对象
        PaymentPo po=payment.createPo();

        ReturnObject<VoObject> returnObject=null;

        int ret=paymentPoMapper.insertSelective(po);
        if(ret==0){
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + po.getOrderId()));
        }else{
            logger.debug("insertRole: insert role = " + po.toString());
            payment.setId(po.getId());
            returnObject=new ReturnObject<>(payment);
        }

        return returnObject;
    }

    /**
     * 跟据id获取支付单
     * @author Yuting Zhong
     * @param id
     * @return
     */
    public Payment getPaymentByID(Long id) {
        Payment returnObject=null;
        PaymentPo po=paymentPoMapper.selectByPrimaryKey(id);

        if(po!=null)
          returnObject=new Payment(po);

        return returnObject;
    }

    /**
     * 跟据订单id查询支付
     * @author Xuwen Chen
     * @param orderId
     * @return
     */
    public List<Payment> findPaymentByOrder(Long orderId) {
        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria= example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<PaymentPo> paymentPos=paymentPoMapper.selectByExample(example); //得到满足条件的po对象

        List<Payment> ret=new ArrayList<>(paymentPos.size());

        for(PaymentPo pay:paymentPos){
            Payment payment=new Payment(pay);
            ret.add(payment);
        }
        return ret;
    }

    /**
     * 跟据订单id查询支付
     * @author Xuwen Chen
     * @param aftersaleid
     * @return
     */
    public List<Payment> findPaymentByAftersale(Long aftersaleid) {
        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria= example.createCriteria();
        criteria.andAftersaleIdEqualTo(aftersaleid);
        List<PaymentPo> paymentPos=paymentPoMapper.selectByExample(example); //得到满足条件的po对象

        List<Payment> ret=new ArrayList<>(paymentPos.size());

        for(PaymentPo pay:paymentPos){
            Payment payment=new Payment(pay);
            ret.add(payment);
        }
        return ret;
    }
}
