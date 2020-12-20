package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order_provider.IOrderService;
import cn.edu.xmu.aftersale.dubbo.AftersaleService;
import cn.edu.xmu.user.dubbo.UserService;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.vo.NewPaymentVo;

import cn.edu.xmu.payment.util.PaymentPatterns;

import io.swagger.models.auth.In;
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

    @DubboReference(version = "0.0.1",check = false)
    private IOrderService orderService;

    @DubboReference(version = "0.0.1",check = false)
    private AftersaleService aftersaleService;

    @DubboReference(version = "0.0.1",check = false)
    private UserService userService;

    private Logger logger= LoggerFactory.getLogger(PaymentService.class);

    /**
     * 买家为订单创建支付
     * @pararm 订单id id
     * @author Yuting Zhong
     * Modified at 2020/12/9
     */
   public ReturnObject<VoObject> createOrderPayment(Long userId,Long orderId,NewPaymentVo vo){
       ReturnObject<VoObject> retObject=null;
       if(vo.getPrice().equals(0))
           retObject=new ReturnObject<>(ResponseCode.OK,"支付成功，支付金额为0，不创建支付记录");

       //String checkBelong=orderService.checkUserOrder(userId,orderId);
       String checkBelong="1";

       //校验用户和订单的从属关系  若订单不存在或订单不属于对应用户，则返回相应错误码，并直接返回给controller层
      if(checkBelong.equals("-1")){
          logger.debug("findOrderRefundShop: fail: 订单不存在");
           retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
       }else if(checkBelong.equals("0")){
          logger.debug("findOrderRefundShop: fail: 该订单无权限");
          retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权访问");
      }else if(checkBelong.equals("1")){
          if(vo.getPaymentPattern().equals(PaymentPatterns.REBATEPAY.getCode()))
          {
              //在用户模块查询返点够不够
             //boolean rebateEnough= userService.reduceRebate(userId,vo.getPrice());
              boolean rebateEnough=true;
/*
              if(!rebateEnough){
                  retObject=new ReturnObject<>(ResponseCode.REBATE_NOTENOUGH,"返点不足");
                  logger.debug("findOrderRefundShop: fail: 返点不足");
                  return retObject;
              }*/
          }

               //创建bo对象
               Payment payment=vo.createPayment();
               payment.setOrderId(orderId);

               //用bo对象payment在dao层创建po对象，dao层返回构造后的vo对象
               retObject=paymentDao.createPayment(payment);

//               if(retObject.getCode().equals(ResponseCode.OK))
//               {
//                   //修改订单状态
//                  // Long amount=orderService.getOrderAmount(orderId);
//
//                   if(!amount.equals(-1)&&orderPayed(orderId,amount))
//                   {
//                       orderService.changeOrderState(orderId,(byte)21);
//                   }
//               }

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

        if(vo.getPrice().equals(0))
            retObject=new ReturnObject<>(ResponseCode.OK,"支付成功，支付金额为0，不创建支付记录");

         Integer checkBelong=1;
        //Integer checkBelong = aftersaleService.checkUserAftersale(userId, aftersaleId);
        //校验用户和售后单的从属关系  若订单不存在或订单不属于对应用户，则返回相应错误码，并直接返回给controller层
        if (checkBelong.equals(-1)) {
            logger.debug("findOrderRefundShop: fail: 售后单不存在");
            retObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "售后单不存在");
        } else if (checkBelong.equals(0)) {
            logger.debug("findOrderRefundShop: fail: 该售后单无权限");
            retObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "该售后单无权访问");
        } else if (checkBelong.equals(1)) {

            if (vo.getPaymentPattern().equals(PaymentPatterns.REBATEPAY.getCode())) {
                //扣除用户返点
                //boolean rebateEnough = userService.reduceRebate(userId, vo.getPrice());
                 boolean rebateEnough=true;/*
                if (!rebateEnough) {
                    retObject = new ReturnObject<>(ResponseCode.REBATE_NOTENOUGH, "返点不足");
                    logger.debug("findOrderRefundShop: fail: 返点不足");
                }*/
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
        System.out.println(checkBelong);
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
       Integer checkBelong=aftersaleService.checkShopAftersale(shopId,aftersaleId);
        // Integer checkBelong=1;
        if(checkBelong.equals(-1)){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
            return returnObject;
        }else if(checkBelong.equals(0)){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
            return returnObject;
        }else if(checkBelong.equals(1)){
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
        //检查订单是否属于用户  check=1 属于 check=0 资源不存在 check=-1 不属于
         String checkBelong=orderService.checkUserOrder(userId,orderId);
        //  String checkBelong="1";

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
        Integer checkBelong=aftersaleService.checkUserAftersale(userId,aftersaleId);
        //Integer checkBelong=1;
        if(checkBelong.equals(-1)){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
            return returnObject;
        }else if(checkBelong.equals(0)){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
            return returnObject;
        }else if(checkBelong.equals(1)){
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

    /**
     * 查询订单的所有支付
     * @param pid
     * @return
     */
    public List<PaymentPo> findOrderPayment(Long pid) {
        List<PaymentPo> ret = paymentDao.findOrderPayment(pid);
        return ret;
    }

    /**
     * 查询订单是否支付完成
     * @param orderId 订单id
     * @param amount  订单总价
     * @return true订单支付完成  false订单支付未完成
     */
    public boolean orderPayed(Long orderId,Long amount){
        List<PaymentPo> paymentPos=paymentDao.findOrderPayment(orderId);
        if(paymentPos==null||paymentPos.isEmpty())
            return false;

        Long totalAmount=0L;
        for(PaymentPo po:paymentPos){
            totalAmount=totalAmount+po.getAmount();
        }
        if(amount.equals(totalAmount))
            return true;

        return false;
    }
}
