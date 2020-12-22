package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.order_provider.IOrderService;
import cn.edu.xmu.aftersale.dubbo.AftersaleService;
import cn.edu.xmu.user.dubbo.UserService;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.dao.RefundDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.vo.NewRefundVo;

import cn.edu.xmu.payment.util.PaymentPatterns;
import com.sun.xml.bind.v2.runtime.output.SAXOutput;
import io.swagger.models.auth.In;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 退款服务
 * @author Yuting Zhong
 * Modified at 2020/12/2
 */
@Service
public class RefundService {
    private Logger logger=LoggerFactory.getLogger(RefundService.class);

    @Autowired
    private RefundDao refundDao;
    @Autowired
    private PaymentDao paymentDao;

    @DubboReference(version = "0.0.1",check = false)
    private IOrderService orderService;

    @DubboReference(version = "0.0.1",check = false)
    private AftersaleService aftersaleService;

    @DubboReference(version = "0.0.1",check = false)
    private UserService userService;

   @Autowired
    private PaymentService paymentService;

    /**
     * 管理员查看订单退款信息
     * @pararm 店铺id shopid
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    public ReturnObject findOrderRefundShop(Long shopId,Long orderId){


        //check=1 属于 check=0 不属于 check=-1 不存在
        String checkBelong=orderService.checkShopOrder(shopId,orderId);
        //String checkBelong="-1";

        if(checkBelong.equals("-1")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
            return returnObject;
        }else if(checkBelong.equals("0")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
            return returnObject;
        }else if(checkBelong.equals("1")){
            ReturnObject<List> ret=new ReturnObject<>(refundDao.findRefundByOrder(orderId));
            return ret;
        }
        return null;
    }

    /**
     * 管理员查看售后单退款信息
     * @pararm 店铺id shopid
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    public ReturnObject findAftersaleRefundShop(Long shopId,Long aftersaleId){
        //check=1 属于 check=0 不属于 check=-1 不存在
          Integer checkBelong=aftersaleService.checkShopAftersale(shopId,aftersaleId);
        //Integer checkBelong=-1;
        System.out.println("checkShopAftersale"+checkBelong);
        if(checkBelong.equals(-1)){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"售后单不存在");
            return returnObject;
        }else if(checkBelong.equals(0)){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该售后单无权限");
            return returnObject;
        }else if(checkBelong.equals(1)){
            ReturnObject<List> ret=new ReturnObject<>(refundDao.findRefundByAftersale(aftersaleId));
            return ret;
        }
        return null;
    }
    /**
     * 买家查看自己的订单退款信息
     * @pararm 订单id id
     * @author Yuting Zhong@3333
     * Modified at 2020/12/2
     */
    public ReturnObject findOrderRefundSelf(Long userId,Long orderId){
        //check=1 属于 check=0 不属于 check=-1 不存在
        String checkBelong=orderService.checkUserOrder(userId,orderId);

        // String checkBelong="0";

        if(checkBelong.equals("-1")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
            return returnObject;
        }else if(checkBelong.equals("0")){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
            return returnObject;
        }else if(checkBelong.equals("1")){
            ReturnObject<List> ret=new ReturnObject<>(refundDao.findRefundByOrder(orderId));
            return ret;
        }
        return null;
    }

    /**
     * 买家查看自己的售后退款信息
     * @pararm 售后单id id
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    public ReturnObject findAftersaleRefundSelf(Long userId,Long aftersaleId){
        //check=1 属于 check=0 不属于 check=-1 不存在
         Integer checkBelong=aftersaleService.checkUserAftersale(userId,aftersaleId);
       //  Integer checkBelong=1;

        if(checkBelong.equals(-1)){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"售后单不存在");
            return returnObject;
        }else if(checkBelong.equals(0)){
            ReturnObject<VoObject> returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该售后单无权限");
            return returnObject;
        }else if(checkBelong.equals(1)){
            ReturnObject<List> ret=new ReturnObject<>(refundDao.findRefundByAftersale(aftersaleId));
            return ret;
        }
        return null;
    }

    /**
     * 内部创建退款接口
     * @param shopId
     * @param paymentId
     * @param customerId
     * @param vo
     * @return
     */
    public ReturnObject<VoObject> create(Long shopId,Long paymentId,Long customerId,NewRefundVo vo){
        ReturnObject<VoObject> retObject=null;

        //跟据支付id获取支付
        Payment payment=paymentService.getPaymentById(paymentId);

        //若支付不存在 返回支付不存在  若退款金额大于支付金额 返回退款金额大于支付金额
        if(payment==null){
            return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该支付不存在");
        }else if(payment.getAmount()<vo.getAmount()){
            return retObject=new ReturnObject<>(ResponseCode.REFUND_MORE,"退款金额大于支付金额");
        }else{
            //判断支付是订单支付还是售后支付

            //订单支付
            if(payment.getOrderId()!=null){
                //校验订单和商店的关系  -1订单不存在  0订单不属于商店  1订单属于店铺
                 String checkBelong=orderService.checkShopOrder(shopId,payment.getOrderId());
                //String checkBelong="1";
                if(checkBelong.equals("-1")){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
                }else if(checkBelong.equals("0")){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权访问");
                }

               // 订单属于店铺  判断是否是返点支付  如果是返点支付  获取顾客id 添加用户返点  若新增失败  返回错误信息
                if(payment.getPaymentPattern().equals(PaymentPatterns.REBATEPAY.getCode())){

                    userService.reduceRebate(customerId,-payment.getActualAmount().longValue());

                }

            }else if(payment.getAftersaleId()!=null){//售后单支付
                //校验售后单和店铺的关系 -1售后单不存在  0售后单不属于店铺  1售后单属于店铺
                Integer checkBelong=aftersaleService.checkShopAftersale(shopId,payment.getAftersaleId());
                //String checkBelong="1";

                if(checkBelong.equals(-1)){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"售后单不存在");
                }else if(checkBelong.equals(0)){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该售后单无权访问");
                }

                //售后单属于店铺  获取顾客id 返还返点

                if(payment.getPaymentPattern().equals(PaymentPatterns.REBATEPAY.getCode())){

                    userService.reduceRebate(customerId,-payment.getActualAmount().longValue());

                }
            }
        }

