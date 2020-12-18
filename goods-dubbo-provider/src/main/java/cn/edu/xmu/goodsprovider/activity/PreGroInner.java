package cn.edu.xmu.goodsprovider.activity;

import java.util.List;

/**
 * @Author Pinzhen Chen
 * @Date 2020/12/11 22:00
 */
public interface PreGroInner {

    //1. 检查spuId是否在这个grouponId中
    boolean checkGrouponSkuId(Long gid, Long skuId);

    //2. 检查skuId是否在这个presaleId中
    boolean checkPresaleSkuId(Long pId, Long skuId);

    //3.获得商品的定金
    Long getAdvancePrice(Long presaleId, Long skuId);

    //4.获得商品的尾款
    Long getFinalPrice(Long presaleId, Long skuId);

    boolean  deductPreStock(Long actId, Long skuId, Integer quantity);

    boolean  deductGroStock(Long actId, Long skuId, Integer quantity);
}
