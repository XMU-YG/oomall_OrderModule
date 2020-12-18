package cn.edu.xmu.cart.dubbo;

import java.util.List;

public interface CartService {
    public void deductCart(List<Long> skuId, Long customerId);
}
