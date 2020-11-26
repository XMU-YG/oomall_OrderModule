package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;
import org.springframework.cglib.core.TinyBitSet;

import java.time.LocalDateTime;

@Data
public class OrderItem implements VoObject {
    private Long goods_sku_id;
    private Integer quantity;
    private Long order_id;
    private Long id;
    private Long price;
    private Long discount;
    private String name;
    private Long coupon_id;
    private Long coupon_activity_id;
    private Long be_share_id;
    private LocalDateTime gmt_created;
    private LocalDateTime gmt_modified;

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
