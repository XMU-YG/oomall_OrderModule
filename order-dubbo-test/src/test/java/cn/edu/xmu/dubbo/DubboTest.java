package cn.edu.xmu.dubbo;

import cn.edu.xmu.produce.order.IOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootTest(classes = Application.class)
@EnableDubbo
@EnableDiscoveryClient

public class DubboTest {

    @DubboReference
    IOrderService orderService;
    @Test
    public void dubboTest(){
        System.out.println(orderService.getOrderItemById(1L));
    }

}
