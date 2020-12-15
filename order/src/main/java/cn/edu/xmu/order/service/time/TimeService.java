package cn.edu.xmu.order.service.time;

import cn.edu.xmu.timer.client.TimerService;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Task;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Component("TimeService")
@Service
public class TimeService {

    private int paymentTime=10;

    private int receiveTime=10;
    @DubboReference
    private TimerService timerService;

    /**
     * 支付定时任务
     * @param customerId
     * @param orderId
     * @author Gang Ye
     */
    public void createPayTask(Long customerId,Long orderId){
        Task task=new Task();
        List<Param> params=new ArrayList<>();
        Param param=new Param();

        param.setSeq(1);
        param.setTypeName("Long");
        param.setParamValue(customerId.toString());
        params.add(param);

        param.setSeq(2);
        param.setTypeName("Long");
        param.setParamValue(orderId.toString());
        params.add(param);

        task.setParamList(params);
        task.setBeanName("cn.edu.xmu.order.service.OrderService");
        task.setMethodName("checkOrderPayState");
        task.setSenderName("order");
        task.setTopic("pay-group");
        task.setBeginTime(LocalDateTime.now().plusSeconds(paymentTime));
        timerService.createTask(task,0);
    }
    /**
     * 返点定时任务
     * @author Gang Ye
     */
    public void createRebateTask(){
        Task task=new Task();
        task.setBeanName("cn.edu.xmu.order.service.OrderService");
        task.setMethodName("checkOrderRebate");
        task.setSenderName("order");
        task.setTopic("rebate-group");
        task.setBeginTime(LocalDateTime.now());
        timerService.createTask(task,1);
        timerService.createTask(task,2);
        timerService.createTask(task,3);
        timerService.createTask(task,4);
        timerService.createTask(task,5);
        timerService.createTask(task,6);
        timerService.createTask(task,7);

    }
}
