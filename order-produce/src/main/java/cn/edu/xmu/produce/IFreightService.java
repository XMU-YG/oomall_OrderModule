package cn.edu.xmu.produce;

import java.util.Map;

public interface IFreightService {

    public Long calculateFreight(Long regionId, Map<Long,Integer> goods);
}
