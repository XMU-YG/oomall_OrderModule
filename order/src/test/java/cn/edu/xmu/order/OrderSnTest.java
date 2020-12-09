package cn.edu.xmu.order;

import cn.edu.xmu.order.dao.OrderDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderSnTest {

    @Autowired
    private OrderDao orderDao;
    @Test
    public void haveOrderSnTest(){
        String a="111111111111";
        String b="2016102361242";
        System.out.println(orderDao.haveOrderSn(a)+"  "+orderDao.haveOrderSn(b));
    }
}
