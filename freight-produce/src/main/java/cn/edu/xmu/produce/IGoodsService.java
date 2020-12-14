package cn.edu.xmu.produce;


/**
 * 运费所需的商品模块接口
 */
public interface IGoodsService {

    /**
     * 获得商品的weight,freightId,shopId
     */
    public String getGoodsInfoBySkuIds(Long skuId);

    /**
     * 清空商品关联的运费模板
     * @param freightId
     * @return
     */
    public boolean cleanFreightIdById (Long freightId);
}
