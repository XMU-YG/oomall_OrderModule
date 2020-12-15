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

}
