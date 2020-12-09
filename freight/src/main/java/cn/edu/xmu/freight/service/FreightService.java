package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.FreightItem;
import cn.edu.xmu.freight.model.po.FreightPo;
import cn.edu.xmu.freight.model.vo.WeightItemVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FreightService {
    private Logger logger= LoggerFactory.getLogger(FreightService.class);

    @Autowired
    private FreightDao freightDao;

    @Transactional
    public ReturnObject<List> findFreightItemsById(Long shopId, Long id){
        return freightDao.findFreightItemsById(shopId,id);
    }

    @Transactional
    public ReturnObject<List> findPieceItemsById(Long shopId, Long id){
        return freightDao.findPieceItemsById(shopId,id);
    }

    @Transactional
    public ReturnObject deleteWeightItem(Long shopId, Long id) {
        return freightDao.deleteWeightItem(shopId,id);
    }

    @Transactional
    public ReturnObject deletePieceItem(Long shopId, Long id) {
        return freightDao.deletePieceItem(shopId,id);
    }

    @Transactional
    public ReturnObject<VoObject> createWeightItem(Long shopId, Long id, WeightItemVo vo) {

        //vo创建bo
        FreightItem freightItem=vo.createFreightItem();
        freightItem.setFreightModelId(id);
        freightItem.setGmtCreate(LocalDateTime.now());

        ReturnObject<FreightItem> ret=freightDao.createFreightItem(shopId,freightItem);

        ReturnObject<VoObject> retObj=null;
        if(ret.getCode().equals(ResponseCode.OK))
        {
            retObj=new ReturnObject<>(ret.getData());
        }
        else
        {
            retObj=new ReturnObject<>(ret.getCode(),ret.getErrmsg());
        }
        return retObj;
    }
}
