package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order_provider.IOrderService;
import cn.edu.xmu.order_provider.other.IPOtherService;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.vo.NewPaymentVo;

import cn.edu.xmu.payment.util.PaymentPatterns;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @DubboReference(version ="1.0-SNAPSHOT")
    private IOrderService orderService;

    @DubboReference(version="1.0-SNAPSHOT")
    private IPOtherService otherSerivice;

    private Logger logger= LoggerFactory.getLogger(PaymentService.class);

    /**
     * 买家为订单创建支付
     * @pararm 订单id id
     * @author Yuting Zhong
     * Modified at 2020/12/9
     */
   public ReturnObject<VoObject> createOrderPayment(Long userId,Long orderId,NewPaymentVo vo){
       ReturnObject<VoObject> retObject=null;

       String checkBelong=orderService.checkUserOrder(userId,orderId);
      // int checkBelong=1;

       //校验用户和订单的从属关系  若订单不存在或订单不属于对应用户，则返回相应错误码，并直接返回给controller层
      if(checkBelong.equals("-1")){
          logger.debug("findOrderRefundShop: fail: 订单不存在");
           retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
       }else if(checkBelong.equals("0")){
          logger.debug("findOrderRefundShop: fail: 该订单无权限");
          retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权访问");
      }else if(checkBelong.equals("1")){
          if(vo.getPaymentPattern()==PaymentPatterns.REBATEPAY.getCode())
          {
              //在用户模块查询返点够不够
              boolean rebateEnough= otherSerivice.reduceRebate(userId,vo.getPrice());

              if(!rebateEnough){
                  retObject=new ReturnObject<>(ResponseCode.REBATE_NOTENOUGH,"返点不足");
                  logger.debug("findOrderRefundShop: fail: 返点不足");
                  return retObject;
              }
          }

               //创建bo对象
               Payment payment=vo.createPayment();
               payment.setOrderId(orderId);

               //用bo对象payment在dao层创建po对象，dao层返回构造后的vo对象
               retObject=paymentDao.createPayment(payment);

       }

       return retObject;
   }

    /**
     * 买家为售后单创建支付
     * @pararm 售后单id id
     * @author Yuting Zhong
     * Modified at 2020/12/9
     */
    public ReturnObject<VoObject> createAftersalePayment(Long userId,Long aftersaleId,NewPaymentVo vo) {
        ReturnObject<VoObject> retObject = null;

        //String checkBelong="1";
        String checkBelong = otherSerivice.checkUserAftersale(userId, aftersaleId);
        //校验用户和售后单的从属关系  若订单不存在或订单不属于对应用户，则返回相应错误码，并直接返回给controller层
        if (checkBelong.equals("-1")) {
            logger.debug("findOrderRefundShop: fail: 售后单不存在");
            retObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "售后单不存在");
        } else if (checkBelong.equals("0")) {
            logger.debug("findOrderRefundShop: fail: 该售后单无权限");
            retObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "该售后单无权访问");
        } else if (checkBelong.equals("1")) {

            if (vo.getPaymentPattern() == PaymentPatterns.REBATEPAY.getCode()) {
                //扣除用户返点
               boolean rebateEnough = otherSerivice.reduceRebate(userId, vo.getPrice());
                // boolean rebateEnough=true;
                if (!rebateEnough) {
                    retObject = new ReturnObject<>(ResponseCode.REBATE_NOTENOUGH, "返点不足");
                    logger.debug("findOrderRefundShop: fail: 返点不足");
                }
                return retObject;

            }
            //创建bo对象
            Payment payment = vo.createPayment();
            payment.setAftersaleId(aftersaleId);

            //用bo对象payment在dao层创建po对象，dao层返回构造后的vo对象
            retObject = paymentDao.createPayment(payment);

        }

        return retObject;
    }

    /**
     * 管理员查看订单支付信息
     * @author Yuting Zhong@3333
     * Modified at 2020/12/11
     */
    public ReturnObject findOrderPaymentShop(Long orderId, Long shopId) {

        String checkBelong=orderService.checkShopOrder(shopId,orderId);
        //String checkBelong="1";

        if(checkBelong.equals("-1")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
            return returnObject;
        }else if(checkBelong.equals("0")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
            return returnObject;
        }else if(checkBelong.equals("1")){
            ReturnObject<List> ret = new ReturnObject<>(paymentDao.findPaymentByOrder(orderId));
            return ret;
        }
        return null;
    }


    /**
     * 管理员查看售后单支付信息
     * @author Yuting Zhong@3333
     * Modified at 2020/12/11
     */
    public ReturnObject findAftersalePaymentShop(Long aftersaleId, Long shopId) {

        //检查店铺和售后单的从属关系
       String checkBelong=otherSerivice.checkShopAftersale(shopId,aftersaleId);
        // String checkBelong="1";
        if(checkBelong.equals("-1")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
            return returnObject;
        }else if(checkBelong.equals("0")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
            return returnObject;
        }else if(checkBelong.equals("1")){
            ReturnObject<List> ret = new ReturnObject<>(paymentDao.findPaymentByAftersale(aftersaleId));
            return ret;
        }
        return null;
    }

    /**
     * 买家查看订单支付信息
     * @author Yuting Zhong@3333
     * Modified at 2020/12/11
     */
    public ReturnObject findOrderPaymentSelf(Long userId, Long orderId) {
        //检查
        String checkBelong=orderService.checkUserOrder(userId,orderId);
        //String checkBelong="1";
        if(checkBelong.equals("-1")){
           ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
           return returnObject;
        }else if(checkBelong.equals("0")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
            return returnObject;
        }else if(checkBelong.equals("1")){
            ReturnObject<List> ret = new ReturnObject<>(paymentDao.findPaymentByOrder(orderId));
            return ret;
        }
        return null;
    }

    /**
     * 买家查看售后单支付信息
     * @author Yuting Zhong@3333
     * Modified at 2020/12/11
     */
    public ReturnObject findAftersalePaymentSelf(Long userId,Long aftersaleId) {
        //检查用户和售后单的从属关系
        String checkBelong=otherSerivice.checkUserAftersale(userId,aftersaleId);
        //String checkBelong="1";
        if(checkBelong.equals("-1")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
            return returnObject;
        }else if(checkBelong.equals("0")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
            return returnObject;
        }else if(checkBelong.equals("1")){
            ReturnObject<List> ret = new ReturnObject<>(paymentDao.findPaymentByAftersale(aftersaleId));
            return ret;
        }
        return null;
    }

    /**
     * 跟据id查询支付
     * @author Yuting Zhong@3333
     * @param paymentId
     * @return
     */
    public Payment getPaymentById(Long paymentId) {
        return paymentDao.getPaymentByID(paymentId);
    }



}
