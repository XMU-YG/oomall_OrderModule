package cn.edu.xmu.ooad.order.discount.impl;

import cn.edu.xmu.ooad.order.bo.COrderItem;
import cn.edu.xmu.ooad.order.discount.BaseCouponDiscount;
import cn.edu.xmu.ooad.order.discount.BaseCouponLimitation;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
public class PriceCouponDiscount extends BaseCouponDiscount {

	public PriceCouponDiscount(){}

	public PriceCouponDiscount(BaseCouponLimitation limitation, long value) {
		super(limitation, value);
	}

	@Override
	public void calcAndSetDiscount(List<COrderItem> COrderItems) {
		long total = 0L;
		for (COrderItem oi : COrderItems) {
			total += oi.getPrice() * oi.getQuantity();
		}

		for (COrderItem oi : COrderItems) {
			long discount = oi.getPrice() - (long) ((1.0 * oi.getQuantity() * oi.getPrice() / total) * value / oi.getQuantity());
			oi.setDiscount(discount);
		}
	}
}
