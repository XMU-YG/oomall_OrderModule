package cn.edu.xmu.dubbo;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.produce.order.IOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
public class Test {

    @DubboReference(version ="1.0-SNAPSHOT")
    private IOrderService orderService;

    @GetMapping("orderItem")
    public Object test(){
        String s=orderService.getOrderItemById(1L);
        OrderItemPo orderItemPo= JacksonUtil.toObj(s, OrderItemPo.class);
        System.out.println(s);
        return new ReturnObject<>(orderItemPo);
    }

}
