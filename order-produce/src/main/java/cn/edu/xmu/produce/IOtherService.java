package cn.edu.xmu.produce;

import java.util.List;

/**
 * 订单所需其他模块接口
 * @author Gang Ye
 * @created 2020/12/11
 */
public interface IOtherService {

    /**
     * 获得该订单产生的返点数
     * @param orderItemPosJson orderItemPo转化的list
     * @param customerId 顾客id
     * @return 返点数
     */
    public Integer calculateRebateNum(String orderItemPosJson, Long customerId);

    /**
     * 获得顾客信息
     * @param customerId 顾客id
     * @return 顾客信息视图String
     *     private Long customerId;
     *     private String customerUserName;
     *     private String customerRealName;
     */
    public String findCustomerById(Long customerId);

    public Long getBeSharedId(Long skuId,Long customerId);
}
