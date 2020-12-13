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
            //若支付orderId不为空，则支付为订单支付，验证订单和店铺的关系；若支付aftersalId不为空，则支付为售后支付
            if(payment.getOrderId()!=null){
                //int checkBelong=orderService.checkShopOrder(shopId,payment.getOrderId());
                int checkBelong=1;

                if(checkBelong==0){
                  return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单不存在");
                }else if(checkBelong==-1){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该订单无权访问");
                }
            }else if(payment.getAftersaleId()!=null){
                //int checkBelong=aftersaleService.checkShopAftersale(shopId,payment.getAftersaleId();
                int checkBelong=1;

                if(checkBelong==0){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"售后单不存在");
                }else if(checkBelong==-1){
                    return retObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该售后单无权访问");
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
