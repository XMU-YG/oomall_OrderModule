package cn.edu.xmu.user.dao;

import cn.edu.xmu.ooad.util.RandomCaptcha;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.user.mapper.UserPoMapper;
import cn.edu.xmu.user.model.bo.Customer;
import cn.edu.xmu.user.model.po.UserPo;
import cn.edu.xmu.user.model.po.UserPoExample;
import cn.edu.xmu.user.model.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;


@Repository
public class CustomerDao {
    @Autowired
    private UserPoMapper userPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(CustomerDao.class);

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;


    @Autowired
    private JavaMailSender mailSender;

    public ReturnObject<Object> findUserById(Long Id) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(Id);
        UserPo userPo=null;

        try{
            userPo = userPoMapper.selectByPrimaryKey(Id);
        }
        catch (DataAccessException e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

        return new ReturnObject(userPo);
    }

    public ReturnObject<Object> modifyUserByVo(Long id, CustomerChangeVo customerChangeVo) {
        UserPo po = userPoMapper.selectByPrimaryKey(id);
        po.setBirthday(customerChangeVo.getBirthday());
        if(customerChangeVo.getGender().equals("男"))
            po.setGender((byte) 0);
        else if(customerChangeVo.getGender().equals("女"))
            po.setGender((byte) 1);
        po.setRealName(customerChangeVo.getRealname());
        ReturnObject<Object> retObj=null;
        userPoMapper.updateByPrimaryKeySelective(po);
        return retObj;
    }

    public ReturnObject<Object> modifyPassword(ModifyPwdVo modifyPwdVo) {

        //通过验证码取出id
        if(!redisTemplate.hasKey("cp_"+modifyPwdVo.getCaptcha()))
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
        String id= redisTemplate.opsForValue().get("cp_"+modifyPwdVo.getCaptcha()).toString();

        UserPo userpo = null;
        try {
            userpo = userPoMapper.selectByPrimaryKey(Long.parseLong(id));
        }catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }

        //新密码与原密码相同
        if(AES.decrypt(userpo.getPassword(), Customer.AESPASS).equals(modifyPwdVo.getNewPassword()))
            return new ReturnObject<>(ResponseCode.PASSWORD_SAME);

        //加密
        UserPo userPo = new UserPo();
        userPo.setPassword(AES.encrypt(modifyPwdVo.getNewPassword(), Customer.AESPASS));

