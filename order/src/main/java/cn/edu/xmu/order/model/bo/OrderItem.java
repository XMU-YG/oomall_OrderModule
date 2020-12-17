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

    public OrderItem(OrderItemPo orderItemPo){
        this.id=orderItemPo.getId();
        this.skuId=orderItemPo.getGoodsSkuId();
        this.beShareId=orderItemPo.getBeShareId();
        this.couponActivityId=orderItemPo.getCouponActivityId();
        this.discount=orderItemPo.getDiscount();
        this.name=orderItemPo.getName();
        this.orderId=orderItemPo.getOrderId();
        this.price=orderItemPo.getPrice();
        this.quantity=orderItemPo.getQuantity();
    }

    public OrderItem() {

    }


    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
