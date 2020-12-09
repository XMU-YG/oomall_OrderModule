package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.FreightItem;
import cn.edu.xmu.freight.model.bo.PieceItem;
import cn.edu.xmu.freight.model.po.FreightPo;
import cn.edu.xmu.freight.model.po.PieceFreightPo;
import cn.edu.xmu.freight.model.po.WeightFreightPo;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class FreightService {
    private Logger logger= LoggerFactory.getLogger(FreightService.class);

    @Autowired
    private FreightDao freightDao;

    @Transactional
    public ReturnObject<List> findFreightItemsById(Long shopId, Long id){
        
        List<WeightFreightPo> weightFreightPos = freightDao.findFreightItemsById(id);
        ArrayList<FreightItem> freightItems = new ArrayList<>(weightFreightPos.size());
        for (WeightFreightPo weightFreightPo : weightFreightPos) {
            FreightItem freightItem = new FreightItem(weightFreightPo);
            freightItems.add(freightItem);
        }
        return new ReturnObject<>(freightItems);
    }

    @Transactional
    public ReturnObject<List> findPieceItemsById(Long shopId, Long id){

        List<PieceFreightPo> pieceFreightPos=freightDao.findPieceItemsById(id);
        ArrayList<PieceItem> pieceItems = new ArrayList<>(pieceFreightPos.size());
        for (PieceFreightPo pieceFreightPo : pieceFreightPos) {
            PieceItem pieceItem = new PieceItem(pieceFreightPo);
            pieceItems.add(pieceItem);
        }
        return new ReturnObject<>(pieceItems);
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