        //更新数据库
        try {
            userPoMapper.updateByPrimaryKeySelective(userPo);
        }catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject<Object> resetPassword(ResetPwdVo vo, String ip) {

//        //防止重复请求验证码
//        if(redisTemplate.hasKey("ip_"+ip))
//            return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
//        else {
//            //1 min中内不能重复请求
//            redisTemplate.opsForValue().set("ip_"+ip,ip);
//            redisTemplate.expire("ip_" + ip, 60*1000, TimeUnit.MILLISECONDS);
//        }

        //验证邮箱、手机号
        UserPoExample userPoExample1 = new UserPoExample();
        UserPoExample.Criteria criteria = userPoExample1.createCriteria();
        criteria.andMobileEqualTo(AES.encrypt(vo.getMobile(), Customer.AESPASS));
        List<UserPo> userPo1 = null;
        try {
            userPo1 = userPoMapper.selectByExample(userPoExample1);
        }catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(userPo1.isEmpty())
            return new ReturnObject<>(ResponseCode.MOBILE_WRONG);
        else if(!userPo1.get(0).getEmail().equals(AES.encrypt(vo.getEmail(), Customer.AESPASS)))
            return new ReturnObject<>(ResponseCode.EMAIL_WRONG);


        //随机生成验证码
        String captcha = RandomCaptcha.getRandomString(6);
//        while(redisTemplate.hasKey(captcha))
//            captcha = RandomCaptcha.getRandomString(6);

        String id = userPo1.get(0).getId().toString();
        String key = "cp_" + captcha;
        //key:验证码,value:id存入redis
//        redisTemplate.opsForValue().set(key,id);
        //五分钟后过期
//        redisTemplate.expire("cp_" + captcha, 5*60*1000, TimeUnit.MILLISECONDS);


//        //发送邮件(请在配置文件application.properties填写密钥)
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setSubject("【oomall】密码重置通知");
//        msg.setSentDate(new Date());
//        msg.setText("您的验证码是：" + captcha + "，5分钟内有效。");
//        msg.setFrom("925882085@qq.com");
//        msg.setTo(vo.getEmail());
//        try {
//            mailSender.send(msg);
//        } catch (MailException e) {
//            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
//        }

        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject queryUser(CustomerCondition condition) {
        /* 构建查询语句 */
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        if(null != condition.getUserName()){
            criteria.andUserNameEqualTo(condition.getUserName());
        }
        if(null != condition.getEmail()){
            criteria.andEmailEqualTo(condition.getEmail());
        }
        if(null != condition.getMobile()){
            criteria.andMobileEqualTo(condition.getMobile());
        }
        Integer page = condition.getPage();
        Integer pageSize = condition.getPageSize();

        // 开启分页查询
        PageHelper.startPage(page, pageSize);
        List<UserPo> userPos=null;
        try{
            userPos=userPoMapper.selectByExample(example);
        }
        catch (DataAccessException e){
            logger.error("selectAllUsers: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

        return new ReturnObject<>(new PageInfo<>(userPos));

    }

    public ReturnObject<Customer> getUserByName(String userName) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<UserPo> customers = null;
        try {
            logger.debug("query userName"+userName);
            customers = userPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            StringBuilder message = new StringBuilder().append("getUserByName: ").append(e.getMessage());
            logger.error(message.toString());
        }

        if (null == customers || customers.isEmpty()) {
            return new ReturnObject<>();
        } else {
            Customer customer = new Customer(customers.get(0));
            return new ReturnObject<>(customer);
        }
    }

    public ReturnObject<Object> changeUserState(Long id,Integer state) {
        UserPo po=userPoMapper.selectByPrimaryKey(id);
        if(state==4){
            po.setState((byte) 4);
        }
        else if(state==6){
            po.setState((byte) 6);
        }
        userPoMapper.updateByPrimaryKeySelective(po);

        ReturnObject<Object> retObj=null;
        return retObj;
    }

    private UserPo createUserStateModPo(Long id, Customer.State state) {
        // 查询密码等资料以计算新签名
        UserPo orig = userPoMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户的状态
        if (orig == null || (orig.getState() != null && Customer.State.getTypeByCode(orig.getState().intValue()) == Customer.State.DELETE)) {
            return null;
        }

        // 构造 User 对象以计算签名
        Customer customer = new Customer(orig);
        customer.setState(state);
        // 构造一个全为 null 的 vo 因为其他字段都不用更新
        CustomerVo vo = new CustomerVo();

        return customer.createUpdatePo(vo);
    }

    /**
    * 更新用户返点
    * @author: Zeyao Feng
    * @date: Created in 2020/12/18 2:07
    */
    public ReturnObject updateRebateById(Long userId,Long rebate){
        //先查询用户当前的返点
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        UserPo userPo=null;

        try {
            userPo = userPoMapper.selectByPrimaryKey(userId);
        } catch (DataAccessException e) {
            logger.error("updateRebateById: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

        if(userPo==null){
            logger.debug("userId not found:"+userId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        //检查该用户的返点
        if(userPo.getPoint()<rebate){
            logger.debug("user rebate not enough:"+userId);
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }

        //用户返点足够时，扣返点
        criteria.andIdEqualTo(userPo.getId());
        long afterRebate=userPo.getPoint()-rebate;
        userPo.setPoint((int)afterRebate);

        try {
            // 第一个参数是修改值，第二个参数是对应的查询条件
            int ret = userPoMapper.updateByPrimaryKeySelective(userPo);
            if (ret == 0) {
                // 修改失败
                logger.debug("updateRebate failed");
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                // 修改成功
                return new ReturnObject(ResponseCode.OK);
            }
        } catch (DataAccessException e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误:" + e.getMessage()));

        } catch (Exception e) {
           return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误" + e.getMessage()));
        }

    }


}