        //用vo创建bo对象，将orderId和aftersaleid填入bo
        Refund refund=vo.createRefund();

        refund.setPaymentId(paymentId);
        refund.setAftersaleId(payment.getAftersaleId());
        refund.setOrderId(payment.getOrderId());

        //将bo传入dao
        retObject=refundDao.createRefund(refund);

        //拿到dao返回的vo
        return retObject;

    }
    /**
     * 管理员创建退款
     * @author Yuting Zhong@3333
     * Modified at 2020/12/10
     */
    public ReturnObject<VoObject> createRefund(Long shopId, Long id, NewRefundVo vo) {
        ReturnObject<VoObject> retObject=null;

        if(vo.getAmount()==0)
            return retObject=new ReturnObject<>(ResponseCode.OK,"退款金额为0，退款成功，但不产生退款记录");


        //跟据支付id获取支付
        Payment payment=paymentService.getPaymentById(id);

        //若支付不存在 返回支付不存在  若退款金额大于支付金额 返回退款金额大于支付金额
        if(payment==null){
            return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该支付不存在");
        }else if(payment.getAmount()<vo.getAmount()){
            return retObject=new ReturnObject<>(ResponseCode.REFUND_MORE,"退款金额大于支付金额");
        }else{
            //判断支付是订单支付还是售后支付

            //订单支付
            if(payment.getOrderId()!=null){
                //校验订单和商店的关系  -1订单不存在  0订单不属于商店  1订单属于店铺
                 String checkBelong=orderService.checkShopOrder(shopId,payment.getOrderId());
                // String checkBelong="1";
                if(checkBelong.equals("-1")){
                  return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
                }else if(checkBelong.equals("0")){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权访问");
                }

                //订单属于店铺  判断是否是返点支付  如果是返点支付  获取顾客id 添加用户返点  若新增失败  返回错误信息
                if(payment.getPaymentPattern().equals(PaymentPatterns.REBATEPAY.getCode())){
                    Long userId=orderService.getOrderUser(payment.getOrderId());

                 userService.reduceRebate(userId,-payment.getActualAmount());
                }

            }else if(payment.getAftersaleId()!=null){//售后单支付
                //校验售后单和店铺的关系 -1售后单不存在  0售后单不属于店铺  1售后单属于店铺
             Integer checkBelong=aftersaleService.checkShopAftersale(shopId,payment.getAftersaleId());
                //  String checkBelong="1";

                if(checkBelong.equals(-1)){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"售后单不存在");
                }else if(checkBelong.equals(0)){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该售后单无权访问");
                }

                //售后单属于店铺  获取顾客id 返还返点
                if(payment.getPaymentPattern().equals(PaymentPatterns.REBATEPAY.getCode())){
                    Long userId=aftersaleService.getAftersaleUser(payment.getAftersaleId());

                    userService.reduceRebate(userId,-payment.getAmount().longValue());
                }
            }
        }

        //用vo创建bo对象，将orderId和aftersaleid填入bo
        Refund refund=vo.createRefund();

        refund.setPaymentId(id);
        refund.setAftersaleId(payment.getAftersaleId());
        refund.setOrderId(payment.getOrderId());

        //将bo传入dao
        retObject=refundDao.createRefund(refund);

        //拿到dao返回的vo
        return retObject;
    }
}
