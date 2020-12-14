package cn.edu.xmu.produce.freight;

import java.util.Map;

/**
 * 订单所需运费模块接口
 * @author Gang Ye
 * @created 2020/12/11
 */
public interface IFreightService {

    public Long calculateFreight(Long regionId, Map<Long,Integer> goods);
}
