package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "商城订单服务", tags = "oomall")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/freight", produces = "application/json;charset=UTF-8")
public class FreightController {
    //创建日志实例
    private static final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    private FreightService freightService;
    //封装HTTP响应消息
    @Resource
    private HttpServletResponse httpServletResponse;

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
    @GetMapping("shops/{shopId}/freightmodels/{id}/weightItems")
    public Object getFreightItemsById(@PathVariable(name = "shopId") Long shopId, @PathVariable(name = "id") Long id) {
        Object ret = null;
        ReturnObject<List> returnObject = freightService.findFreightItemsById(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK) {
            ret = Common.getListRetObject(returnObject);
        } else {
            ret = ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
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
    @GetMapping("shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object getPieceItemsById(@PathVariable(name = "shopId") Long shopId, @PathVariable(name = "id") Long id) {
        Object ret = null;
        ReturnObject<List> returnObject = freightService.findPieceItemsById(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK) {
            ret = Common.getListRetObject(returnObject);
        } else {
            ret = ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
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
    //@Audit
    @DeleteMapping("/shops/{shopId}/weightItems/{id}")
    public Object deleteWeightItem(@PathVariable(name = "shopId") Long shopId,@PathVariable(name = "id") Long id){
        return freightService.deleteWeightItem(shopId,id);
    }
}
