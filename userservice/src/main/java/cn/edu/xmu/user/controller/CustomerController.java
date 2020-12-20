package cn.edu.xmu.user.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.user.model.bo.Customer;
import cn.edu.xmu.user.model.vo.*;
import cn.edu.xmu.user.service.NewCustomerService;
import cn.edu.xmu.user.service.CustomerService;
import cn.edu.xmu.user.util.IpUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "买家用户服务", tags = "user")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
public class CustomerController {

    private  static  final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private NewCustomerService newCustomerService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /***
     * 1、获得买家的所有状态
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="获得买家的所有状态")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @RequestMapping(value = "/users/states",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.GET)
    public Object getAllStates(){
        List<CustomerStateRetVo> stateVos=new ArrayList<>();
        Customer.State[] states= Customer.State.class.getEnumConstants();
        for(Customer.State state:states){
            stateVos.add(new CustomerStateRetVo(state));
        }
        return ResponseUtil.ok(stateVos);
    }

    /***
     * 2、注册用户
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="注册用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "NewUserVo", name = "vo", value = "vo", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @RequestMapping(value = "/users",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.POST)
    public Object register(@Validated @RequestBody NewCustomerVo vo, BindingResult result){

//        if(result.hasErrors()){
//            return Common.processFieldErrors(result,httpServletResponse);
//        }
        ReturnObject returnObject= newCustomerService.register(vo);
        if(returnObject.getCode()== ResponseCode.OK){
            return ResponseUtil.ok(returnObject.getData());
        }
        else return ResponseUtil.fail(returnObject.getCode());
    }

    /***
     * 3、买家查看自己信息
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="买家查看自己信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token", required = true)
    })
    @ApiResponses({
    })
    //@Audit
    @RequestMapping(value = "/users",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.GET)
    public Object getUserSelf(@LoginUser  @ApiIgnore Long userId) {
        userId=1L;
        ReturnObject returnObject=  customerService.findUserById(userId);
        return returnObject;
    }

    /***
     * 4、买家修改自己的信息
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="买家修改自己的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @RequestMapping(value = "/users",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.PUT)
//    @Audit // 需要认证
    public Object modifyUserInfo(@LoginUser  @ApiIgnore Long userId, @Validated @RequestBody CustomerChangeVo vo, BindingResult bindingResult) {
        userId=1L;
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        ReturnObject returnObj = customerService.modifyUserInfo(userId, vo);
        return returnObj;
    }

    /***
     * 5、用户修改密码
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="用户修改密码")
    @ApiResponses({
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 741, message = "不能与旧密码相同"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @RequestMapping(value = "/users/password",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.PUT)
    @ResponseBody
    public Object modifyPassword(@RequestBody ModifyPwdVo vo) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyPassword");
        }
        ReturnObject returnObject = customerService.modifyPassword(vo);
        return returnObject;
    }

    /***
     * 6、用户重置密码
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="用户重置密码")
    @ApiResponses({
            @ApiResponse(code = 745, message = "与系统预留的邮箱不一致"),
            @ApiResponse(code = 746, message = "与系统预留的电话不一致"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @RequestMapping(value = "/users/password/reset",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.PUT)
    @ResponseBody
    public Object resetPassword(@RequestBody ResetPwdVo vo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {

        if (logger.isDebugEnabled()) {
            logger.debug("resetPassword");
        }
        /* 处理参数校验错误 */
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }

        String ip = IpUtil.getIpAddr(httpServletRequest);

        ReturnObject returnObject = customerService.resetPassword(vo,ip);
        return returnObject;
    }

    /***
     * 7、平台管理员获取所有用户列表
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="平台管理员获取所有用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "userName", value = "用户名", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "email", value = "邮箱", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "mobile", value = "电话号码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功" )})
    @RequestMapping(value = "/users/all",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.GET)
    //@Audit
    public Object queryAllReturnUser(
                            @LoginUser  @ApiIgnore Long id,
                            @RequestParam(required = false)   String  userName,
                            @RequestParam(required = false)   String  email,
                            @RequestParam(required = false)   String  mobile,
                            @RequestParam(required = false, defaultValue = "1")  Integer page,
                            @RequestParam(required = false, defaultValue = "10")  Integer pageSize){
        CustomerCondition condition = new CustomerCondition(userName,email,mobile,page,pageSize);

        ReturnObject<PageInfo<VoObject>> returnObject = customerService.queryAllUser(condition);
        return returnObject;
    }

    /***
     * 8、用户密码登录
     * @return Object
     */
    @ApiOperation(value="用户密码登录")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @RequestMapping(value = "/users/login",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.POST)
    public Object login(@Validated @RequestBody LoginVo loginVo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest){
        /* 处理参数校验错误 */
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }
        ReturnObject jwt = customerService.login(loginVo.getUserName(), loginVo.getPassword());

        if(jwt.getData() == null){
            return ResponseUtil.fail(jwt.getCode(),jwt.getErrmsg());
        }else{
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return ResponseUtil.ok(jwt.getData());
        }
    }

    /***
     * 9、用户登出
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="用户登出")
    @Audit
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @RequestMapping(value = "/users/logout",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.GET)
    public Object logout(@LoginUser  @ApiIgnore Long userId){
        logger.debug("logout: userId = "+userId);
        ReturnObject<Boolean> success = customerService.Logout(userId);
        if (success.getData() == null)  {
            return ResponseUtil.fail(success.getCode(), success.getErrmsg());
        }else {
            return ResponseUtil.ok();
        }
    }

    /***
     * 10、管理员查看任意买家信息
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="管理员查看任意买家信息")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Long", name = "id",            value ="用户id",    required = true)
    })
    @RequestMapping(value = "/users/{id}",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.GET)
    public Object getUserById(@LoginUser  @ApiIgnore Long adid,@ApiParam(value = "用户id",required=true) @PathVariable("id") Long id) {

        ReturnObject returnObject = customerService.findUserById(id);

        return returnObject;
    }

    /***
     * 11、平台管理员封禁买家
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="平台管理员封禁买家")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path",   dataType = "Long", name = "did",value ="店id",required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Long", name = "id",value ="用户id",required = true)
    })
    //@Audit
    @RequestMapping(value = "/shops/{did}/users/{id}/ban",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.PUT)
    public Object banUser(@LoginUser  @ApiIgnore Long adId,
                          @ApiParam(value = "店id",required=true) @PathVariable("did") Long did,
                          @ApiParam(value = "用户id",required=true) @PathVariable("id") Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("forbidUser: id = "+ id);
        }
        ReturnObject returnObject = customerService.banUser(id);
        return returnObject;
    }

    /***
     * 12、平台管理员解禁买家
     * @return Object
     * createdBy: Gengchen Xu 2020-12-02 12:03
     */
    @ApiOperation(value="平台管理员解禁买家")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType = "path",   dataType = "Long", name = "did",value ="店id",required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Long", name = "id",value ="用户id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @RequestMapping(value = "/shops/{did}/users/{id}/release",
            produces = { "application/json;charset=UTF-8" },
            method = RequestMethod.PUT)
    public Object releaseUser(@LoginUser  @ApiIgnore Long adId,
                              @ApiParam(value = "店id",required=true) @PathVariable("did") Long did,
                              @ApiParam(value = "用户id",required=true) @PathVariable("id") Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("releaseUser: id = "+ id);
        }
        ReturnObject returnObject = customerService.releaseUser(id);
        return returnObject;
    }
}
