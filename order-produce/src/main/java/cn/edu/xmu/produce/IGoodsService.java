package cn.edu.xmu.produce;

import java.util.List;

/**
 * 订单所需的商品模块接口
 * @author Gang Ye
 * @created 2020/12/11
 */
public interface IGoodsService {

    /**
     * 获得商品信息
     * @param SkuId
     * @return 由商品信息视图的json转化的String
     *     private Long goods_sku_id;
     *     private Integer quantity;
     *     private Long price;
     *     private String name;
     *     private boolean isSeckill;
     */
    public String findGoodsBySkuId(Long SkuId);

    /**
     * 根据店铺Id获得店铺信息
     * @param id 店铺ID
     * @return 店铺信息转化的String
     *     private Long shopId;
     *     private String shopName;
     *     private Byte state;
     *     private LocalDateTime shopGmtCreate;
     *     private LocalDateTime shopGmtModified;
     */
    public String findShopById(Long id);

    public boolean checkPresaleSkuId(Long presaleId, Long skuId);

    public boolean checkGrouponSkuId(Long grouponId,Long skuId);

    public Long calculateGrouponDiscount(Long grouponId,Long skuId);

    public boolean isSeckillGoods(Long skuId);

    /**
     * 扣库存
     * @param skuId 商品Id
     * @param quantity 扣除数量，为负表示库存加回
     * @param type 扣除商品类型 0普通 1团购 2预售
     * @return 扣成功 true，库存不足 false
     */
    public boolean deductStock(Long skuId,Integer quantity,Byte type);

    public boolean deductNorStock(Long skuId,Integer quantity);
    public boolean deductPreStock(Long skuId,Integer quantity);
    public boolean deductGroStock(Long skuId,Integer quantity);

    /**
     * 获得商品的定金
     * @param presaleId 预售活动id
     * @param skuId 商品skuId
     * @return 定金
     */
    public Long getAdvancePrice(Long presaleId,Long skuId);

    /**
     * 获得商品的尾款
     * @param presaleId 预售活动id
     * @param skuId 商品skuId
     * @return 尾款
     */
    public Long getFinalPrice(Long presaleId,Long skuId);

    /**
     * 根据skuid得到shopid
     * @param skuId skuid
     * @return shopid
     */
    public Long findShopBySkuId(Long skuId);

}
