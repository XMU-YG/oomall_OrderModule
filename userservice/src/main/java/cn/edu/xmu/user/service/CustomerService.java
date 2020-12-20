package cn.edu.xmu.user.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.user.dao.CustomerDao;
import cn.edu.xmu.user.model.bo.Customer;
import cn.edu.xmu.user.model.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Value("${privilegeservice.login.jwtExpire}")
    private Integer jwtExpireTime;

    @Autowired
    private CustomerDao customerDao;

//    @Autowired
//    private RedisTemplate<String, Serializable> redisTemplate;

    @Value("${privilegeservice.login.multiply}")
    private Boolean canMultiplyLogin;

    /**
     * 分布式锁的过期时间（秒）
     */
    @Value("${privilegeservice.lockerExpireTime}")
    private long lockerExpireTime;

    @Transactional
    public  ReturnObject login(String userName, String password) {
        // 获取用户 获取未被删除的
        ReturnObject retObj = customerDao.getUserByName(userName);
        if(retObj.getCode()!= ResponseCode.OK){
            return retObj;
        }
        Customer customer = (Customer) retObj.getData();
        // 比对密钥
        if(customer == null || !password.equals(customer.getPassword())){
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
        }

        // 校验状态
        if(customer.getState().equals(Customer.State.FORBID)){
            return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
        }

        // 不检查重复登录,创建新的token
        JwtHelper jwtHelper = new JwtHelper();
        String jwt = jwtHelper.createToken(customer.getId(),-2L,jwtExpireTime);
        return new ReturnObject<>(jwt);

    }

    public ReturnObject<VoObject> findUserById(Long id) {
        ReturnObject returnObject = customerDao.findUserById(id);
        return returnObject;
    }

    @Transactional
    public ReturnObject<Object> modifyUserInfo(Long userId, CustomerChangeVo vo) {
        return customerDao.modifyUserByVo(userId, vo);
    }

    @Transactional
    public ReturnObject<Object> modifyPassword(ModifyPwdVo vo) {
        return customerDao.modifyPassword(vo);
    }

    @Transactional
    public ReturnObject<Object> resetPassword(ResetPwdVo vo, String ip) {
        return customerDao.resetPassword(vo,ip);
    }

    public ReturnObject queryAllUser(CustomerCondition condition) {
        ReturnObject retObject = customerDao.queryUser(condition);
        return retObject;
    }


    public ReturnObject<Boolean> Logout(Long userId){
//        redisTemplate.delete("up_" + userId);
//        return new ReturnObject<>(true);
        return new ReturnObject<>(true);
    }

    @Transactional
    public ReturnObject<Object> banUser(Long id) {
        return customerDao.changeUserState(id, 6);
    }

    @Transactional
    public ReturnObject<Object> releaseUser(Long id){
        return customerDao.changeUserState(id, 4);
    }

}
