package cn.edu.xmu.order.service;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

//@Service
//public class RocketMQService {
//
////    private static final Logger logger = LoggerFactory.getLogger(RocketMQService.class);
////    @Value("${orderservice.order-pay-topic.delay-level}")
////    private int payTimeout;
////
////    @Value("${orderservice.order-pay-topic.timeout}")
////    private int timeout;
////
////    @Resource
////    private RocketMQTemplate rocketMQTemplate;
////
////    public void sendOrderPayMessage(Long orderId){
////        logger.info("sendOrderPayMessage: send message orderId = "+orderId+" delay ="+payTimeout+" time =" + LocalDateTime.now());
////        rocketMQTemplate.asyncSend("order-pay-topic", MessageBuilder.withPayload(orderId.toString()).build(), new SendCallback() {
////            @Override
////            public void onSuccess(SendResult sendResult) {
////                logger.info("sendOrderPayMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
////            }
////
////            @Override
////            public void onException(Throwable throwable) {
////                logger.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
////            }
////        }, timeout * 1000, payTimeout);
////    }
//
//}
