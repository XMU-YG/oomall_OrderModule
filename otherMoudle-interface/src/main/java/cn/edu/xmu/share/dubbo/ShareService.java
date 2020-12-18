package cn.edu.xmu.share.dubbo;

/**
* 分享模块外部接口
* @author: Zeyao Feng
* @date: Created in 2020/12/15 10:13
*/
public interface ShareService {

    /**
    * 更新返点
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 10:26
    */
    public void calcRebate(Long orderItemId, Long userId, Long price,
                           Long skuId, Integer quantity, Long beShareId);


    /**
    * 订单创建时，填写被分享项Id
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 10:30
    */
    public Long fillOrderItemByBeShare(Long skuId,Long userId);


    /**
    * 点击分享链接时，创建被分享项
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 10:31
    */
    public void createBeShare(Long userId, Long shareId, Long skuId);
}
