package cn.edu.xmu.produce.freight;

import java.util.List;
public interface IFFreightService {

    /**
     * 获得运费模板概要
     * @param id
     * @return
     */
    public String getFreModelByModelId(Long id);

    public Long calculateFreight(Long rid,String json);

}
