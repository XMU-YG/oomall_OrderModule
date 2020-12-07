package cn.edu.xmu.freight.dao;

import cn.edu.xmu.freight.mapper.FreightPoMapper;
import cn.edu.xmu.freight.mapper.PieceFreightPoMapper;
import cn.edu.xmu.freight.mapper.WeightFreightPoMapper;
import cn.edu.xmu.freight.model.bo.FreightItem;
import cn.edu.xmu.freight.model.bo.PieceItem;
import cn.edu.xmu.freight.model.po.*;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FreightDao {

    private static final Logger logger = LoggerFactory.getLogger(FreightDao.class);
    @Resource
    private FreightPoMapper freightPoMapper;
    @Resource
    private WeightFreightPoMapper weightFreightPoMapper;
    @Resource
    private PieceFreightPoMapper pieceFreightPoMapper;

    /**
     * 店家或管理员查询某个（重量）运费模板的明细
     *
     * @param shopid 店铺id
     * @param id     运费模板id
     * @return 运费模板详细信息
     * @author ShiYu Liao
     * @Create 2020/12/5
     * @Modify 2020/12/5
     */
    public ReturnObject<List> findFreightItemsById(Long shopid, Long id) {

        FreightPo freightPo = null;
        try {
            freightPo = freightPoMapper.selectByPrimaryKey(id);//筛选条件实际是model_id
        } catch (DataAccessException e) {
            logger.error("findFreightItemsById:  DataAccessException:  " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (freightPo == null) {
            logger.debug("findFreightItemsById error: it's empty!  shopid:  " + shopid + "   id:  " + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "运费模板号不存在");

        } else if (freightPo.getShopId().equals(shopid)) {
            logger.debug("findFreightItemsById success！  shopid:  " + shopid + "   id:  " + id);

            WeightFreightPoExample weightFreightPoExample=new  WeightFreightPoExample();
            WeightFreightPoExample.Criteria criteria= weightFreightPoExample.createCriteria();
            criteria.andFreightModelIdEqualTo(id);
            List<WeightFreightPo> weightFreightPos=weightFreightPoMapper.selectByExample(weightFreightPoExample);
            ArrayList<FreightItem> freightItems=new ArrayList<>(weightFreightPos.size());
            for (WeightFreightPo weightFreightPo : weightFreightPos){
                FreightItem freightItem=new FreightItem(weightFreightPo);
                freightItems.add(freightItem);
            }
            return new ReturnObject<>(freightItems);

        } else {
            logger.debug("findFreightItemsById error: don't have privilege!   shopid:  " + shopid + "   id:  " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "运费模板不属于该店铺");
        }
    }

    /**
     * 店家或管理员查询件数运费模板的明细
     *
     * @param shopid 店铺id
     * @param id     运费模板id
     * @return 运费模板详细信息
     * @author ShiYu Liao
     * @Create 2020/12/7
     * @Modify 2020/12/7
     */
    public ReturnObject<List> findPieceItemsById(Long shopid, Long id) {

        FreightPo freightPo = null;
        try {
            freightPo = freightPoMapper.selectByPrimaryKey(id);//筛选条件实际是model_id
        } catch (DataAccessException e) {
            logger.error("findPieceItemsById:  DataAccessException:  " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (freightPo == null) {
            logger.debug("findPieceItemsById error: it's empty!  shopid:  " + shopid + "   id:  " + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "运费模板号不存在");

        } else if (freightPo.getShopId().equals(shopid)) {
            logger.debug("findPieceItemsById success！  shopid:  " + shopid + "   id:  " + id);

            PieceFreightPoExample pieceFreightPoExample=new  PieceFreightPoExample();
            PieceFreightPoExample.Criteria criteria= pieceFreightPoExample.createCriteria();
            criteria.andFreightModelIdEqualTo(id);
            List<PieceFreightPo> pieceFreightPos=pieceFreightPoMapper.selectByExample(pieceFreightPoExample);
            ArrayList<PieceItem> pieceItems=new ArrayList<>(pieceFreightPos.size());
            for (PieceFreightPo pieceFreightPo : pieceFreightPos){
                PieceItem pieceItem=new PieceItem(pieceFreightPo);
                pieceItems.add(pieceItem);
            }
            return new ReturnObject<>(pieceItems);

        } else {
            logger.debug("findPieceItemsById error: don't have privilege!   shopid:  " + shopid + "   id:  " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "运费模板不属于该店铺");
        }
    }

    /**
     * 店家或管理员删除重量运费模板明细
     * 需要登陆
     * @param shopId
     * @param id
     * @author ShiYu Liao
     * @created 2020/12/7
     */
    public ReturnObject deleteWeightItem(Long shopId, Long id) {

        WeightFreightPo weightFreightPo=null;
        try{
            weightFreightPo=weightFreightPoMapper.selectByPrimaryKey(id);
        }catch (DataAccessException e){
            logger.error("deleteWeightItem:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,"失败");
        }
        if (weightFreightPo==null){
            logger.debug("shop deleteWeightItem error: it's empty!  id:  "+id+"   shopId:  "+shopId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"运费明细不存在");

        }
        else {
            FreightPo freightPo =freightPoMapper.selectByPrimaryKey(weightFreightPo.getFreightModelId());
            if(freightPo==null)
            {
                logger.error("null");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else if(freightPo.getShopId()!=shopId)
            {
                logger.error("没有删除该模板的权限");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
            else
            {
                if(weightFreightPoMapper.deleteByPrimaryKey(id)==1)
                {
                    logger.debug("shop deleteWeightItem success！  shopId:  "+shopId+"   id:  "+id);
                    return new ReturnObject(ResponseCode.OK,"删除重量运费模板明细成功");
                }
               else
                {
                    logger.debug("shop deleteWeightItem error: The deletion failed!  id:  "+id+"   shopId:  "+shopId);
                    return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,"删除重量运费明细失败");
                }
            }
        }
    }
}
