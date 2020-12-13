package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.dao.RefundDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import cn.edu.xmu.payment.model.vo.PaymentVo;
import cn.edu.xmu.payment.util.PaymentStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    //@Autowired
   // private OrderService orderService;

   // @Autowired
   // private AftersaleService aftersaleService;
   @Autowired
    private PaymentService paymentService;

    /**
     * 管理员查看订单退款信息
     * @pararm 店铺id shopid
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    public ReturnObject<VoObject> findOrderRefundShop(Long shopId,Long orderId){
        //int check=aftersaleService.checkShopUser(shopId,aftersaleid);
        //check=1 属于 check=0 资源不存在 check=-1 不属于
        ReturnObject<VoObject> returnObject=null;
        int check=1;
        if(check==0){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
        }else if(check==-1){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
        }else if(check==1){
            returnObject=refundDao.findRefundByOrder(orderId);
        }
        return returnObject;
    }

    /**
     * 管理员查看售后单退款信息
     * @pararm 店铺id shopid
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    public ReturnObject<VoObject> findAftersaleRefundShop(Long shopId,Long aftersaleId){
        //int check=aftersaleService.checkShopAftersale(shopId,aftersaleid);
        //check=1 属于 check=0 资源不存在 check=-1 不属于
        ReturnObject<VoObject> returnObject=null;
        int check=1;
        if(check==0){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"售后单不存在");
        }else if(check==-1){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该售后单无权限");
        }else if(check==1){
            returnObject=refundDao.findRefundByAftersale(aftersaleId);
        }
        return returnObject;
    }
    /**
     * 买家查看自己的订单退款信息
     * @pararm 订单id id
     * @author Yuting Zhong@3333
     * Modified at 2020/12/2
     */
    public ReturnObject<VoObject> findOrderRefundSelf(Long userId,Long orderId){
        //int check=aftersaleService.checkUserOrder(userId,orderid);
        //check=1 属于 check=0 资源不存在 check=-1 不属于
        ReturnObject<VoObject> returnObject=null;
        int check=1;
        if(check==0){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
        }else if(check==-1){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权限");
        }else if(check==1){
            returnObject=refundDao.findRefundByOrder(orderId);
        }
        return returnObject;
    }

    /**
     * 买家查看自己的售后退款信息
     * @pararm 售后单id id
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    public ReturnObject<VoObject> findAftersaleRefundSelf(Long userId,Long aftersaleId){
        //int check=aftersaleService.checkUserAftersale(userId,aftersaleid);
        //check=1 属于 check=0 资源不存在 check=-1 不属于
        ReturnObject<VoObject> returnObject=null;
        int check=1;
        if(check==0){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"售后单不存在");
        }else if(check==-1){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该售后单无权限");
        }else if(check==1){
            returnObject=refundDao.findRefundByAftersale(aftersaleId);
        }
        return returnObject;
    }

    /**
     * 管理员创建退款
     * @author Yuting Zhong@3333
     * Modified at 2020/12/10
     */
    public ReturnObject<VoObject> createRefund(Long shopId, Long id, NewRefundVo vo) {
        ReturnObject<VoObject> retObject=null;

        Payment payment=paymentService.getPaymentById(id);

        if(payment==null){
            return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该支付不存在");
        }else if(payment.getAmount()<vo.getAmount()){
            return retObject=new ReturnObject<>(ResponseCode.REFUND_MORE,"退款金额大于支付金额");
        }else{
            ReturnObject<VoObject> checkBelong=null;

            if(payment.getOrderId()!=null){
                //checkBelong=orderService.checkShopOrder(shopId,payment.getOrderId());
               // checkBelong=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权访问");
                checkBelong=new ReturnObject<>(ResponseCode.OK,"该订单无权访问");
                if(!checkBelong.getCode().equals(ResponseCode.OK)){
                    return retObject=checkBelong;
                }
            }else if(payment.getAftersaleId()!=null){
                //checkBelong=aftersaleService.checkShopAftersale(shopId,payment.getAftersaleId());
                checkBelong=new ReturnObject<>(ResponseCode.OK,"该售后单无权访问");
                if(!checkBelong.getCode().equals(ResponseCode.OK)){
                    return retObject=checkBelong;
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
