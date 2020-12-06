package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.model.vo.FreightInfoVo;
import cn.edu.xmu.freight.model.vo.FreightSimpInfoVo;
import cn.edu.xmu.freight.model.vo.PieceModelItemVo;
import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.freight.service.FreightService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;


@Api(value = "运费服务", tags = "freight")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/freight", produces = "application/json;charset=UTF-8")
public class FreightController {
    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    private FreightService freightService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得运费模板概要
     * @author:胡曼珑
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
    //@Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}")
    public Object getFreModelSummeryByModelId(@PathVariable Long shopId,@PathVariable Long id){
        Object ret=null;
            ReturnObject<VoObject> object=freightService.getFreModelSummeryByModelId(shopId,id);
            logger.debug("getAllSimpleOrders: id : "+id+" shopId: "+shopId);
            ret=Common.getRetObject(object);

        return ret;
    }

    /**
     * 管理员定义店铺的运费模板
     * @author:胡曼珑
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
    //@Audit
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
        ReturnObject<VoObject> object=freightService.createFreightModel(id,vo);
        logger.debug("createFreightModel by: shopId : "+id);
        ret=Common.getRetObject(object);

        return ret;
    }

    /**
     * 获得店铺中商品的运费模板
     * @author:胡曼珑
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
    //@Audit
    @GetMapping("/shops/{id}/freightmodels")
    public Object getFreModelByShopId(@PathVariable Long id,@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize,@RequestParam(required = false) String name){
        logger.debug("getFreModelByShopId: page = " + page + " pageSize = "+ pageSize);

        page=(page==null)?1:page;
        pageSize=(pageSize==null)?10:pageSize;

        Object ret=null;
        ReturnObject<PageInfo<VoObject>> object=freightService.getFreModelByShopId(id,name,page,pageSize);
        logger.debug("getAllSimpleOrders: id : "+id);
        ret=Common.getPageRetObject(object);

        return ret;
    }

    /**
     * 管理员修改店铺的运费模板
     * @author:胡曼珑
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
    //@Audit
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
        ReturnObject<VoObject> object=freightService.editFreightModel(shopId,id,freightModelInfo);
        logger.debug("createFreightModel by: shopId : "+shopId + " id: "+id);
        ret=Common.getRetObject(object);

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
    //@Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object setDefaultModel(@PathVariable Long shopId,@PathVariable Long id,@LoginUser @ApiIgnore @RequestParam(required = false, defaultValue = "0") Long userId){
        logger.info("userId: "+userId);
        logger.debug("setDefaultModel: shopId : "+shopId+" id : "+id);
        Object ret=null;
        ReturnObject<VoObject> object=freightService.setDefaultModel(shopId,id);
        logger.debug("getAllSimpleOrders: id : "+id);
        ret=Common.getRetObject(object);

        return ret;
    }

    /*
    @ApiOperation(value = "管理员定义件数模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺ID", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="id", value="id", required = true, dataType="int", paramType="path",example = "1"),
            @ApiImplicitParam(name="vo", value="运费模板资料", required = true, dataType="PieceModelItemVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/default")
    public Object createPieceFreight(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody PieceModelItemVo vo,BindingResult bindingResult){
        logger.debug("createPieceFreight: shopId : "+shopId+" id : "+id);

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        Object ret=null;

        ReturnObject<VoObject> object=freightService.createPieceFreight(shopId,id,vo);
        logger.debug("createPieceFreight: data : "+object.getData());
        ret=Common.getRetObject(object);

        return ret;
    }

     */


}
