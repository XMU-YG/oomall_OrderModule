package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.factory.PostOrderFactory;
import cn.edu.xmu.order.model.vo.AddressVo;
import cn.edu.xmu.order.model.vo.NewOrderVo;
import cn.edu.xmu.order.service.OrderService;
import cn.edu.xmu.order.service.impl.PostOrderServiceImpl;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Api(value = "商城订单服务", tags = "oomall")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/order", produces = "application/json;charset=UTF-8")
public class OrderController {
    private  static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 新增订单
     * @param customerId
     * @param vo
     * @param bindingResult
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    @ApiOperation(value = "新增订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="orderInfo", value="订单信息", required = true, dataType="object", paramType="body")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 900, message = "商品库存不足")
    })
    @Audit
    @PostMapping("orders")
    public Object addNewOrderByCustomer(@ApiIgnore @LoginUser Long customerId, @Validated @RequestBody NewOrderVo vo,BindingResult bindingResult) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        Object ret=null;
        Class c= PostOrderFactory.createService(vo);
        Constructor a=c.getConstructor();
        PostOrderServiceImpl postOrderService=(PostOrderServiceImpl)a.newInstance();

        ReturnObject<VoObject> object=postOrderService.addNewOrderByCustomer(customerId,vo);

        if (object.getCode().equals(ResponseCode.OK)){
            ret=Common.getRetObject(object);
        }
        else{
            ret=Common.decorateReturnObject(object);
        }
        return ret;
    }

    /**
     * 分页查询顾客所有订单概要信息
     * @param orderSn 订单编号
     * @param state 订单状态
     * @param beginTime 开始时间（指create_time）
     * @param endTime 结束时间（指create_time）
     * @param page 页码
     * @param pageSize 页码大小
     * @return 订单概要视图（SimpleOrderRetVo）
     * @author Gang Ye
     * @Create 2020/11/26
     * @Modify 2020/12/8
     *          修改时间段查询
     */
    @ApiOperation(value = "查询顾客订单概要")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="orderSn", value="订单编号", required = false, dataType="String", paramType="query"),
            @ApiImplicitParam(name="state", value="订单状态", required = false, dataType="int", paramType="query"),
            @ApiImplicitParam(name="beginTime", value="订单开始时间", required = false, dataType="String", paramType="query"),
            @ApiImplicitParam(name="endTime", value="订单结束时间", required = false, dataType="String", paramType="query"),
            @ApiImplicitParam(name="page", value="页码", required = true, dataType="int", paramType="query"),
            @ApiImplicitParam(name="pageSize", value="分页大小", required = true, dataType="int", paramType="query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("orders")
    public Object getALLSimpleOrders(@ApiIgnore @LoginUser Long customerId, @RequestParam(required = false) String orderSn, @RequestParam(required = false) Integer state, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime, @RequestParam Integer page, @RequestParam Integer pageSize){

        Object ret=null;
        if (page<=0||pageSize<=0){
            ret=Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }else{
            LocalDateTime begin=null,end=null;
            if (beginTime!=null){
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                begin=LocalDateTime.parse(beginTime,dateTimeFormatter);
            }
            if (endTime!=null){
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                end=LocalDateTime.parse(endTime,dateTimeFormatter);
            }
            logger.debug("getAllSimpleOrders: customerId : "+customerId);
            ReturnObject<PageInfo<VoObject>> object=orderService.getAllSimpleOrders(customerId,orderSn,state,begin,end,page,pageSize);
            if (object.getCode().equals(ResponseCode.OK)){
                ret=Common.getPageRetObject(object);
            }
            else{
                ret=Common.getNullRetObj(new ReturnObject<>(object),httpServletResponse);
            }
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
            @ApiImplicitParam(name="id", value="订单号", required = true, dataType="int", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),
    })
    @Audit
    @GetMapping("orders/{id}")
    public Object getSelfOrderById(@ApiIgnore @LoginUser Long customerId, @PathVariable Long id){
        Object ret=null;
        ReturnObject<VoObject> object=orderService.getOrderById(customerId,id);
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
            @ApiImplicitParam(name="id", value="订单号", required = true, dataType="int", paramType="path"),
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
    public Object modifySelfOrderAddressById(@ApiIgnore @LoginUser Long customerId, @PathVariable(name = "id") Long orderId, @Validated @RequestBody AddressVo vo, BindingResult bindingResult){
        logger.debug("modifySelfOrderAddressById:  customerId: "+customerId+"  orderId: "+orderId+"   vo:  "+vo.toString());
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        Object ret=null;
        ReturnObject object=orderService.modifySelfOrderAddressById(customerId,orderId,vo);
        ret=Common.getNullRetObj(object,httpServletResponse);
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
            @ApiImplicitParam(name="id", value="订单号", required = true, dataType="int", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),

    })
    @Audit
    @DeleteMapping("orders/{id}")
    public Object deleteSelfOrderById(@ApiIgnore @LoginUser Long customerId,@PathVariable(name = "id") Long orderId){
        Object ret=null;
        ReturnObject object=orderService.deleteSelfOrderById(customerId,orderId);
        //System.out.println(object.getCode()+"  "+object.getErrmsg());
        ret=Common.getNullRetObj(object,httpServletResponse);
        return ret;
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
            @ApiImplicitParam(name="orderId", value="订单号", required = true, dataType="int", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),
    })
    @Audit
    @PutMapping("orders/{id}/confirm")
    public Object confirmSelfOrderById(@ApiIgnore @LoginUser Long customerId,@PathVariable Long orderId){
        Object ret=null;
        ReturnObject object=orderService.confirmSelfOrderById(customerId,orderId);
        ret=Common.getNullRetObj(object,httpServletResponse);
        return ret;
    }

    /**
     * 买家将团购订单转为普通订单
     * @param customerId
     * @param id
     * @return
     */
    @ApiOperation(value = "买家将团购订单转为普通订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="订单号", required = true, dataType="int", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 800, message = "订单状态禁止"),
    })
    @Audit
    @PostMapping("orders/{id}/groupon-normal")
    public Object translateGroToNor(@ApiIgnore @LoginUser Long customerId,@PathVariable Long id){
        Object ret=null;
        ReturnObject object=orderService.translateGroToNor(customerId,id);
        ret=Common.getNullRetObj(object,httpServletResponse);
        return ret;
    }

    /**
     * 卖家查询本店铺所有订单概要
     * @param shopId
     * @param customerId
     * @param orderSn
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return 订单概要视图
     * @author Gang Ye
     */
    @ApiOperation(value = "店家查询顾客订单概要")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="商店id", required = false, dataType="int", paramType="path"),
            @ApiImplicitParam(name="customerId", value="顾客ID", required = false, dataType="int", paramType="query"),
            @ApiImplicitParam(name="orderSn", value="订单编号", required = false, dataType="String", paramType="query"),
            @ApiImplicitParam(name="beginTime", value="订单开始时间", required = false, dataType="String", paramType="query"),
            @ApiImplicitParam(name="endTime", value="订单结束时间", required = false, dataType="String", paramType="query"),
            @ApiImplicitParam(name="page", value="页码", required = true, dataType="int", paramType="query"),
            @ApiImplicitParam(name="pageSize", value="分页大小", required = true, dataType="int", paramType="query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("shops/{shopId}/orders")
    public Object getShopSelfSimpleOrders(
            @ApiIgnore @PathVariable(name = "shopId") Long shopId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = true) Integer page,
            @RequestParam(required = true) Integer pageSize){

        logger.debug("getShopSelfSimpleOrders:  shopId:  "+shopId);
        ReturnObject<PageInfo<VoObject>> object=null;
        page= page==null? 1:page;
        pageSize=pageSize==null?10:pageSize;

        LocalDateTime begin=null,end=null;
        if (beginTime!=null){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            begin=LocalDateTime.parse(beginTime,dateTimeFormatter);
        }
        if (endTime!=null){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            end=LocalDateTime.parse(endTime,dateTimeFormatter);
        }
        object=orderService.getShopSelfSimpleOrders(shopId,customerId,orderSn,begin,end,page,pageSize);

        if (object.getCode().equals(ResponseCode.OK)){
            return Common.getPageRetObject(object);
        }
        else{
            return Common.getNullRetObj(new ReturnObject<>(object),httpServletResponse);
        }
    }

    /**
     * 卖家查询本店订单详情
     * @param shopId
     * @param id
     * @return 订单详情
     * @author Gang Ye
     */
    @ApiOperation(value = "店家查询本店订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺ID", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="订单ID", required = true, dataType="int", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),
    })
    //@Audit
    @GetMapping("shops/{shopId}/orders/{id}")
    public Object getShopSelfOrder(@PathVariable(name = "shopId") Long shopId, @PathVariable(name = "id") Long id){
        Object ret=null;

        ReturnObject<VoObject> object=orderService.getShopSelfOrder(shopId,id);
        logger.debug("shop getOrderById: orderId : "+id+"   shopId:  "+shopId);
        if (object.getCode().equals(ResponseCode.OK)){
            ret=Common.getRetObject(object);
        }
        else{
            ret=ResponseUtil.fail(object.getCode(),object.getErrmsg());
        }
        return ret;
    }

    /**
     * 卖家修改留言
     * @param shopId
     * @param orderId
     * @param message
     * @return
     * @author Gang Ye
     */
    @ApiOperation(value = "店家修改订单（留言）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺ID", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="订单ID", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="message", value="留言", required = false, dataType="object", paramType="body"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),
    })
    @Audit
    @PutMapping("shops/{shopId}/orders/{id}")
    public Object modifyOrderMessage(@PathVariable(name = "shopId") Long shopId,@PathVariable(name = "id") Long orderId,@RequestBody(required = false) String message){
        Object ret=null;
        if (message==null){
            ret=Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        else{
            ReturnObject object=orderService.modifyOrderMessage(shopId,orderId,message);
            ret=Common.getNullRetObj(object,httpServletResponse);
        }
        return ret;
    }

    /**
     * 卖家取消本店铺订单
     * @param shopId
     * @param orderId
     * @return
     * @author Gang Ye
     */
    @ApiOperation(value = "店铺取消本店铺订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="订单号", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="shopId", value="店铺", required = true, dataType="int", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),

    })
    @Audit
    @DeleteMapping("shops/{shopId}/orders/{id}")
    public Object deleteShopOrder(@PathVariable(name = "shopId") Long shopId,@PathVariable(name = "id") Long orderId){
        Object ret=null;
        ReturnObject object=orderService.deleteShopOrder(shopId,orderId);
        ret=Common.getNullRetObj(object,httpServletResponse);
        return ret;
    }

    /**
     * 卖家标记发货
     * @param shopId
     * @param orderId
     * @param freightSn
     * @return
     * @author Gang Ye
     */
    @ApiOperation(value = "店家标记订单发货")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺ID", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="订单ID", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="freightSn", value="运费单号", required = true, dataType="object", paramType="body"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "订单号不存在"),
            @ApiResponse(code = 505, message = "该订单无权访问"),
    })
    @Audit
    @PutMapping("shops/{shopId}/orders/{id}/deliver")
    public Object deliverShopOrder(@PathVariable(name = "shopId") Long shopId,@PathVariable(name = "id") Long orderId, @RequestBody String freightSn){
        Object ret=null;
        if (freightSn==null){
            ret=Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        else{
            ReturnObject object=orderService.deliverShopOrder(shopId,orderId,freightSn);
            ret=Common.getNullRetObj(object,httpServletResponse);
        }
        return ret;
    }

    @ApiOperation(value = "获得订单所有状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("orders/states")
    public Object getOrderAllStates(@ApiIgnore @LoginUser Long customerId){
        ReturnObject<List> object=null;
        Object ret=null;
        object=orderService.getOrderAllStates(customerId);
        logger.debug("customer get all order states.  customer: "+customerId);
        if (object.getCode().equals(ResponseCode.OK)){
            ret=Common.getListRetObject(object);
        }
        else{
            ret=Common.decorateReturnObject(object);
        }

        return ret;
    }

}
