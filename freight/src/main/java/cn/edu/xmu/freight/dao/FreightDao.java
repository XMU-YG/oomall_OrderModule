package cn.edu.xmu.freight.dao;

import cn.edu.xmu.freight.mapper.PieceFreightPoMapper;
import cn.edu.xmu.freight.mapper.WeightFreightPoMapper;
import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.bo.PieceFreight;
import cn.edu.xmu.freight.model.po.FreightPo;
import cn.edu.xmu.freight.model.po.FreightPoExample;
import cn.edu.xmu.freight.model.po.PieceFreightPo;
import cn.edu.xmu.freight.model.po.PieceFreightPoExample;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.freight.mapper.FreightPoMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
//import sun.java2d.pipe.SpanShapeRenderer;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Repository
public class FreightDao {
    private  static  final Logger logger = LoggerFactory.getLogger(FreightDao.class);
    @Autowired
    private FreightPoMapper freightPoMapper;
    @Resource
    private WeightFreightPoMapper weightFreightPoMapper;
    @Resource
    private PieceFreightPoMapper pieceFreightPoMapper;


    /**
     * 获得运费模板概要
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject<VoObject> getFreModelSummeryByModelId(Long shopId,Long id)
    {
        logger.info("id "+id+" shopId:"+shopId);
       FreightPo freightPo=freightPoMapper.selectByPrimaryKey(id);
       logger.info("id"+freightPo.getId());

       if(freightPo==null)
       {
           logger.error("null");
           return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
       }
       else if(freightPo.getShopId()!=shopId)
       {
           logger.error("没有查询该模板的权限");
           return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
       }
       else
       {
           return new ReturnObject<>(new Freight(freightPo));
       }
    }

    /**
     * 管理员定义运费模板
     * @author 胡曼珑
     * @param freight
     * @return
     */
    public ReturnObject<Freight> createFreightModel(Freight freight)
    {
        FreightPo freightPo=freight.getFreightPo();
        ReturnObject<Freight> retObj=null;
        try{
            int ret=freightPoMapper.insertSelective(freightPo);
            if(ret==0)
            {
                //插入失败
                logger.error("createFreightModel freight fail");
                retObj=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("新增失败: "+freightPo.getName()));
            }
            else
            {
                //插入成功
                logger.debug("createFreightModel:insert freight = "+freightPo.toString());
                freight.setId(freightPo.getId());
                retObj=new ReturnObject<>(freight);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("name")) {
                //若有重复的角色名则新增失败
                logger.debug("createFreightModel: have same freight model name = " + freightPo.getName());
                retObj = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME, String.format("运费模板名重复：" + freightPo.getName()));

            }
            else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }

        catch (Exception e)
        {

            logger.error("other exception : "+e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 获得店铺的运费模板
     * @auhtor 胡曼珑
     * @param id
     * @param name
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> getFreModelByShopId(Long id,String name,Integer page,Integer pageSize)
    {
        FreightPoExample example=new FreightPoExample();
        FreightPoExample.Criteria criteria=example.createCriteria();
        List<FreightPo> freightPos=null;

        criteria.andShopIdEqualTo(id);
        try {
            if (name == null) {
                freightPos=freightPoMapper.selectByExample(example);
            }
            else
            {
                criteria.andNameEqualTo(name);
                freightPos=freightPoMapper.selectByExample(example);
            }
        }
        catch (DataAccessException e){
            logger.error("getFreModelByShopId:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        List<VoObject> ret=new ArrayList<>(freightPos.size());
        for(FreightPo po:freightPos)
        {
            Freight freight=new Freight(po);
            logger.debug("getFreModelByShopId: id: "+po.getId()+" shopId: "+po.getShopId());
            ret.add(freight);
        }

        PageHelper.startPage(page,pageSize);
        PageInfo<FreightPo> freightPoPage=PageInfo.of(freightPos);
        PageInfo<VoObject> freightPage=new PageInfo<>(ret);
        freightPage.setPages(freightPoPage.getPages());
        freightPage.setPageNum(freightPoPage.getPageNum());
        freightPage.setPageSize(freightPoPage.getPageSize());
        freightPage.setTotal(freightPoPage.getTotal());

        return new ReturnObject<>(freightPage);

    }

    /**
     * 修改运费模板
     * @author 胡曼珑
     * @param freight
     * @return
     */
    public ReturnObject<Freight> editFreightModel(Freight freight)
    {
        FreightPo freightPo=freight.getFreightPo();
        ReturnObject<Freight> retObj=null;
        //shopid是用来干啥的？验证？或是查询？
        try
        {
            int ret=freightPoMapper.updateByPrimaryKeySelective(freightPo);
            if (ret == 0) {
                //修改失败
                logger.debug("editFreightModel: update freight fail : " + freightPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("模板id不存在：" + freightPo.getId()));
            } else {
                //修改成功
                logger.debug("editFreightModel: update role = " + freightPo.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
            if (Objects.requireNonNull(e.getMessage()).contains("name")) {
                //若有重复的角色名则新增失败
                logger.debug("editFreightModel: have same freight model name = " + freightPo.getName());
                retObj = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME, String.format("运费模板名重复：" + freightPo.getName()));

            }
            else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        return retObj;
    }

    /**
     * 设置默认运费模板
     * @author 胡曼珑
     * @param freight
     * @return
     */
    public ReturnObject<Freight> setDefaultModel(Freight freight)
    {
        ReturnObject<Freight> retObj=null;
        FreightPo freightPo=freight.getFreightPo();
        FreightPoExample example=new FreightPoExample();
        FreightPoExample.Criteria criteria= example.createCriteria();
        //筛选条件：defaultmodel,shopId
        criteria.andDefaultModelEqualTo((byte)1);
        criteria.andShopIdEqualTo(freightPo.getShopId());
        FreightPo freightPo1=new FreightPo();
        freightPo1.setDefaultModel((byte)0);
        try
        {
            //将原有的默认模板设为非默认
          int ret1=freightPoMapper.updateByExampleSelective(freightPo1,example);
          if(ret1==0)
          {
              //修改失败
              logger.debug("editFreightModel: update freight fail : " + freightPo.toString());
              retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("模板id不存在：" + freightPo.getId()));
              return retObj;
          }
            int ret=freightPoMapper.updateByPrimaryKeySelective(freightPo);
            if (ret == 0) {
                //修改失败
                logger.debug("editFreightModel: update freight fail : " + freightPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("模板id不存在：" + freightPo.getId()));
            } else {
                //修改成功
                logger.debug("editFreightModel: update role = " + freightPo.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
        logger.error("editFreightModel:  DataAccessException:  "+e.getMessage());
        return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }
        return retObj;
    }


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
}
