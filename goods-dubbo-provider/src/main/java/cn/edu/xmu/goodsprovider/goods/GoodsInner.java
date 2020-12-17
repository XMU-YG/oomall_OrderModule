package cn.edu.xmu.goodsprovider.goods;


import java.util.List;

/**
 * 商品模块内部接口
 * @Author Pinzhen Chen
 * @Date 2020/12/11 13:42
 */
public interface GoodsInner {

    //1. 根据freightId删除相关商品的freight_id的值（设为null)
    boolean cleanFreightById(Long fid);

    //2. 根据skuId数组获取有关运费信息
    String getFreightInfoBySkuId(Long skuId);

    //3. 根据skuID找到sku对象,sku对象里面有所属spu信息
    String getSkuById(Long id);

    //6. 使用spuId查询List<skuId>,可以直接返回SpuRetVo，里面自带skuList
    String findSpuById(Long id);

    boolean  deductNorStock(Long skuId,Integer quantity);

//    //7. 根据spuId获取skuId
//    Long findSkuIdBySpuId(Long id);
}
