package cn.edu.xmu.aftersale.dubbo;


import java.time.LocalDateTime;

/**
 * 售后模块内部接口类
 * @author Jintai Wang
 * Created in 2020/12/12 20:00
 */
public interface AftersaleService {

    /**
     * 订单模块
     * 检查shop和aftersale的关系
     * 返回值：
     * 数据库错误  -2
     * 不存在     -1
     * 不属于     0
     * 属于       1
     * Created in 2020/12/12
     */
    public Integer checkShopAftersale(Long shopId,Long aftersaleId);

    /**
     * 订单模块
     * 检查user和aftersale的关系
     * 返回值：
     * 数据库错误  -2
     * 不存在     -1
     * 不属于     0
     * 属于       1
     * Created in 2020/12/12
     */
    public Integer checkUserAftersale(Long customerId,Long aftersaleId);

    /**
     * 分享模块
     * 根据orderItemId检查是否有售后记录
     * 返回值
     * 有 true
     * 无 false
     * Created in 2020/12/12
     */
    public Boolean getAftersaleByOrderItemId(Long orderItemId, LocalDateTime orderCompletedTime);
}
