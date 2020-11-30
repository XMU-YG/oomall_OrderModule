package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.vo.AddressVo;
import cn.edu.xmu.order.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@Api(value = "商城订单服务", tags = "oomall")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/order", produces = "application/json;charset=UTF-8")
public class OrderController {
    private  static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletResponse httpServletResponse;

//    @ApiOperation(value = "新增订单")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
//            @ApiImplicitParam(name="newOrderVo", value="订单信息", required = true, dataType="NewOrderVo", paramType="body")
//
//    })
//    @ApiResponses({
//            @ApiResponse(code = 0, message = "成功"),
//            @ApiResponse(code = 900, message = "商品库存不足")
//    })
//    //@Audit
//    @PostMapping("/orders")
//    public Object PostNewOrder(@PathVariable NewOrderVo newOrderVo){
//        return Common.getRetObject(orderService.addNewOrder(newOrderVo));
//    }

    /**
     * 分页查询顾客所有订单概要信息
     * @param orderSn 订单编号
     * @param state 订单状态
     * @param beginTime 开始时间（指confirm_time）
     * @param endTime 结束时间（指confirm_time）
     * @param page 页码
     * @param pageSize 页码大小
     * @return 订单概要视图（SimpleOrderInformation）
     * @author Gang Ye
     * @Create 2020/11/26
     * @Modify 2020/11/26
     */
    @ApiOperation(value = "查询顾客订单概要")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="orderSn", value="订单编号", required = true, dataType="String", paramType="query"),
            @ApiImplicitParam(name="state", value="订单状态", required = true, dataType="Integer", paramType="query"),
            @ApiImplicitParam(name="beginTime", value="订单开始时间", required = true, dataType="String", paramType="query"),
            @ApiImplicitParam(name="endTime", value="订单结束时间", required = true, dataType="String", paramType="query"),
            @ApiImplicitParam(name="page", value="页码", required = true, dataType="Integer", paramType="query"),
            @ApiImplicitParam(name="pageSize", value="分页大小", required = true, dataType="Integer", paramType="query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("orders")
    public Object getALLSimpleOrders(@LoginUser Long customerId, @RequestParam String orderSn, @RequestParam Integer state, @RequestParam String beginTime, @RequestParam String endTime, @RequestParam Integer page, @RequestParam Integer pageSize){
        Object ret=null;
        if (page<=0||pageSize<=0){
            ret=Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }else{
            ReturnObject<PageInfo<VoObject>> object=orderService.getAllSimpleOrders(customerId,orderSn,state,beginTime,endTime,page,pageSize);
            logger.debug("getAllSimpleOrders: orderSn : "+orderSn+"   state:  "+state);
            ret=Common.getPageRetObject(object);
        }
        return ret;
    }

    /**
     * 根据订单号查询顾客订单详情
     * @param customerId 顾客id
     * @param id 订单id
     * @return 订单详细信息
     * @author Gang Ye
     * @created 2020/11/27
     * @modified 2020/11/27 by Gang Ye
     */
    @ApiOperation(value = "根据订单号查询顾客订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="orderId", value="订单号", required = true, dataType="Integer", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),
    })
    @Audit
    @GetMapping("orders/{id}")
    public Object getSelfOrderById(@LoginUser Long customerId, @PathVariable Long id){
        Object ret=null;
        ReturnObject object=orderService.getOrderById(customerId,id);
        logger.debug("customer getOrderById: orderId : "+id+"   customerId:  "+customerId);
        if (object.getCode().equals(ResponseCode.OK)){
            ret=Common.getRetObject(object);
        }
        else{
            ret=ResponseUtil.fail(object.getCode(),object.getErrmsg());
        }
        return ret;
    }

    /**
     * 买家修改本人订单的收货信息，现在仅允许用户调用本 API 更改未发货订单的收货地址。
     * @param customerId
     * @param orderId
     * @param vo
     * @param bindingResult
     * @return
     * @author Gang Ye
     * @created 2020/11/29
     *
     */
    @ApiOperation(value = "买家修改本人订单地址信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="orderId", value="订单号", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="vo", value="收货信息", required = true, dataType="object", paramType="body"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "订单无权访问"),
            @ApiResponse(code = 800, message = "订单状态禁止"),
    })
    @Audit
    @PutMapping("orders/{id}")
    public Object modifySelfOrderAddressById(@LoginUser Long customerId, @PathVariable Long orderId, @RequestBody AddressVo vo, BindingResult bindingResult){
        logger.debug("modifySelfOrderAddressById:  customerId: "+customerId+"  orderId: "+orderId+"   vo:  "+vo.toString());
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        Object ret=null;
        ReturnObject object=orderService.modifySelfOrderAddressById(customerId,orderId,vo);
        if (object.getCode().equals(ResponseCode.OK)){
            ret=Common.decorateReturnObject(object);
        }
        else{
            ret=ResponseUtil.fail(object.getCode(),object.getErrmsg());
        }
        return ret;

    }

    /**
     * 用户本人调用本 API，只能取消，逻辑删除本人的订单
     * 发货前取消，完成后逻辑删除
     * @param customerId
     * @param orderId
     * @author Gang Ye
     * @created 2020/11/30
     */
    @ApiOperation(value = "逻辑删除顾客订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="orderId", value="订单号", required = true, dataType="Integer", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),

    })
    @Audit
    @DeleteMapping("orders/{id}")
    public Object deleteSelfOrderById(@LoginUser Long customerId,@PathVariable Long orderId){
        return orderService.deleteSelfOrderById(customerId,orderId);
    }

    /**
     * 客户对他本人的、状态为待收货的订单标记确认收货。
     * @param customerId
     * @param orderId
     * @return
     * @author Gang Ye
     * @created 2020/11/30
     */
    @ApiOperation(value = "买家标记确认收货")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="orderId", value="订单号", required = true, dataType="Integer", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),
    })
    @Audit
    @PutMapping("orders/{id}/confirm")
    public Object confirmSelfOrderById(@LoginUser Long customerId,@PathVariable Long orderId){
        return orderService.confirmSelfOrderById(customerId,orderId);
    }

}
