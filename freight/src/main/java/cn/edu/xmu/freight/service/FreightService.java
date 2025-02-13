package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.bo.FreightItem;
import cn.edu.xmu.freight.model.bo.InnerSkuFreightInfo;
import cn.edu.xmu.freight.model.bo.PieceItem;
import cn.edu.xmu.freight.model.po.PieceFreightPo;
import cn.edu.xmu.freight.model.po.WeightFreightPo;
import cn.edu.xmu.freight.model.vo.*;
import cn.edu.xmu.goodsprovider.goods.GoodsInner;
import cn.edu.xmu.address.dubbo.AddressDubboService;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageInfo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FreightService {
    private Logger logger=LoggerFactory.getLogger(FreightService.class);

    @Autowired
    private FreightDao freightDao;

    @DubboReference(version = "0.0.1",check = false)
    private GoodsInner goodsService;

    @DubboReference(version = "0.0.1",check = false)
    private AddressDubboService addressDubboService;
    /**
     * 获取运费模板概要
     * @author 胡曼珑
     * @param id
     * @return
     */
    @Transactional
    public FreightModelVo getFreModelByModelId(Long id)
    {
        logger.info("id"+id);
       return freightDao.getFreModelByModelId(id);

    }

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
        freight.setGmtCreate(LocalDateTime.now().withNano(0));
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
    @Transactional
    public ReturnObject<PageInfo<VoObject>> getFreModelByShopId(Long id,String name,Integer page,Integer pageSize)
    {
        ReturnObject<PageInfo<VoObject>> returnObject = freightDao.getFreModelByShopId(id,name,page,pageSize);
        return returnObject;
    }


    /**
     * 修改运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @param vo
     * @return
     */
    @Transactional
    public ReturnObject<VoObject> editFreightModel(Long shopId,Long id, FreightSimpInfoVo vo)
    {
        ReturnObject ret=freightDao.getFreModelSummeryByModelId(shopId, id);
        if(!ret.getCode().equals(ResponseCode.OK))
            return new ReturnObject<>(ret.getCode(), ret.getErrmsg());
        Freight freight=vo.createFreight();
        freight.setId(id);
        freight.setShopId(shopId);
        freight.setGmtModified(LocalDateTime.now().withNano(0));
        ReturnObject<Freight> retObj=freightDao.editFreightModel(freight);
        return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());


    }

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
        ReturnObject ret=freightDao.getFreModelSummeryByModelId(shopId, id);
        if(!ret.getCode().equals(ResponseCode.OK))
            return new ReturnObject<>(ret.getCode(), ret.getErrmsg());
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
        /*调用商品模块的删除与运费模板的联系*/
        ReturnObject<VoObject> ret1= freightDao.deleteModel(shopId,id);
        boolean judge= true;
        judge=goodsService.cleanFreightById(id);
        if(judge)
            return ret1;
        else
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 计算运费
     * @author 胡曼珑
     * @param rid
     * @param vos
     * @return
     */
    @Transactional
    public Long calculateFreight(Long rid, List<ItemsVo> vos)
    {

        List<Long> skuIds=vos.stream().map(src->src.getSkuId()).collect(Collectors.toList());
        //前model_id,后shopid
        Map<Long,Long> models=new HashMap<Long,Long>();
        List<Long> weights=new ArrayList<>();
        Long weightSum=0L;
        Integer counts=0;
        Long shopId=null;
        Long freightId=null;;
        String goodsInfoJson=null;
        int i=0;
       ///InnerSkuFreightInfo info=null;
        for (Long id:skuIds) {
            goodsInfoJson=goodsService.getFreightInfoBySkuId(id);
            InnerSkuFreightInfo info= JacksonUtil.toObj(goodsInfoJson,InnerSkuFreightInfo.class);
            weightSum+=info.getWeight()*vos.get(i).getCount();
            freightId=info.getFreightId();
            shopId=info.getShopId();
            models.put(freightId,shopId);
            i++;
        }

        for(ItemsVo b : vos)
        {
            counts+=b.getCount();
        }

        List<Long> pids=addressDubboService.getPidsById(rid);
          /*List<Long> pids=new ArrayList<>();
        pids.add(154L);
        pids.add(14L);

           */


        /*调用商品模块的接口
        * 获得重量
        * */

        return freightDao.calculateFreight(models,weightSum,counts,rid,pids);
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

       int type=((FreightModelVo)ro.getData().createVo()).getType();
        if(type!=0)
            return new ReturnObject<>(ResponseCode.MODEL_TYPE_DISMATCH);
        //vo创建bo
        FreightItem freightItem=vo.createFreightItem();
        freightItem.setFreightModelId(id);
        freightItem.setGmtCreate(LocalDateTime.now().withNano(0));

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
        pieceItem.setGmtCreate(LocalDateTime.now().withNano(0));

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
        freightItem.setGmtModified(LocalDateTime.now().withNano(0));
        ReturnObject<FreightItem> retObj=freightDao.editFreightItem(shopId,freightItem);
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
        pieceItem.setGmtModified(LocalDateTime.now().withNano(0));
        ReturnObject<PieceItem> retObj=freightDao.editPieceItem(shopId,pieceItem);
        return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
    }

}
