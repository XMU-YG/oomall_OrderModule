package cn.edu.xmu.freight.service.impl;


import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.FreightItem;
import cn.edu.xmu.freight.model.po.WeightFreightPo;
import cn.edu.xmu.freight.model.vo.FreightModelVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.produce.IFreightService;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
@DubboService(version ="1.0-SNAPSHOT") // 注意这里的Serivce引用的是dubbo的包
public class FreightServiceImpl implements IFreightService{
    @Autowired
    private FreightService freightService;


    @Override
    public String getFreModelByModelId(Long id)
    {
        return JacksonUtil.toJson(freightService.getFreModelByModelId(id));

    }


}
