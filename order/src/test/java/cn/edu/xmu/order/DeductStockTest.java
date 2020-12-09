package cn.edu.xmu.order;

import cn.edu.xmu.order.dao.OrderDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OrderServiceApplication.class)
public class DeductStockTest extends Thread{

    @Autowired
    private OrderDao orderDao;

    int a;

    @Test
    public void deductStockTest(){
        a=orderDao.deductStock(11l,11);
        while(a++<10000){
            Thread thread=new Thread(()->{
                int s=a;
                System.out.println("this is: "+s);
                orderDao.deductStock(11l,3);
            });
            thread.start();
        }

        System.out.println(a);

    }
}
