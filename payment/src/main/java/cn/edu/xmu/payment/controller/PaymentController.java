package cn.edu.xmu.payment.controller;

import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.model.bo.NewRefund;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import cn.edu.xmu.payment.model.vo.PatternVo;
import cn.edu.xmu.payment.service.PaymentService;
import cn.edu.xmu.payment.service.RefundService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(value = "商城订单服务", tags = "oomall")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/payment", produces = "application/json;charset=UTF-8")
public class PaymentController {

    private  static  final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

    @Resource
    private HttpServletResponse httpServletResponse;

    /**
     * 管理员根据订单号查询支付信息
     * shopId用来检测，这里没有写到
     * @author BaekHyun
     * Modified at 2020/12/5
     **/
    @ApiOperation(value = "管理员查询订单的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="订单id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Auditid
    @GetMapping("shops/{shopId}/orders/{id}/payments")

    public Object admingetPaymentInfo(@PathVariable Long id, @PathVariable Long shopId){
        Object ret=null;
        ReturnObject<List> returnObject =  paymentService.admingetPaymentInfo(id, shopId);
        //这里的list是数据的类型
        logger.debug("Admin getPaymentInfo: orderId:"+id+"shopId:"+shopId);
        if(returnObject.getCode().equals(ResponseCode.OK)){
            ret=Common.getListRetObject(returnObject);
        }
        else{
            ret=ResponseUtil.fail(returnObject.getCode(),returnObject.getErrmsg());
        }
        return ret;
        
    }

    /**
     * 买家根据订单号查询支付信息
     *需要用customerId验证这个订单号是不是这个用户的
     * @author BaekHyun
     * Modified at 2020/12/3
     **/
    @ApiOperation(value = "买家查询自己的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="订单id", required = true, dataType="int", paramType="path",example="1"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @GetMapping("orders/{id}/payments")

    public Object getPaymentInfo(@LoginUser @ApiIgnore @RequestParam(required =false)  Long userId, @PathVariable  Long id){
        Object ret=null;

        ReturnObject<List> object=paymentService.getPaymentInfo(userId,id);

        logger.debug("customer getPaymentInfo: orderId:"+id);
        if(object.getCode().equals(ResponseCode.OK)){
            ret= Common.getListRetObject(object); //这里疑惑是要返回一个信息值还是一组信息值，下面管理员部分同理
        }
        else{
            ret= ResponseUtil.fail(object.getCode(),object.getErrmsg());
        }
        return ret;
    }
//
    /**
     * 买家根据售后单号查询支付信息
     *需要用customerId验证这个订单号是不是这个用户的  **这里暂时还没加
     * @author BaekHyun
     * Modified at 2020/12/8
     **/
    @ApiOperation(value = "买家查询自己的售后支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="售后单id", required = true, dataType="int", paramType="path",example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @GetMapping("aftersales/{id}/payments")

    public Object getAftersalePayment(@LoginUser @ApiIgnore @RequestParam(required =false)  Long userId, @PathVariable Long id){
        Object ret=null;
        ReturnObject<List> object=paymentService.getAftersalePayment(userId, id);

        logger.debug("customer getPaymentInfo: aftersaleId:"+id);
        if(object.getCode().equals(ResponseCode.OK)){
            ret= Common.getListRetObject(object); //这里疑惑是要返回一个信息值还是一组信息值，下面管理员部分同理
        }
        else{
            ret= ResponseUtil.fail(object.getCode(),object.getErrmsg());
        }

        return ret;
    }

    /**
     * 管理员根据售后订单号查询售后单支付信息
     * shopId用来检测，这里没有写到
     * @author BaekHyun
     * Modified at 2020/12/5
     **/
    @ApiOperation(value = "管理员查询订单的售后单支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="售后单id", required = true, dataType="int", paramType="path",example = "1"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @GetMapping("shops/{shopId}/aftersales/{id}/payments")
    public Object AdmingetAftersalePayment(@PathVariable(name="id") Long id){

        Object ret=null;
        ReturnObject<List> object=paymentService.admingetAftersalePayment(id);

        logger.debug("admin getPaymentInfo: aftersaleId:"+id);
        if(object.getCode().equals(ResponseCode.OK)){
            ret= Common.getListRetObject(object); //这里疑惑是要返回一个信息值还是一组信息值，下面管理员部分同理
        }
        else{
            ret= ResponseUtil.fail(object.getCode(),object.getErrmsg());
        }

        return ret;
    }

    /**
     * 获取支付渠道
     * 目前只返回 “001 返点支付”  “002 普通支付”
     * @author BaekHyun
     * Modified at 2020/12/9
     **/
    @ApiOperation(value = "获得支付渠道")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/payments/patterns")
    public Object getAllPatterns(){
        Payment.Pattern[] patterns= Payment.Pattern.class.getEnumConstants();
        List<PatternVo> patternVos=new ArrayList<PatternVo>();

        for(int i=0;i< patterns.length;i++){
            patternVos.add(new PatternVo(patterns[i]));
        }

        return ResponseUtil.ok(new ReturnObject<List>(patternVos).getData());
    }


    /**
     * 管理员创建退款信息，需要检查该payment是否为本商铺的payment
     *即时返回支付成功，生成paysn
     * @author BaekHyun
     * Modified at 2020/12/9
     */
    @ApiOperation(value="管理员创建退款信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType ="String",name="authorization",value="用户token",required = true),
            @ApiImplicitParam(paramType="path",dataType="int", name="id",value="支付id",required = true, example = "1"),
            @ApiImplicitParam(paramType = "path",dataType = "int",name="shopId",value="店铺id",required = true,example = "1"),
            @ApiImplicitParam(paramType="body",dataType = "NewRefundVo",name="body",value="退款金额",required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PostMapping("/shops/{shopId}/payments/{id}/refunds")
    public Object createRefund(@PathVariable(name = "id") Long id, @PathVariable(name = "shopId") Long shopId,
                               @Validated @RequestBody NewRefundVo body, BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }

        ReturnObject<VoObject> retObject = refundService.createRefund(shopId,id,body);
        if(retObject.getCode().equals(ResponseCode.OK)){
            returnObject=Common.getRetObject(retObject);
        }else{
            returnObject=Common.decorateReturnObject(retObject);
        }

        return returnObject;

    }




}
