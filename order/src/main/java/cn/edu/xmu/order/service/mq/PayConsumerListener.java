//package cn.edu.xmu.order.service.mq;
//
//import cn.edu.xmu.ooad.util.JacksonUtil;
//import cn.edu.xmu.order.service.OrderService;
//import org.apache.rocketmq.spring.annotation.ConsumeMode;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
///**
// * 消息消费者
// **/
//@Service
//@RocketMQMessageListener(topic = "order-pay-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 100, consumerGroup = "pay-group")
//public class PayConsumerListener implements RocketMQListener<String> {
//    private static final Logger logger = LoggerFactory.getLogger(PayConsumerListener.class);
//
//    @Autowired
//    private OrderService orderService;
//
//    @Override
//    public void onMessage(String message) {
//        Long orderId = JacksonUtil.toObj(message, Long.class);
//        logger.info("onMessage: got message orderId =" + orderId +" time = "+ LocalDateTime.now());
//        orderService.checkOrderPayState(orderId);
//    }
//}
