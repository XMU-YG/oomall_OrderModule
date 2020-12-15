package cn.edu.xmu.produce.other;

/**
 * 支付所需其他模块接口
 * @author Yuting Zhong
 * @Create 2020/12/13
 */
public interface IPOtherService {
    /**
     * 检验用户和售后单所属关系
     * @param userId
     * @param aftersaleId
     * @return 1：售后单属于用户  0：售后单不属于该用户  -1：售后单不存在  -2：数据库错误
     */
    public String checkUserAftersale(Long userId,Long aftersaleId);

    /**
     * 检查店铺和售后单所属关系
     * @param shopId
     * @param aftersaleId
     * @return 1：售后单属于店铺  0：售后单不属于店铺  -1：售后单不存在  -2：数据库错误
     */
    public String checkShopAftersale(Long shopId,Long aftersaleId);

    /**
     * 扣除用户返点
     * @param userId
     * @param rebate
     * @return true扣除成功  false扣除失败
     */
    public boolean reduceRebate(Long userId,Long rebate);
}
