package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrderItemPo;

public class SimpleOrderItem implements VoObject {
    private Long goods_sku_id;
    private Integer quantity;
    private Long order_id;
    private Long price;
    private Long discount;
    private String name;
    private Long coupon_activity_id;
    private Long be_share_id;

    /**
     * 由po构造简单bo
     * @param po
     */
    public  SimpleOrderItem(OrderItemPo po){
        this.goods_sku_id=po.getGoodsSkuId();
        this.quantity=po.getQuantity();
        this.order_id=po.getOrderId();
        this.price=po.getPrice();
        this.discount=po.getDiscount();
        this.name=po.getName();
        this.coupon_activity_id=po.getCouponActivityId();
        this.be_share_id=po.getBeShareId();
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
