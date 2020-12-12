package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
<<<<<<< Updated upstream
=======
import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.bo.FreightItem;
import cn.edu.xmu.freight.model.bo.PieceItem;
import cn.edu.xmu.freight.model.po.FreightPo;
import cn.edu.xmu.freight.model.po.PieceFreightPo;
import cn.edu.xmu.freight.model.po.WeightFreightPo;
import cn.edu.xmu.freight.model.vo.*;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
>>>>>>> Stashed changes
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
<<<<<<< Updated upstream

=======
import com.github.pagehelper.PageInfo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
>>>>>>> Stashed changes
import java.util.List;
import java.util.Map;

@Service
public class FreightService {
    private Logger logger= LoggerFactory.getLogger(FreightService.class);

    @Autowired
    private FreightDao freightDao;

<<<<<<< Updated upstream
=======
    /**
     * 获取运费模板概要
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    @Transactional
    public ReturnObject<VoObject> getFreModelSummeryByModelId(Long shopId,Long id)
    {
        logger.info("id"+id+" shopId"+shopId);
       return freightDao.getFreModelSummeryByModelId(shopId,id);

    }

    /**
     * 定义运费模板
     * @author 胡曼珑
     * @param shopId
     * @param vo
     * @return
     */
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

    /**
     * 获得店铺的运费模板
     * @author 胡曼珑
     * @param id
     * @param name
     * @param page
     * @param pageSize
     * @return
     */
>>>>>>> Stashed changes
    @Transactional
    public ReturnObject<List> findFreightItemsById(Long shopid, Long id){
        return freightDao.findFreightItemsById(shopid,id);
    }

    @Transactional
    public ReturnObject<List> findPieceItemsById(Long shopid, Long id){
        return freightDao.findPieceItemsById(shopid,id);
    }
<<<<<<< Updated upstream
=======

    /**
     * 设置店铺的默认运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
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

    /**
     * 克隆运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    @Transactional
    public ReturnObject<VoObject> cloneModel(Long shopId,Long id)
    {
        return freightDao.cloneModel(shopId,id);

    }

    /**
     * 删除运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    @Transactional
    public ReturnObject<VoObject> deleteModel(Long shopId,Long id)
    {
        return freightDao.deleteModel(shopId,id);
    }

    @Transactional
    public Object calculateFreight(Long rid, List<ItemsVo> vos)
    {
        //前model_id,后shopid
        Map<Long,Long> models=new HashMap<Long,Long>();
        List<Long> weights=null;
        Long weightSum=0L;
        Long counts=0L;
        for(Long a : weights)
        {
            weightSum+=a;
        }
        for(ItemsVo b : vos)
        {
            counts+=b.getCount();
        }
        /*调用商品模块的接口
        * 获得重量
        * */

        return freightDao.calculateFreight(models,weightSum,counts,rid);
    }
    @Transactional
    public ReturnObject<List> findFreightItemsById(Long shopId, Long id){

        //校验
        ReturnObject<VoObject> ro=null;
        ro=freightDao.getFreModelSummeryByModelId(shopId,id);
        if(!ro.getCode().equals(ResponseCode.OK))
        {
            return new ReturnObject<List>(ro.getCode());
        }

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

        //校验
        ReturnObject<VoObject> ro=null;
        ro=freightDao.getFreModelSummeryByModelId(shopId,id);
        if(!ro.getCode().equals(ResponseCode.OK))
        {
            return new ReturnObject<List>(ro.getCode());
        }

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

        //校验
        ReturnObject<VoObject> ro=null;
        ro=freightDao.getFreModelSummeryByModelId(shopId,id);
        if(!ro.getCode().equals(ResponseCode.OK))
        {
            return ro;
        }

        //vo创建bo
        FreightItem freightItem=vo.createFreightItem();
        freightItem.setFreightModelId(id);
        freightItem.setGmtCreate(LocalDateTime.now());

        ReturnObject<FreightItem> ret=freightDao.createFreightItem(freightItem);

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
    public ReturnObject<VoObject> createPieceItem(Long shopId, Long id, PieceItemVo vo) {

        //校验
        ReturnObject<VoObject> ro=null;
        ro=freightDao.getFreModelSummeryByModelId(shopId,id);
        if(!ro.getCode().equals(ResponseCode.OK))
        {
            return ro;
        }

        //vo创建bo
        PieceItem pieceItem=vo.createPieceItem();
        pieceItem.setFreightModelId(id);
        pieceItem.setGmtCreate(LocalDateTime.now());

        ReturnObject<PieceItem> ret=freightDao.createPieceItem(pieceItem);

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

    /**
     * 修改重量模板明细
     * @author 廖诗雨
     * @param shopId
     * @param id
     * @param vo
     * @return
     */
    @Transactional
    public ReturnObject<VoObject> editFreightItem(Long shopId,Long id, WeightItemVo vo)
    {
        FreightItem freightItem=vo.createFreightItem();
        freightItem.setId(id);
        freightItem.setGmtModified(LocalDateTime.now());
        ReturnObject<FreightItem> retObj=freightDao.editFreightItem(freightItem);
        return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
    }

    /**
     * 修改件数模板明细
     * @author 廖诗雨
     * @param shopId
     * @param id
     * @param vo
     * @return
     */
    @Transactional
    public ReturnObject<VoObject> editPieceItem(Long shopId,Long id, PieceItemVo vo)
    {
        PieceItem pieceItem=vo.createPieceItem();
        pieceItem.setId(id);
        pieceItem.setGmtModified(LocalDateTime.now());
        ReturnObject<PieceItem> retObj=freightDao.editPieceItem(pieceItem);
        return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
    }

>>>>>>> Stashed changes
}
