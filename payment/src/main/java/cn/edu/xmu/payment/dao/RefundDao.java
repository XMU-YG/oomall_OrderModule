package cn.edu.xmu.payment.dao;

import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.util.RefundStates;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Service;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.mapper.RefundPoMapper;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.po.RefundPo;
import cn.edu.xmu.payment.model.po.RefundPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 退款dao
 * @author Yuting Zhong
 */

@Repository
public class RefundDao {

    private static final Logger logger=LoggerFactory.getLogger(RefundDao.class);

    @Autowired
    private RefundPoMapper refundPoMapper;


    /**
     * 通过订单号查找退款信息
     * createdBy: Yuting Zhong 2020-12-5
     */
    public ReturnObject<VoObject> findRefundByOrder(Long id){
        RefundPoExample example=new RefundPoExample();
        RefundPoExample.Criteria criteria=example.createCriteria();
        criteria.andOrderIdEqualTo(id);

        List<RefundPo> refundPos=refundPoMapper.selectByExample(example);

        ReturnObject<VoObject> returnObject=null;

        if(refundPos==null||refundPos.isEmpty()){
            returnObject = new ReturnObject<>(ResponseCode.OK,"该订单无退款");
            logger.debug("findRefundByOrder: 该订单无退款");
        }else {
            returnObject = new ReturnObject<>(new Refund(refundPos.get(0)));
            logger.debug("findRefunfByOrder: refund: "+returnObject.toString());
        }

        return returnObject;
    }

    /**
     * 通过售后单号查找退款信息
     * CreateBy: Yuting Zhong 2020-12-6
     */
    public ReturnObject<VoObject> findRefundByAftersale(Long id){
        RefundPoExample example=new RefundPoExample();
        RefundPoExample.Criteria criteria=example.createCriteria();
        criteria.andAftersaleIdEqualTo(id);

        List<RefundPo> refundPos=refundPoMapper.selectByExample(example);

        ReturnObject<VoObject> returnObject=null;

        if(refundPos==null||refundPos.isEmpty()){
            returnObject=new ReturnObject<>(ResponseCode.OK,"该售后单无退款");
            logger.debug("findRefundByAftersale:  售后单无退款");
        }else{
            returnObject=new ReturnObject<>(new Refund(refundPos.get(0)));
            logger.debug("findRefunByAftersale: refund: "+returnObject.toString());
        }

        return returnObject;
    }

    /**
     * 新建退款
     * @author Yuting Zhong
     * Modified at 2020/12/10
     */
    public ReturnObject<VoObject> createRefund(Refund refund) {
        //在payment里填入时间、状态、生成sn
        refund.setGmtCreate(LocalDateTime.now());
        refund.setState((byte)RefundStates.REFUNDED.getCode());

        //用bo创建po
        RefundPo po=refund.createPo();

        ReturnObject<VoObject> returnObject=null;
        int ret=refundPoMapper.insertSelective(po);
        if(ret==0){
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + po.getOrderId()));
        }else{
            logger.debug("insertRole: insert role = " + po.toString());
            refund.setId(po.getId());
            returnObject=new ReturnObject<>(refund);
        }

        return returnObject;
    }
}
