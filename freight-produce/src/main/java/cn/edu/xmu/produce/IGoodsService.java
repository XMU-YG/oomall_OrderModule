package cn.edu.xmu.produce;


/**
 * 运费所需的商品模块接口
 */
public interface IGoodsService {

    /**
     * 获得商品的weight,freightId,shopId
     */
    public String getGoodsInfoBySkuIds(Long skuId);

}
