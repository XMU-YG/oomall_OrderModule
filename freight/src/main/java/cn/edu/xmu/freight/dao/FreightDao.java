package cn.edu.xmu.freight.dao;

import cn.edu.xmu.freight.mapper.PieceFreightPoMapper;
import cn.edu.xmu.freight.mapper.WeightFreightPoMapper;
import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.bo.FreightItem;
import cn.edu.xmu.freight.model.bo.PieceItem;
import cn.edu.xmu.freight.model.po.*;
import cn.edu.xmu.freight.model.vo.FreightModelVo;
import cn.edu.xmu.freight.model.vo.ItemsVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.RandomCaptcha;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
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
import java.util.*;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Repository
public class FreightDao {
    private  static  final Logger logger = LoggerFactory.getLogger(FreightDao.class);
    @Resource
    private FreightPoMapper freightPoMapper;
    @Resource
    private WeightFreightPoMapper weightFreightPoMapper;
    @Resource
    private PieceFreightPoMapper pieceFreightPoMapper;

    /**
     * 计算运费
     * @author 胡曼珑
     * @param models
     * @param weightSum
     * @param counts
     * @param rid
     * @return
     */
    public Long calculateFreight(Map<Long,Long> models, Long weightSum,Integer counts,Long rid,List<Long> pids)
    {
        //Boolean check=checkMix(models);
        ReturnObject<VoObject>ret=null;
        int type=0;
        //注意：unit的单位是g
        int unit=0;
        Set<Long> keySet=models.keySet();
        Iterator<Long> it=keySet.iterator();
        ArrayList<Long> freights= new ArrayList<>();
        Long freight=0L;
        Long maxFreight=0l;
        while(it.hasNext())
        {
            Long modelId= it.next();
            Long shopId=models.get(modelId);
            //需判断modelId是否为null,当为null时则使用店铺默认运费模板
            ret=getFreModelSummeryByModelId(shopId,modelId);

            if(!ret.getCode().equals(ResponseCode.OK))
                return -1l;


            type=((FreightModelVo)ret.getData().createVo()).getType();

            if(type==0)
            {
                unit=((FreightModelVo)ret.getData().createVo()).getUnit();
                freight=calWeightFreight(modelId,weightSum,rid,unit,pids);
                freights.add(freight);
            }
            else if(type==1)
            {
                freight=calPieceFreight(modelId,counts,rid,pids);
                freights.add(freight);
            }
        }
        for(Long b:freights)
        {
            if(maxFreight.longValue()<b.longValue())
                maxFreight=b;
        }

        //return  ResponseUtil.ok(maxFreight);
        return maxFreight;
    }
    public Long calPieceFreight(Long modelId,Integer counts,Long rid,List<Long> pids)
    {
        PieceFreightPoExample example=new PieceFreightPoExample();
        PieceFreightPoExample.Criteria criteria= example.createCriteria();
        criteria.andFreightModelIdEqualTo(modelId);
        criteria.andRegionIdEqualTo(rid);
        List<PieceFreightPo> pos=pieceFreightPoMapper.selectByExample(example);
        if(pos.size()==0)
        {
            for(int i=0;i<2;i++)
            {
                criteria.getCriteria().clear();
                criteria.andFreightModelIdEqualTo(modelId);
                criteria.andRegionIdEqualTo(pids.get(i));
                pos=pieceFreightPoMapper.selectByExample(example);
                if(pos.size()>0)
                    break;
            }
        }
        PieceFreightPo po=pos.get(0);
        Long freight;
        Long firstItemsPrice=po.getFirstItemsPrice();
        Long additionalItemsPrice=po.getAdditionalItemsPrice();
        int firstItem=po.getFirstItems();
        int additionalItems=po.getAdditionalItems();
        //续件组数:num
        Integer num=(counts-firstItem)/additionalItems;
        if(num*additionalItems+firstItem<counts)
            num++;
        if(counts<firstItem)
        return firstItemsPrice;
        else
        {
            freight=firstItemsPrice+num*additionalItemsPrice;
            return freight;
        }

    }

