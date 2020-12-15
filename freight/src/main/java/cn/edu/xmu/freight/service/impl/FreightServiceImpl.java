package cn.edu.xmu.freight.service.impl;


import cn.edu.xmu.freight.model.vo.ItemsVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.produce.freight.IFFreightService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DubboService(version ="1.0-SNAPSHOT") // 注意这里的Serivce引用的是dubbo的包
public class FreightServiceImpl implements IFFreightService {
    @Autowired
    private FreightService freightService;


    @Override
    public String getFreModelByModelId(Long id)
    {
        return JacksonUtil.toJson(freightService.getFreModelByModelId(id));

    }

    @Override
    public Long calculateFreight(Long rid, Map<Long,Integer> goodsMap)
    {
        ItemsVo vo=new ItemsVo();
        List<ItemsVo> vos=new ArrayList<>();
        for(Map.Entry<Long,Integer> entry : goodsMap.entrySet())
        {
          vo.setSkuId(entry.getKey());
          vo.setCount(entry.getValue());
          vos.add(vo);
        }
        return freightService.calculateFreight(rid,vos);

    }

}
