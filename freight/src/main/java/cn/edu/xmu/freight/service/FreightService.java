package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.bo.PieceFreight;
import cn.edu.xmu.freight.model.vo.FreightInfoVo;
import cn.edu.xmu.freight.model.vo.FreightSimpInfoVo;
import cn.edu.xmu.freight.model.vo.PieceModelItemVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageInfo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FreightService {
    private Logger logger=LoggerFactory.getLogger(FreightService.class);

    @Autowired
    private FreightDao freightDao;
    @Transactional
    public ReturnObject<VoObject> getFreModelSummeryByModelId(Long shopId,Long id)
    {
        logger.info("id"+id+" shopId"+shopId);
        return freightDao.getFreModelSummeryByModelId(shopId,id);
    }
    @Transactional
    public ReturnObject<VoObject> createFreightModel(Long shopId, FreightInfoVo vo)
    {
        Freight freight=vo.createFreight();
        freight.setShopId(shopId);
        freight.setGmtCreate(LocalDateTime.now());
        freight.setDefaultModel((byte) 0);
        ReturnObject<Freight> ret=freightDao.createFreightModel(freight);
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

    @Transactional
    public ReturnObject<PageInfo<VoObject>> getFreModelByShopId(Long id,String name,Integer page,Integer pageSize)
    {
        ReturnObject<PageInfo<VoObject>> returnObject = freightDao.getFreModelByShopId(id,name,page,pageSize);
        return returnObject;
    }

    @Transactional
    public ReturnObject<VoObject> editFreightModel(Long shopId,Long id, FreightSimpInfoVo vo)
    {
        Freight freight=vo.createFreight();
        freight.setId(id);
        freight.setShopId(shopId);
        freight.setGmtModified(LocalDateTime.now());
        ReturnObject<Freight> retObj=freightDao.editFreightModel(freight);
        return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());


    }

    @Transactional
    public ReturnObject<VoObject> setDefaultModel(Long shopId,Long id)
    {
        Freight freight=new Freight();
        freight.setId(id);
        freight.setShopId(shopId);
        freight.setDefaultModel((byte)1);
        ReturnObject<Freight> retObj=freightDao.setDefaultModel(freight);
        return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
    }
/*
    @Transactional
    public ReturnObject<VoObject> createPieceFreight(Long shopId, Long id, PieceModelItemVo vo)
    {
        PieceFreight pieceFreight=vo.createPieceFreight();
        pieceFreight.setGmtCreate(LocalDateTime.now());
        ReturnObject<PieceFreight> ret=freightDao.createPieceFreight(shopId,id,pieceFreight);
        ReturnObject<VoObject> retObj=null;
        return retObj;
    }

 */
}