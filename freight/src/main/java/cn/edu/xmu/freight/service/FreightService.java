package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FreightService {
    private Logger logger= LoggerFactory.getLogger(FreightService.class);

    @Autowired
    private FreightDao freightDao;

    @Transactional
    public ReturnObject<List> findFreightItemsById(Long shopid, Long id){
        return freightDao.findFreightItemsById(shopid,id);
    }

    @Transactional
    public ReturnObject<List> findPieceItemsById(Long shopid, Long id){
        return freightDao.findPieceItemsById(shopid,id);
    }

    @Transactional
    public ReturnObject deleteWeightItem(Long shopId, Long id) {
        return freightDao.deleteWeightItem(shopId,id);
    }

    @Transactional
    public ReturnObject deletePieceItem(Long shopId, Long id) {
        return freightDao.deletePieceItem(shopId,id);
    }
}
