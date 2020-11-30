package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.vo.NewOrderVo;
import cn.edu.xmu.order.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @ApiOperation(value = "新增订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="newOrderVo", value="订单信息", required = true, dataType="NewOrderVo", paramType="body")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 900, message = "商品库存不足")
    })
    //@Audit
    @PostMapping("/orders")
    public Object PostNewOrder(@PathVariable NewOrderVo newOrderVo){
        return Common.getRetObject(orderService.addNewOrder(newOrderVo));
    }

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
    @GetMapping("/orders")
    public Object getAllSimpleOrders(
            @RequestParam String orderSn,
            @RequestParam Integer state,
            @RequestParam String ime,
            @RequestParam String endTime,
            @RequestParam Integer page,
            @RequestParam Integer pageSize){
        Object ret=null;
        if (page<=0||pageSize<=0){
            ret=Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }else{
            ReturnObject<PageInfo<VoObject>> object=orderService.getAllSimpleOrders(orderSn,state,beginTime,endTime,page,pageSize);
            logger.debug("getAllSimpleOrders: orderSn : "+orderSn+"   state:  "+state);
            ret=Common.getPageRetObject(object);
        }
        return ret;
    }

}
