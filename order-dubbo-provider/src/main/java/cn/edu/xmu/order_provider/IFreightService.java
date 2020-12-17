package cn.edu.xmu.order_provider;

import java.util.Map;

public interface IFreightService {

    /**
     * 获得运费模板概要
     * @param id
     * @return
     */
    public String getFreModelByModelId(Long id);

    public Long calculateFreight(Long rid, Map<Long,Integer> goodsMap);

}
