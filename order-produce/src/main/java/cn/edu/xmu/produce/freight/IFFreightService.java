package cn.edu.xmu.produce.freight;

import java.util.List;
import java.util.Map;

public interface IFFreightService {

    /**
     * 获得运费模板概要
     * @param id
     * @return
     */
    public String getFreModelByModelId(Long id);

    /**
     * 计算运费
     * @param rid
     * @param goodsMap
     * @return
     */
    public Long calculateFreight(Long rid, Map<Long,Integer> goodsMap);

}