    public Long calWeightFreight(Long modelId,Long weight,Long rid,int unit,List<Long> pids)
    {
        WeightFreightPoExample example=new WeightFreightPoExample();
        WeightFreightPoExample.Criteria criteria= example.createCriteria();
        criteria.andFreightModelIdEqualTo(modelId);
        criteria.andRegionIdEqualTo(rid);
        List<WeightFreightPo> pos=weightFreightPoMapper.selectByExample(example);
        if(pos.size()==0)
        {
            for(int i=0;i<2;i++)
            {
                criteria.getCriteria().clear();
                criteria.andFreightModelIdEqualTo(modelId);
                criteria.andRegionIdEqualTo(pids.get(i));
                pos=weightFreightPoMapper.selectByExample(example);
                if(pos.size()>0)
                    break;
            }
        }
        WeightFreightPo po=pos.get(0);
        Long freight;
        //注意：首重应该也是以g为单位，因为是Long类型，常见的0.5kg首重就只能用g表示
        Long first=po.getFirstWeight();
        Long num=(weight.longValue()-first.longValue())/unit;
        Long ten=(10000l-first.longValue())/unit;
        Long fifty=(50000l-first.longValue())/unit;
        Long hundred=(100000l-first.longValue())/unit;
        Long threeHundred=(300000l-first.longValue())/unit;
        Long firstWeightFreight=po.getFirstWeightFreight();
        Long tenPrice=po.getTenPrice();
        Long fiftyPrice=po.getTenPrice();
        Long hundredPrice=po.getHundredPrice();
        Long trihunPrice=po.getTrihunPrice();
        Long abovePrice=po.getAbovePrice();

        if(num.longValue()*unit<weight.longValue()) num++;
        if(weight.longValue()<first.longValue())
            return firstWeightFreight;
        else if(weight.longValue()<10000)
        {
            freight=firstWeightFreight.longValue()+(num.longValue())*tenPrice.longValue();
            return freight;
        }
        else if(weight.longValue()<50000)
        {
            freight=firstWeightFreight.longValue()+(ten.longValue())*tenPrice.longValue()+(num-ten)*fiftyPrice;
            return freight;
        }
        else if(weight.longValue()<100000)
        {
            freight=firstWeightFreight.longValue()+ten.longValue()*tenPrice.longValue()+(fifty.longValue()-ten.longValue())*fiftyPrice.longValue()+(num.longValue()-fifty.longValue())*hundredPrice.longValue();
            return freight;
        }
        else if(weight.longValue()<300000)
        {
            freight=firstWeightFreight.longValue()+ten.longValue()*tenPrice.longValue()+(fifty.longValue()-ten.longValue())*fiftyPrice.longValue()+(hundred.longValue()-fifty.longValue())*hundredPrice.longValue()+(num.longValue()-hundred.longValue())*trihunPrice.longValue();
            return freight;
        }
        else
        {
            freight=firstWeightFreight.longValue()+(ten.longValue())*tenPrice.longValue()+(fifty.longValue()-ten.longValue())*fiftyPrice.longValue()+(hundred.longValue()-fifty.longValue())*hundredPrice.longValue()+(threeHundred.longValue()-hundred.longValue())*trihunPrice.longValue()+(num.longValue()-threeHundred.longValue())*abovePrice.longValue();
            return freight;
        }

    }

