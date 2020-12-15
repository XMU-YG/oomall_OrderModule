package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrderItemPo;
import lombok.Data;
import org.springframework.cglib.core.TinyBitSet;

import java.time.LocalDateTime;

/**
 * OrderItem详细信息
 */
@Data
public class OrderItem implements VoObject {
    private Long id;
    private Long skuId;
    private Long orderId;
    private String name;
    private Integer quantity;
    private Long price;
    private Long discount;
    private Long couponActivityId;
    private Long beShareId;


    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
