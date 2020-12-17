package cn.edu.xmu.order;

import cn.edu.xmu.order.model.bo.OrderItem;
import cn.edu.xmu.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.constraints.Max;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = OrderServiceApplication.class)
public class ServiceTest {

    @Autowired
    OrderService orderService;

    @Test
    public void classify() throws JsonProcessingException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        List<OrderItem> orderItemList=new ArrayList<>();
        for (long i = 0; i <10 ; i++) {
            OrderItem orderItem=new OrderItem();
            orderItem.setId(i);
            orderItem.setCouponActivityId(i%3);
            orderItemList.add(orderItem);
        }
        orderService.calculateDiscount(orderItemList);

    }
}