   /* public Boolean checkMix(Map<Long,Long> models)
    {
        Boolean type0=false;
        Boolean type1=false;
        int type;
        Set<Long> keySet=models.keySet();
        Iterator<Long> it=keySet.iterator();
        while(it.hasNext()) {
            Long modelId = it.next();
            Long shopId = models.get(modelId);
            FreightPo po=freightPoMapper.selectByPrimaryKey(modelId);
            type=po.getType();
            if(type==0)
                type0=true;
            else if(type==1)
                type1=true;
        }
        if(type0&&type1)
            return true;
        return false;
    }*/
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
        FreightPo freightPo=null;
        if(id==null)
        {
            FreightPoExample example=new FreightPoExample();
            FreightPoExample.Criteria criteria= example.createCriteria();
            criteria.andShopIdEqualTo(shopId);
            criteria.andDefaultModelEqualTo((byte)1);
            List<FreightPo> pos=freightPoMapper.selectByExample(example);
            freightPo=pos.get(0);

        }
       else  freightPo=freightPoMapper.selectByPrimaryKey(id);
        if(freightPo==null)
        {
            logger.error("null");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(!freightPo.getShopId().equals(shopId))
        {
            logger.error("没有查询该模板的权限");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        return new ReturnObject<>(new Freight(freightPo));


    }

    public FreightModelVo getFreModelByModelId(Long id)
    {
        FreightPo freightPo=null;
        freightPo=freightPoMapper.selectByPrimaryKey(id);
        Freight bo=new Freight(freightPo);
        return new FreightModelVo(bo);
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
                //若有重复的运费模板名则新增失败
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
        PageHelper.startPage(page,pageSize);
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


       PageInfo<FreightPo> freightPoPage=PageInfo.of(freightPos);
        PageInfo<VoObject> freightPage=PageInfo.of(ret);
        freightPage.setPages(freightPoPage.getPages());
        freightPage.setPageNum(freightPoPage.getPageNum());
        freightPage.setPageSize(freightPoPage.getPageSize());
        freightPage.setTotal(freightPoPage.getTotal());

        //PageInfo<VoObject> freightPage=PageInfo.of(ret);
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
                //若有重复的运费模板名则新增失败
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
     * 克隆运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject<VoObject> cloneModel(Long shopId,Long id)
    {
        ReturnObject<VoObject> retObj=null;
        FreightPo freightPo =freightPoMapper.selectByPrimaryKey(id);
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

        //模板名称为原加随机数
        String name=RandomCaptcha.getRandomString(10);
        freightPo.setName(name);

        //模板初始为非默认
        freightPo.setDefaultModel((byte)0);
        //更新创建时间
        freightPo.setGmtCreate(LocalDateTime.now().withNano(0));
        freightPo.setGmtModified(null);
        //克隆主模板
        try {
            int ret = freightPoMapper.insertSelective(freightPo);
            if (ret == 0) {
                //修改失败
                logger.debug("cloneModel: update freight fail : " + freightPo.toString());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("模板id不存在：" + freightPo.getId()));
            }
        }
            catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("name")) {
                //若有重复的运费模板名则新增失败
                logger.debug("cloneModel: have same freight model name = " + freightPo.getName());
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

        //克隆模板明细
        int ret1=0;
        Long newId=freightPo.getId();
        try
        {
            int type=freightPo.getType();
            //重量明细
            if(type==0)
            {
              List<WeightFreightPo> pos=findFreightItemsById(id);
              for(WeightFreightPo po:pos)
              {
                  po.setFreightModelId(newId);
                  po.setGmtCreate(LocalDateTime.now().withNano(0));
                  po.setGmtModified(null);
                  ret1=weightFreightPoMapper.insertSelective(po);
                  if(ret1==0)
                  {
                      logger.debug("cloneModel : clone weight freight item fail : regionId : "+po.getRegionId());
                      return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("新增失败: "+po.getRegionId()));
                  }
              }
            }
            //单件明细
            else if(type==1)
            {
                List<PieceFreightPo> pos=findPieceItemsById(id);
                for(PieceFreightPo po:pos)
                {
                    po.setFreightModelId(newId);
                    po.setGmtCreate(LocalDateTime.now().withNano(0));
                    po.setGmtModified(null);
                    ret1=pieceFreightPoMapper.insertSelective(po);
                    if(ret1==0)
                    {
                        logger.debug("cloneModel : clone piece freight item fail : regionId : "+po.getRegionId());
                        return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("新增失败: "+po.getRegionId()));
                    }
                }
            }
            //成功的返回值
        Freight freight=new Freight(freightPo);
            retObj=new ReturnObject<>(freight);
        }
        catch (Exception e)
        {

            logger.error("other exception : "+e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
            return retObj;
    }


    /**
     * 删除运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject<VoObject> deleteModel(Long shopId,Long id)
    {
        /* 先根据id找到该运费模板，判断它是否属于该商铺*/
        ReturnObject<VoObject> retObj=null;
        FreightPo freightPo =freightPoMapper.selectByPrimaryKey(id);
        if(freightPo==null)
        {
            logger.error("null");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(freightPo.getShopId()!=shopId)
        {
            logger.error("该运费模板不属于店铺 shopId "+shopId);
            retObj=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return retObj;
        }
        int ret=0;

        try {
            /*根据type分情况调用模板明细的删除函数*/
            int type=freightPo.getType();
            if(type==0)
            {
                WeightFreightPoExample del=new WeightFreightPoExample();
                WeightFreightPoExample.Criteria criteria=del.createCriteria();
                criteria.andFreightModelIdEqualTo(id);
                ret=weightFreightPoMapper.deleteByExample(del);
            }
            else if(type==1)
            {
                PieceFreightPoExample del=new PieceFreightPoExample();
                PieceFreightPoExample.Criteria criteria= del.createCriteria();;
                criteria.andFreightModelIdEqualTo(id);
                ret=pieceFreightPoMapper.deleteByExample(del);
            }
           //是否要加上如果type不等于0，1的情况吗？

            int ret1 = freightPoMapper.deleteByPrimaryKey(id);
            if (ret1 == 0) {
                //删除失败
                logger.debug("deleteModel: delete freight fail : " + freightPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("模板id不存在：" + freightPo.getId()));
            } else {
                //删除成功
                logger.debug("deleteModel: delete freight = " + freightPo.toString());
                retObj = new ReturnObject<>(ResponseCode.OK);
            }
        }
        catch (Exception e){
            logger.error("editFreightModel:  Exception:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return retObj;
    }


    /**
     * 店家或管理员查询某个（重量）运费模板的明细
     *
     * @param id     运费模板id
     * @return 运费模板详细信息
     * @author ShiYu Liao
     * @Create 2020/12/5
     * @Modify 2020/12/5
     */
    public List<WeightFreightPo> findFreightItemsById(Long id) {
/*
        FreightPo freightPo = null;
        try {
            freightPo = freightPoMapper.selectByPrimaryKey(id);//筛选条件实际是model_id
        } catch (DataAccessException e) {
            logger.error("findFreightItemsById:  DataAccessException:  " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (freightPo == null) {
            logger.debug("findFreightItemsById error: it's empty!  shopId:  " + shopId + "   id:  " + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "运费模板号不存在");

        } else if (freightPo.getShopId().equals(shopId)) {
            logger.debug("findFreightItemsById success！  shopId:  " + shopId + "   id:  " + id);

        } else {
            logger.debug("findFreightItemsById error: don't have privilege!   shopId:  " + shopId + "   id:  " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "运费模板不属于该店铺");
        }
*/
        WeightFreightPoExample weightFreightPoExample = new WeightFreightPoExample();
        WeightFreightPoExample.Criteria criteria = weightFreightPoExample.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        List<WeightFreightPo> weightFreightPos = weightFreightPoMapper.selectByExample(weightFreightPoExample);
        /*
        ArrayList<FreightItem> freightItems = new ArrayList<>(weightFreightPos.size());
        for (WeightFreightPo weightFreightPo : weightFreightPos) {
            FreightItem freightItem = new FreightItem(weightFreightPo);
            freightItems.add(freightItem);
        }
        return new ReturnObject<>(freightItems);
        */
        return weightFreightPos;
    }

    /**
     * 店家或管理员查询件数运费模板的明细
     *
     * @param id     运费模板id
     * @return 运费模板详细信息
     * @author ShiYu Liao
     * @Create 2020/12/7
     * @Modify 2020/12/7
     */
    public List<PieceFreightPo> findPieceItemsById(Long id) {

        /*
        FreightPo freightPo = null;
        try {
            freightPo = freightPoMapper.selectByPrimaryKey(id);//筛选条件实际是model_id
        } catch (DataAccessException e) {
            logger.error("findPieceItemsById:  DataAccessException:  " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (freightPo == null) {
            logger.debug("findPieceItemsById error: it's empty!  shopId:  " + shopId + "   id:  " + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "运费模板号不存在");

        } else if (freightPo.getShopId().equals(shopId)) {
            logger.debug("findPieceItemsById success！  shopId:  " + shopId + "   id:  " + id);

        } else {
            logger.debug("findPieceItemsById error: don't have privilege!   shopId:  " + shopId + "   id:  " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "运费模板不属于该店铺");
        }
        */
        PieceFreightPoExample pieceFreightPoExample = new PieceFreightPoExample();
        PieceFreightPoExample.Criteria criteria = pieceFreightPoExample.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        List<PieceFreightPo> pieceFreightPos = pieceFreightPoMapper.selectByExample(pieceFreightPoExample);
        /*
        ArrayList<PieceItem> pieceItems = new ArrayList<>(pieceFreightPos.size());
        for (PieceFreightPo pieceFreightPo : pieceFreightPos) {
            PieceItem pieceItem = new PieceItem(pieceFreightPo);
            pieceItems.add(pieceItem);
        }
        return new ReturnObject<>(pieceItems);
        */
        return pieceFreightPos;
    }

    /**
     * 店家或管理员删除重量运费模板明细
     * 需要登陆
     *
     * @param shopId
     * @param id
     * @author ShiYu Liao
     * @created 2020/12/7
     */
    public ReturnObject deleteWeightItem(Long shopId, Long id) {

        WeightFreightPo weightFreightPo = null;
        try {
            weightFreightPo = weightFreightPoMapper.selectByPrimaryKey(id);
        } catch (DataAccessException e) {
            logger.error("deleteWeightItem:  DataAccessException:  " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, "失败");
        }
        if (weightFreightPo == null) {
            logger.debug("shop deleteWeightItem error: it's empty!  id:  " + id + "   shopId:  " + shopId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "运费明细不存在");

        } else {

            FreightPo freightPo = freightPoMapper.selectByPrimaryKey(weightFreightPo.getFreightModelId());
            if (freightPo == null) {
                logger.error("null");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else if (freightPo.getShopId() != shopId) {
                logger.error("没有删除该模板的权限");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            } else {
                if (weightFreightPoMapper.deleteByPrimaryKey(id) == 1) {
                    logger.debug("shop deleteWeightItem success！  shopId:  " + shopId + "   id:  " + id);
                    return new ReturnObject(ResponseCode.OK, "删除重量运费模板明细成功");
                } else {
                    logger.debug("shop deleteWeightItem error: The deletion failed!  id:  " + id + "   shopId:  " + shopId);
                    return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "删除重量运费明细失败");
                }
            }
        }
    }

    /**
     * 店家或管理员删除件数运费模板明细
     * 需要登陆
     *
     * @param shopId
     * @param id
     * @author ShiYu Liao
     * @created 2020/12/7
     */
    public ReturnObject deletePieceItem(Long shopId, Long id) {

        PieceFreightPo pieceFreightPo = null;
        try {
            pieceFreightPo = pieceFreightPoMapper.selectByPrimaryKey(id);
        } catch (DataAccessException e) {
            logger.error("deletePieceItem:  DataAccessException:  " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, "失败");
        }
        if (pieceFreightPo == null) {
            logger.debug("shop deletePieceItem error: it's empty!  id:  " + id + "   shopId:  " + shopId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "运费明细不存在");

        } else {
            FreightPo freightPo = freightPoMapper.selectByPrimaryKey(pieceFreightPo.getFreightModelId());
            if (freightPo == null) {
                logger.error("null");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else if (freightPo.getShopId() != shopId) {
                logger.error("没有删除该模板的权限");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            } else {
                if (pieceFreightPoMapper.deleteByPrimaryKey(id) == 1) {
                    logger.debug("shop deletePieceItem success！  shopId:  " + shopId + "   id:  " + id);
                    return new ReturnObject(ResponseCode.OK, "删除件数运费模板明细成功");
                } else {
                    logger.debug("shop deletePieceItem error: The deletion failed!  id:  " + id + "   shopId:  " + shopId);
                    return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "删除件数运费明细失败");
                }
            }
        }
    }

    /**
     * 管理员定义重量模板明细
     *
     * @param freightItem
     * @return
     * @author 廖诗雨
     */
    public ReturnObject<FreightItem> createFreightItem(FreightItem freightItem) {
        //bo创建po
        WeightFreightPo weightFreightPo = freightItem.getWeightFreightPo();
/*
        //校验
        FreightPo freightPo = freightPoMapper.selectByPrimaryKey(weightFreightPo.getFreightModelId());
        if (freightPo == null) {
            logger.error("没有该运费模板");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else if (freightPo.getShopId() != shopId) {
            logger.error("没有查询该模板的权限");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
*/
        WeightFreightPoExample weightFreightPoExample = new WeightFreightPoExample();
        WeightFreightPoExample.Criteria criteria = weightFreightPoExample.createCriteria();
        criteria.andRegionIdEqualTo(weightFreightPo.getRegionId());
        criteria.andFreightModelIdEqualTo(weightFreightPo.getFreightModelId());
        List<WeightFreightPo> weightFreightPos = weightFreightPoMapper.selectByExample(weightFreightPoExample);
        if (weightFreightPos.size() != 0) {
            logger.debug("createFreightModel: have same regionId = " + weightFreightPo.getRegionId());
            return new ReturnObject<>(ResponseCode.REGION_SAME, String.format("运费模板中该地区已经定义：" + weightFreightPo.getRegionId()));
        }

        //修改数据库
        ReturnObject<FreightItem> retObj = null;
        try {
            int ret = weightFreightPoMapper.insertSelective(weightFreightPo);
            if (ret == 0) {
                //插入失败
                logger.error("createFreightItem freight fail");
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败: " + weightFreightPo.getId()));
            } else {
                //插入成功
                logger.debug("createFreightItem:insert freight = " + weightFreightPo.toString());
                freightItem.setId(weightFreightPo.getId());
                retObj = new ReturnObject<>(freightItem);
            }
        } catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {

            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 管理员定义件数模板明细
     *缺少件数/重量的判断，暂时不加
     * @param pieceItem
     * @return
     * @author 廖诗雨
     */
    public ReturnObject<PieceItem> createPieceItem(PieceItem pieceItem) {
        //bo创建po
        PieceFreightPo pieceFreightPo = pieceItem.getPieceFreightPo();
/*
        //校验
        FreightPo freightPo = freightPoMapper.selectByPrimaryKey(pieceFreightPo.getFreightModelId());
        if (freightPo == null) {
            logger.error("没有该运费模板");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else if (freightPo.getShopId() != shopId) {
            logger.error("没有查询该模板的权限");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
*/
        PieceFreightPoExample pieceFreightPoExample = new PieceFreightPoExample();
        PieceFreightPoExample.Criteria criteria = pieceFreightPoExample.createCriteria();
        criteria.andRegionIdEqualTo(pieceFreightPo.getRegionId());
        criteria.andFreightModelIdEqualTo(pieceFreightPo.getFreightModelId());
        List<PieceFreightPo> pieceFreightPos = pieceFreightPoMapper.selectByExample(pieceFreightPoExample);
        if (pieceFreightPos.size() != 0) {
            logger.debug("createPieceItem: have same regionId = " + pieceFreightPo.getRegionId());
            return new ReturnObject<>(ResponseCode.REGION_SAME, String.format("运费模板中该地区已经定义：" + pieceFreightPo.getRegionId()));
        }

        //修改数据库
        ReturnObject<PieceItem> retObj = null;
        try {
            int ret = pieceFreightPoMapper.insertSelective(pieceFreightPo);
            if (ret == 0) {
                //插入失败
                logger.error("createPieceItem freight fail");
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败: " + pieceFreightPo.getId()));
            } else {
                //插入成功
                logger.debug("createPieceItem:insert freight = " + pieceFreightPo.toString());
                pieceItem.setId(pieceFreightPo.getId());
                retObj = new ReturnObject<>(pieceItem);
            }
        } catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {

            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 修改重量模板明细
     * @author 廖诗雨
     * @param freightItem
     * @return
     */
    public ReturnObject<FreightItem> editFreightItem(Long shopId,FreightItem freightItem)
    {

        WeightFreightPo weightFreightPo=freightItem.getWeightFreightPo();
        WeightFreightPo originalWeightFreightPo=weightFreightPoMapper.selectByPrimaryKey(weightFreightPo.getId());

        if(originalWeightFreightPo==null)
        {
            logger.error("没有该运费模板明细");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        weightFreightPo.setFreightModelId(originalWeightFreightPo.getFreightModelId());

        //校验
        FreightPo freightPo = freightPoMapper.selectByPrimaryKey(weightFreightPo.getFreightModelId());
        if (freightPo == null) {
            logger.error("没有该运费模板");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else if (freightPo.getShopId() != shopId) {
            logger.error("没有查询该模板的权限");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        WeightFreightPoExample weightFreightPoExample = new WeightFreightPoExample();
        WeightFreightPoExample.Criteria criteria = weightFreightPoExample.createCriteria();
        criteria.andRegionIdEqualTo(weightFreightPo.getRegionId());
        criteria.andFreightModelIdEqualTo(weightFreightPo.getFreightModelId());
        List<WeightFreightPo> weightFreightPos = weightFreightPoMapper.selectByExample(weightFreightPoExample);
        if (weightFreightPos.size() != 0) {
            logger.info("editFreightItem: have same regionId = " + weightFreightPo.getRegionId());
            return new ReturnObject<>(ResponseCode.REGION_SAME, String.format("运费模板中该地区已经定义：" + weightFreightPo.getRegionId()));
        }

        ReturnObject<FreightItem> retObj=null;
        try
        {
            int ret=weightFreightPoMapper.updateByPrimaryKeySelective(weightFreightPo);
            if (ret == 0) {
                //修改失败
                logger.info("editFreightItem: update freight fail : " + weightFreightPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("模板id不存在：" + weightFreightPo.getId()));
            } else {
                //修改成功
                logger.info("editFreightItem: update role = " + weightFreightPo.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.info("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 修改件数模板明细
     * @author 廖诗雨
     * @param pieceItem
     * @return
     */
    public ReturnObject<PieceItem> editPieceItem(Long shopId,PieceItem pieceItem)
    {
        PieceFreightPo pieceFreightPo=pieceItem.getPieceFreightPo();
        PieceFreightPo originalPieceFreightPo=pieceFreightPoMapper.selectByPrimaryKey(pieceFreightPo.getId());

        if(originalPieceFreightPo==null)
        {
            logger.error("没有该运费模板明细");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        pieceFreightPo.setFreightModelId(originalPieceFreightPo.getFreightModelId());

        //校验
        FreightPo freightPo = freightPoMapper.selectByPrimaryKey(pieceFreightPo.getFreightModelId());
        if (freightPo == null) {
            logger.error("没有该运费模板");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else if (freightPo.getShopId() != shopId) {
            logger.error("没有查询该模板的权限");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        PieceFreightPoExample pieceFreightPoExample = new PieceFreightPoExample();
        PieceFreightPoExample.Criteria criteria = pieceFreightPoExample.createCriteria();
        criteria.andRegionIdEqualTo(pieceFreightPo.getRegionId());
        criteria.andFreightModelIdEqualTo(pieceFreightPo.getFreightModelId());
        List<PieceFreightPo> pieceFreightPos = pieceFreightPoMapper.selectByExample(pieceFreightPoExample);
        if (pieceFreightPos.size() != 0) {
            logger.debug("editPieceItem: have same regionId = " + pieceFreightPo.getRegionId());
            return new ReturnObject<>(ResponseCode.REGION_SAME, String.format("运费模板中该地区已经定义：" + pieceFreightPo.getRegionId()));
        }

        ReturnObject<PieceItem> retObj=null;
        try
        {
            int ret=pieceFreightPoMapper.updateByPrimaryKeySelective(pieceFreightPo);
            if (ret == 0) {
                //修改失败
                logger.debug("editPieceItem: update freight fail : " + pieceFreightPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("模板id不存在：" + pieceFreightPo.getId()));
            } else {
                //修改成功
                logger.debug("editPieceItem: update role = " + pieceFreightPo.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

}
