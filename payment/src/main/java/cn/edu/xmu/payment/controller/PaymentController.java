package cn.edu.xmu.payment.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.vo.NewPaymentVo;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import cn.edu.xmu.payment.model.vo.PatternVo;
import cn.edu.xmu.payment.model.vo.StateVo;
import cn.edu.xmu.payment.service.PaymentService;
import cn.edu.xmu.payment.service.RefundService;
import cn.edu.xmu.payment.util.PaymentPatterns;
import cn.edu.xmu.payment.util.PaymentStates;
import io.swagger.annotations.*;
import net.bytebuddy.asm.Advice;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Api(value="支付服务",tags="payment")
@RestController /*Restful的Controller对象*/
@RequestMapping(value="/payment",produces="application/json;charset=UTF-8")
public class PaymentController {

    private  static  final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得支付单所有状态
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    @ApiOperation(value="获得订单所有状态",produces = "application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功")
    })
    @GetMapping("/payments/states")
    public Object getAllPaymentStates(){
        logger.debug("getAllPaymentStates: ");

        PaymentStates[] paymentStates=PaymentStates.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<>(paymentStates.length);
        for(PaymentStates paymentState:paymentStates){
            stateVos.add(new StateVo(paymentState));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /**
     * 获取支付渠道
     * @author Xuwen Chen  Yuting Zhong@3333
     * Modified at 2020/12/9
     **/
    @ApiOperation(value="获得支付渠道",produces = "application/json")
    @ApiImplicitParams({

    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功")
    })
    @GetMapping("/payments/patterns")
    public Object getAllPaymentPattern(){
        logger.debug("getAllPaymentPattern: ");

        PaymentPatterns[] paymentPatterns=PaymentPatterns.class.getEnumConstants();
        List<PatternVo> patternVos=new ArrayList<>(paymentPatterns.length);
        for(PaymentPatterns paymentPattern:paymentPatterns){
            patternVos.add(new PatternVo(paymentPattern));
        }
        return ResponseUtil.ok(new ReturnObject<List>(patternVos).getData());
    }

    /**
     * 买家为订单创建支付
     * @author Yuting Zhong@3333
     * Modified at 2020/12/9
     */
    @ApiOperation(value = "买家为订单创建支付",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="header",dataType="String",name="authorization",value="用户token",required=true),
            @ApiImplicitParam(paramType="path",dataType="int",name="id",value="订单id",required=true,example="1"),
            @ApiImplicitParam(paramType = "body", dataType = "NewPaymentVo", name = "vo", value = "创建支付内容", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "订单不存在"),
    })
    @Audit
    @PostMapping("/orders/{id}/payments")
    public Object createOrderPayment(@ApiIgnore @LoginUser Long userId,
                                         @Validated @RequestBody NewPaymentVo vo, BindingResult bindingResult,
                                         @PathVariable Long id){
        logger.debug("insert order payment: orderid: "+id);

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug(" fail: validate fail");
            return returnObject;
        }

        //service层做其他校验
        Object retObject=null;
        ReturnObject payment=paymentService.createOrderPayment(userId,id,vo);

        if(payment.getCode().equals(ResponseCode.OK)){
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            retObject=Common.getRetObject(payment);
        }else{
            retObject=Common.decorateReturnObject(payment);
        }

        return retObject;
    }
    /**
     * 买家为售后单创建支付
     * @author Yuting Zhong@3333
     * Modified at 2020/12/9
     */
    @ApiOperation(value = "买家为售后单创建支付",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="header",dataType="String",name="authorization",value="用户token",required=true),
            @ApiImplicitParam(paramType="path",dataType="int",name="id",value="售后单id",required=true,example="1"),
            @ApiImplicitParam(paramType = "body", dataType = "NewPaymentVo", name = "vo", value = "创建支付内容", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "售后单不存在"),
    })
    @Audit
    @PostMapping("/aftersales/{id}/payments")
    public Object createAftersalePayment(@ApiIgnore @LoginUser Long userId,
                                     @Validated @RequestBody NewPaymentVo vo, BindingResult bindingResult,
                                     @PathVariable Long id){
        logger.debug("insert aftersale payment: orderid: "+id);

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug(" fail: validate fail");
            return returnObject;
        }

        //service层做其他校验
        Object retObject=null;
        ReturnObject payment=paymentService.createAftersalePayment(userId,id,vo);

        if(payment.getCode().equals(ResponseCode.OK)){
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            retObject=Common.getRetObject(payment);
        }else{
            retObject=Common.decorateReturnObject(payment);
        }

        return retObject;
    }


    /**
     * 管理员查询订单支付信息
     * @author Xuwen Chen
     */
    @ApiOperation(value = "管理员查询订单的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="订单id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "订单不存在"),
            @ApiResponse(code=505,message = "该订单无权访问"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/orders/{id}/payments")
    public Object getOrderPaymentShop(@PathVariable Long id, @PathVariable Long shopId){
        Object ret=null;

        ReturnObject returnObject =  paymentService.findOrderPaymentShop(id, shopId);

        if(returnObject.getCode().equals(ResponseCode.OK)){
            ret=Common.getListRetObject(returnObject);
        }
        else{
            ret=Common.decorateReturnObject(returnObject);
        }
        return ret;

    }

    /**
     * 管理员查询售后单支付信息
     * @author Xuwen Chen
     */
    @ApiOperation(value = "管理员查询售后单的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="售后单id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "售后单不存在"),
            @ApiResponse(code=505,message = "该售后单无权访问"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/aftersales/{id}/payments")

    public Object getAftersalePaymentShop(@PathVariable Long id, @PathVariable Long shopId){
        Object ret=null;

        ReturnObject returnObject =  paymentService.findAftersalePaymentShop(id, shopId);

        if(returnObject.getCode().equals(ResponseCode.OK)){
            ret=Common.getListRetObject(returnObject);
        }
        else{
            ret=Common.decorateReturnObject(returnObject);
        }
        return ret;

    }

    /**
     * 买家根据订单号查询支付信息
     * @author Xuwen Chen
     * Modified at 2020/12/3
     **/
    @ApiOperation(value = "买家查询自己的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="订单id", required = true, dataType="int", paramType="path",example="1"),
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "订单不存在"),
            @ApiResponse(code=505,message = "该订单无权访问"),
    })
    @Audit
    @GetMapping("/orders/{id}/payments")
    public Object getOrderPaymentSelf(@ApiIgnore @LoginUser  Long userId, @PathVariable  Long id){
        Object ret=null;

        ReturnObject object=paymentService.findOrderPaymentSelf(userId,id);

        if(object.getCode().equals(ResponseCode.OK)){
            ret= Common.getListRetObject(object);
        }
        else{
            ret= Common.decorateReturnObject(object);
        }
        return ret;
    }

    /**
     * 买家根据售后单查询支付信息
     * @author Xuwen Chen
     * Modified at 2020/12/3
     **/
    @ApiOperation(value = "买家查询自己售后单的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="售后单id", required = true, dataType="int", paramType="path",example="1"),
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "售后单不存在"),
            @ApiResponse(code=505,message = "该售后单无权访问"),
    })
    @Audit
    @GetMapping("/aftersales/{id}/payments")
    public Object getAftersalePaymentSelf(@ApiIgnore @LoginUser   Long userId, @PathVariable  Long id){
        Object ret=null;

        ReturnObject object=paymentService.findAftersalePaymentSelf(userId,id);

        if(object.getCode().equals(ResponseCode.OK)){
            ret= Common.getListRetObject(object);
        }
        else{
            ret=  Common.decorateReturnObject(object);
        }
        return ret;
    }

    /**
     * 管理员创建退款信息
     * @author Xuwen Chen
     * Modified at 2020/12/10
     */
    @ApiOperation(value="管理员创建退款")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="header",dataType="String",name="authorization",value="用户token",required=true),
            @ApiImplicitParam(paramType="path",dataType="int",name="shopId",value="店铺id",required=true,example="1"),
            @ApiImplicitParam(paramType="path",dataType="int",name="id",value="支付id",required=true,example="1"),
            @ApiImplicitParam(paramType = "body", dataType = "NewRefundVo", name = "vo", value = "创建退款内容", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "退款不存在"),
            @ApiResponse(code=505,message = "该退款无权访问")
    })
    @Audit
    @PostMapping("/shops/{shopId}/payments/{id}/refunds")
    public Object createRefund(@LoginUser @ApiIgnore Long userId,
                                         @Validated @RequestBody NewRefundVo vo, BindingResult bindingResult,
                                         @PathVariable Long shopId, @PathVariable Long id){
        logger.debug("insert aftersale payment orderid: "+id);

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug(" fail: validate fail");
            return returnObject;
        }

        //service层做其他校验
        Object retObject=null;
        ReturnObject refund=refundService.createRefund(shopId,id,vo);

        if(refund.getCode().equals(ResponseCode.OK)){
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            retObject=Common.getRetObject(refund);
        }else{
            retObject=Common.decorateReturnObject(refund);
        }

        return retObject;
    }


    /**
     * 管理员查询订单退款信息
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    @ApiOperation(value="管理员查看订单退款信息",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="header",dataType="String",name="authorization",value="用户token",required=true),
            @ApiImplicitParam(paramType="path",dataType="int",name="shopId",value="店铺id",required=true,example="1"),
            @ApiImplicitParam(paramType="path",dataType="int",name="id",value="订单id",required=true,example="1"),
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "订单不存在"),
            @ApiResponse(code=505,message = "该订单无权访问"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/orders/{id}/refunds")
    public Object getOrderRefundShop(@PathVariable Long shopId,@PathVariable Long id){
        logger.debug("getOrderRefundShop shopid: "+shopId+" orderid"+id);

        Object returnObject=null;

        ReturnObject refund=refundService.findOrderRefundShop(shopId,id);

        if(refund.getCode().equals(ResponseCode.OK)){
            returnObject=Common.getListRetObject(refund);
        }else{
            returnObject=Common.decorateReturnObject(refund);
        }

        return returnObject;
    }

    /**
     * 管理员查看售后单退款信息
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    @ApiOperation(value="管理员查看售后单退款信息",produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="header",dataType="String",name="authorization",value="用户token",required=true),
            @ApiImplicitParam(paramType="path",dataType="int",name="shopId",value="店铺id",required=true,example="1"),
            @ApiImplicitParam(paramType="path",dataType="int",name="id",value="售后单id",required=true,example="1"),
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "售后单不存在"),
            @ApiResponse(code=505,message = "该售后单无权访问"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/aftersales/{id}/refunds")
    public Object getAftersaleRefundShop(@PathVariable Long shopId,@PathVariable Long id){
        logger.debug("getAftersaleRefundShop shopid: "+shopId+" afterid: "+id);
        //System.out.println("shopId"+shopId+" aftersaleId"+id);
        Object returnObject=null;

        ReturnObject refund=refundService.findAftersaleRefundShop(shopId,id);

        if(refund.getCode().equals(ResponseCode.OK)){
            returnObject=Common.getListRetObject(refund);
            System.out.println(refund.getData());
        }else{
            returnObject=Common.decorateReturnObject(refund);
        }

        return returnObject;
    }

    /**
     *买家查询自己的订单退款信息
     * @author Yuting Zhong@3333
     * Modified at 2020/12/5
     */
    @ApiOperation(value="买家查询自己的订单退款信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="header",dataType="String",name="authorization",value="用户token",required=true),
            @ApiImplicitParam(paramType="path",dataType="int",name="id",value="订单id",required=true,example="1"),

    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "订单不存在"),
            @ApiResponse(code=505,message = "该订单无权访问"),
    })
    @Audit
    @GetMapping("/orders/{id}/refunds")
    public Object getOrderRefundSelf(@ApiIgnore @LoginUser Long userId,
                                     @PathVariable Long id){
        logger.debug("getOrderRefund: userid: "+userId+" orderId: "+id);

        Object returnObject;

        ReturnObject refund=refundService.findOrderRefundSelf(userId,id);

        if(refund.getCode().equals(ResponseCode.OK)){
            returnObject=Common.getListRetObject(refund);
        }else{
            returnObject=Common.decorateReturnObject(refund);
        }

        return returnObject;
    }

    /**
     * 买家查询自己的售后单退款信息
     * @author Yuting Zhong@3333
     * Modified at 2020/12/6
     */
    @ApiOperation(value="买家查询自己的售后单退款信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType="path",dataType="Long",name="id",value="售后单id",required=true,example="1"),
    })
    @ApiResponses({
            @ApiResponse(code=0,message="成功"),
            @ApiResponse(code=504,message = "售后单不存在"),
            @ApiResponse(code=505,message = "该售后单无权访问"),
    })
    @Audit
    @GetMapping("/aftersales/{id}/refunds")
    public Object getAftersaleRefundSelf(
            @ApiIgnore @LoginUser Long userId,
            @ApiIgnore @Depart Long dId,
            @PathVariable(name="id") Long id){
        logger.debug("getAftersaleRefundSelf: userid"+userId+" aftersaleId: "+id);

        Object returnObject;

        ReturnObject refund=refundService.findAftersaleRefundSelf(userId,id);

        if(refund.getCode().equals(ResponseCode.OK)){
            returnObject=Common.getListRetObject(refund);
        }else{
            returnObject=Common.decorateReturnObject(refund);
        }

        return returnObject;
    }
}

