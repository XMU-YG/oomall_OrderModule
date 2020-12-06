package cn.edu.xmu.freight.dao;

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
import org.springframework.stereotype.Service;

@Repository
public class FreightDao {
    private  static  final Logger logger = LoggerFactory.getLogger(FreightDao.class);
    @Autowired
    private FreightPoMapper freightPoMapper;

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
        catch (Exception e)
        {

            logger.error("other exception : "+e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

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
            logger.error("editFreightModel:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return retObj;
    }

    public ReturnObject<Freight> setDefaultModel(Freight freight)
    {
        FreightPo freightPo=freight.getFreightPo();
        ReturnObject<Freight> retObj=null;
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
        logger.error("editFreightModel:  DataAccessException:  "+e.getMessage());
        return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }
        return retObj;
    }
/*
    public ReturnObject<PieceFreight> createPieceFreight(Long shopId,Long id,PieceFreight pieceFreight)
    {
        PieceFreightPoExample example=new PieceFreightPoExample();
        PieceFreightPoExample.Criteria criteria=example.createCriteria();
        PieceFreight ret;

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

    }

 */
}
