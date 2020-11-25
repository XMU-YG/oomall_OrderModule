package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.order.model.vo.NewOrderVo;
import cn.edu.xmu.order.service.OrderService;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Api(value = "商城订单服务", tags = "oomall")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/order", produces = "application/json;charset=UTF-8")
public class OrderController {
    private static final Logger logger= (Logger) LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

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
    @PostMapping("/order")
    public Object PostNewOrder(@PathVariable NewOrderVo newOrderVo){
        return Common.getRetObject(orderService.addNewOrder(newOrderVo));
    }




}
