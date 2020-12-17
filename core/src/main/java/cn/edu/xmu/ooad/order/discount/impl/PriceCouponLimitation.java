package cn.edu.xmu.ooad.order.discount.impl;

import cn.edu.xmu.ooad.order.bo.COrderItem;
import cn.edu.xmu.ooad.order.discount.BaseCouponLimitation;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
public class PriceCouponLimitation extends BaseCouponLimitation {

	public PriceCouponLimitation() {

	}

	public PriceCouponLimitation(long value) {
		super(value);
	}

	@Override
	public boolean pass(List<COrderItem> COrderItems) {
		long t = 0;
		for (COrderItem oi : COrderItems) {
			t += oi.getQuantity() * oi.getPrice();
		}
		return t > value;
	}

}
