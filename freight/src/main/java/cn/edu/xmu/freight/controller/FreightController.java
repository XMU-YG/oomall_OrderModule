package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.model.vo.*;

import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.freight.service.FreightService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Api(value = "运费服务", tags = "freight")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/freight", produces = "application/json;charset=UTF-8")
//@RequestMapping(produces = "application/json;charset=UTF-8")
public class FreightController {
    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    private FreightService freightService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation(value = "买家用运费模板计算一批订单商品的运费")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="rid", value="地区id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="vos", value="运费模板资料", required = true, allowMultiple = true, dataType="ItemsVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/region/{rid}/price")
    public Object calculateFreight(@PathVariable Long rid, @RequestBody List<ItemsVo> vos){
        logger.debug("calculate freight by shopId:"+rid);
        //当返回值为-1时，出错，其他情况下正常
        Long ret1=freightService.calculateFreight(rid,vos);
        ReturnObject ret;
        Object re;
        logger.debug("calculateFreight by: rid : "+rid);
        //if(!ret1.equals(-1l))
         if(ret1.equals(-1l))
        {
            ret=new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            //return Common.getNullRetObj(ret,httpServletResponse);
            return Common.decorateReturnObject(ret);

            //return ResponseUtil.fail(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
else if(ret1.equals(-2l)) {
             ret = new ReturnObject(ResponseCode.REGION_NOT_REACH);
            // return Common.getNullRetObj(ret, httpServletResponse);
             return Common.decorateReturnObject(ret);
         }
        httpServletResponse.setStatus(HttpStatus.SC_CREATED);
       // ret=new ReturnObject(ret1);
        //return Common.getRetObject(ret);
        return ResponseUtil.ok(ret1);

    }

    /**
     * 获得运费模板概要
     * @author 胡曼珑
     * @param id
     * @return
     */
    @ApiOperation(value = "获得运费模板概要")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="模板id", required = true, dataType="int", paramType="path",example = "1")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}")
    public Object getFreModelSummeryByModelId(@PathVariable Long shopId,@PathVariable Long id){
        Object ret=null;
            ReturnObject object=freightService.getFreModelSummeryByModelId(shopId,id);
            logger.debug("getAllSimpleOrders: id : "+id+" shopId: "+shopId);
            if(object.getCode().equals(ResponseCode.OK))
            {
                ret=Common.getRetObject(object);
            }
            else
            //ret=Common.getNullRetObj(object,httpServletResponse);
              ret=Common.decorateReturnObject(object);
        return ret;
    }

    /**
     * 管理员定义店铺的运费模板
     * @author 胡曼珑
     * @param id
     * @param vo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员定义店铺的运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="vo", value="运费模板资料", required = true, dataType="FreightInfoVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{id}/freightmodels")
    public Object createFreightModel(@PathVariable Long id, @Validated @RequestBody FreightInfoVo vo, BindingResult bindingResult){
        logger.debug("create freight model by shopId:"+id);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        Object ret=null;
        ReturnObject object=freightService.createFreightModel(id,vo);
        logger.debug("createFreightModel by: shopId : "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
        ret=Common.decorateReturnObject(object);

        return ret;
    }

    /**
     * 获得店铺中商品的运费模板
     * @author 胡曼珑
     * @param id
     * @return
     */
    @ApiOperation(value = "获得店铺中商品的运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="name", value="模板名称", required = false, dataType="string", paramType="query",example = "1"),
            @ApiImplicitParam(name="page", value="页码", required = false, dataType="int", paramType="query",example = "1"),
            @ApiImplicitParam(name="pageSize", value="每页数目", required = false, dataType="int", paramType="query",example = "10")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{id}/freightmodels")
    public Object getFreModelByShopId(@PathVariable Long id,@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize,@RequestParam(required = false) String name){
        logger.debug("getFreModelByShopId: page = " + page + " pageSize = "+ pageSize);

        page=(page==null||page<=0)?1:page;
        pageSize=(pageSize==null||page<=0)?10:pageSize;

        Object ret=null;
        ReturnObject<PageInfo<VoObject>> object=freightService.getFreModelByShopId(id,name,page,pageSize);
        logger.debug("getAllSimpleOrders: id : "+id);
        ret=Common.getPageRetObject(object);

        return ret;
    }

    /**
     * 管理员修改店铺的运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @param freightModelInfo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员修改店铺的运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="运费模板id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="freightModelInfo", value="运费模板资料", required = true, dataType="FreightSimpInfoVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/freightmodels/{id}")
    public Object editFreightModel(@PathVariable Long shopId,@PathVariable Long id, @Validated @RequestBody FreightSimpInfoVo freightModelInfo, BindingResult bindingResult){
        logger.debug("create freight model by shopId: "+ shopId + " id: "+id);

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        Object ret=null;
        ReturnObject object=freightService.editFreightModel(shopId,id,freightModelInfo);
        logger.debug("createFreightModel by: shopId : "+shopId + " id: "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
        ret=Common.decorateReturnObject(object);

        return ret;
    }

    /**
     * 定义默认运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "店家或管理员为商铺定义默认运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="商户ID", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="id", required = true, dataType="int", paramType="path",example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/default")
    public Object setDefaultModel(@PathVariable Long shopId,@PathVariable Long id,@LoginUser @ApiIgnore @RequestParam(required = false, defaultValue = "0") Long userId){
        logger.info("userId: "+userId);
        logger.debug("setDefaultModel: shopId : "+shopId+" id : "+id);
        Object ret=null;
        ReturnObject object=freightService.setDefaultModel(shopId,id);
        logger.debug("getAllSimpleOrders: id : "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
            ret=Common.decorateReturnObject(object);
        return ret;
    }

    /**
     * 管理员克隆店铺的运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员克隆店铺的运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺ID", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="模板id", required = true, dataType="int", paramType="path",example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/clone")
    public Object cloneModel(@PathVariable Long shopId,@PathVariable Long id){

        logger.info("cloneModel: shopId : "+shopId+" id : "+id);
        Object ret=null;
        ReturnObject object=freightService.cloneModel(shopId,id);
        logger.debug("cloneModel: id : "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            ret=Common.getRetObject(object);
        }
        else
           // ret=Common.getNullRetObj(object,httpServletResponse);
            ret=Common.decorateReturnObject(object);

        return ret;
    }

    /**
     * 删除运费模板
     * @author 胡曼珑
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "删除运费模板，需同步删除与商品的")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺ID", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="id", required = true, dataType="int", paramType="path",example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/freightmodels/{id}")
    public Object deleteModel(@PathVariable Long shopId,@PathVariable Long id){

        logger.info("deleteModel: shopId : "+shopId+" id : "+id);
        Object ret=null;
        ReturnObject object=freightService.deleteModel(shopId,id);
        logger.debug("deleteModel: id : "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {

            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
            ret=Common.decorateReturnObject(object);


        return ret;
    }

    /**
     * 店家或管理员查询某个（重量）运费模板的明细
     *
     * @param shopId 店铺id
     * @param id     运费模板id
     * @return 运费模板详细信息
     * @author ShiYu Liao
     * @Create 2020/12/5
     * @Modify 2020/12/5
     */
    @ApiOperation(value = "店家或管理员查询某个（重量）运费模板的明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "运费模板id", required = true, dataType = "int", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}/weightItems")
    public Object getFreightItemsById(@PathVariable(name = "shopId") Long shopId, @PathVariable(name = "id") Long id) {
        Object ret = null;
        ReturnObject returnObject = freightService.findFreightItemsById(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK) {
            ret = Common.getListRetObject(returnObject);
        } else {
            //ret=Common.getNullRetObj(returnObject,httpServletResponse);
            ret=Common.decorateReturnObject(returnObject);
            //ret = ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
            //ret=Common.decorateReturnObject(returnObject);
        }
        return ret;
    }

    /**
     * 店家或管理员查询件数运费模板的明细
     *
     * @param shopId 店铺id
     * @param id     运费模板id
     * @return 运费模板详细信息
     * @author ShiYu Liao
     * @Create 2020/12/7
     * @Modify 2020/12/7
     */
    @ApiOperation(value = "店家或管理员查询件数运费模板的明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "运费模板id", required = true, dataType = "int", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object getPieceItemsById(@PathVariable(name = "shopId") Long shopId, @PathVariable(name = "id") Long id) {
        Object ret = null;
        ReturnObject returnObject = freightService.findPieceItemsById(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK) {
            ret = Common.getListRetObject(returnObject);
        } else {
            //ret=Common.getNullRetObj(returnObject,httpServletResponse);
            ret=Common.decorateReturnObject(returnObject);
            //ret = ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
            //ret=Common.decorateReturnObject(returnObject);
        }
        return ret;
    }

    /**
     * 店家或管理员删除重量运费模板明细
     * 需要登陆
     * @param shopId
     * @param id
     * @author ShiYu Liao
     * @created 2020/12/7
     */
    @ApiOperation(value = "店家或管理员删除重量运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="运费明细id", required = true, dataType="int", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit
    @DeleteMapping("/shops/{shopId}/weightItems/{id}")
    public Object deleteWeightItem(@PathVariable(name = "shopId") Long shopId,@PathVariable(name = "id") Long id){
        Object ret = null;
        ReturnObject object=freightService.deleteWeightItem(shopId,id);

        if(object.getCode().equals(ResponseCode.OK))
        {
            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
        ret=Common.decorateReturnObject(object);
        return ret;
    }

    /**
     * 店家或管理员删除件数运费模板明细
     * 需要登陆
     * @param shopId
     * @param id
     * @author ShiYu Liao
     * @created 2020/12/7
     */
    @ApiOperation(value = "店家或管理员删除件数运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="运费明细id", required = true, dataType="int", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @Audit
    @DeleteMapping("/shops/{shopId}/pieceItems/{id}")
    public Object deletePieceItem(@PathVariable(name = "shopId") Long shopId,@PathVariable(name = "id") Long id){
        Object ret = null;
        ReturnObject object=freightService.deletePieceItem(shopId,id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
            ret=Common.decorateReturnObject(object);
        return ret;

    }

    /**
     * 管理员定义重量模板明细
     * @author:廖诗雨
     * @param shopId
     * @param id
     * @param vo
     * @return
     */
    @ApiOperation(value = "管理员定义重量模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="明细id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="vo", value="运费模板资料", required = true, dataType="WeightItemVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/weightItems")
    public Object createWeightItem(@PathVariable(name="shopId") Long shopId, @PathVariable(name="id") Long id, @RequestBody WeightItemVo vo){
        logger.debug("create weightItem by id:"+id);
        //校验前端数据
        /*Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }*/
        Object ret=null;
        ReturnObject object=freightService.createWeightItem(shopId,id,vo);
        logger.debug("createWeightItem by: id : "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
            ret=Common.decorateReturnObject(object);



        return ret;
    }

    /**
     * 管理员定义件数模板明细
     * @author:廖诗雨
     * @param shopId
     * @param id
     * @param vo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员定义件数模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="明细id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="vo", value="运费模板资料", required = true, dataType="PieceItemVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object createPieceItem(@PathVariable(name="shopId") Long shopId, @PathVariable(name="id") Long id, @Validated @RequestBody PieceItemVo vo, BindingResult bindingResult){
        logger.debug("create pieceItem by id:"+id);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        Object ret=null;
        ReturnObject object=freightService.createPieceItem(shopId,id,vo);
        logger.debug("createPieceItem by: id : "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            httpServletResponse.setStatus(HttpStatus.SC_CREATED);
            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
            ret=Common.decorateReturnObject(object);


        return ret;
    }

    /**
     * 店家或管理员修改重量运费模板明细
     * @author:廖诗雨
     * @param shopId
     * @param id
     * @param freightModelInfo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "店家或管理员修改重量运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="运费模板明细id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="freightModelInfo", value="运费模板资料", required = true, dataType="WeightItemVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/weightItems/{id}")
    public Object editFreightItem(@PathVariable(name="shopId") Long shopId,@PathVariable(name="id") Long id, @Validated @RequestBody WeightItemVo freightModelInfo, BindingResult bindingResult){
        logger.info("edit freightItem by shopId: "+ shopId + " id: "+id);

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }
        Object ret=null;
        ReturnObject object=freightService.editFreightItem(shopId,id,freightModelInfo);
        logger.info("editFreightItem by: shopId : "+shopId + " id: "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            ret=Common.getRetObject(object);

        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
            ret=Common.decorateReturnObject(object);



        return ret;
    }

    /**
     * 店家或管理员修改件数运费模板明细
     * @author:廖诗雨
     * @param shopId
     * @param id
     * @param freightModelInfo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "店家或管理员修改件数运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="运费模板明细id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="freightModelInfo", value="运费模板资料", required = true, dataType="PieceItemVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/pieceItems/{id}")
    public Object editPieceItem(@PathVariable(name="shopId") Long shopId, @PathVariable(name="id") Long id, @Validated @RequestBody PieceItemVo freightModelInfo, BindingResult bindingResult){
        logger.debug("edit pieceItem by shopId: "+ shopId + " id: "+id);

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);

        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        Object ret=null;
        ReturnObject object=freightService.editPieceItem(shopId,id,freightModelInfo);
        logger.debug("editPieceItem by: shopId : "+shopId + " id: "+id);
        if(object.getCode().equals(ResponseCode.OK))
        {
            ret=Common.getRetObject(object);
        }
        else
            //ret=Common.getNullRetObj(object,httpServletResponse);
            ret=Common.decorateReturnObject(object);


        return ret;
    }



}
