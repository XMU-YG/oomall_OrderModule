package cn.edu.xmu.order.util;

public interface DeductStock {
    public boolean deductStock(Long skuId,Integer quantity);
}
