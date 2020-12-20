package cn.edu.xmu.user.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.bloom.RedisBloomFilter;
import cn.edu.xmu.user.mapper.UserPoMapper;
import cn.edu.xmu.user.model.po.UserPo;
import cn.edu.xmu.user.model.po.UserPoExample;
import cn.edu.xmu.user.model.vo.NewCustomerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NewCustomerDao {
    private  static  final Logger logger = LoggerFactory.getLogger(NewCustomerDao.class);
    @Autowired
    UserPoMapper userPoMapper;
    @Autowired
    RedisTemplate redisTemplate;

    RedisBloomFilter bloomFilter;

    String[] fieldName;
    final String suffixName="BloomFilter";

    /**
     * 通过该参数选择是否清空布隆过滤器
     */
    private boolean reinitialize=true;

    public ReturnObject checkBloomFilter(UserPo po){
        if(bloomFilter.includeByBloomFilter("email"+suffixName,po.getEmail())){
            return new ReturnObject(ResponseCode.EMAIL_REGISTERED);
        }
        if(bloomFilter.includeByBloomFilter("mobile"+suffixName,po.getMobile())){
            return new ReturnObject(ResponseCode.MOBILE_REGISTERED);
        }
        if(bloomFilter.includeByBloomFilter("userName"+suffixName,po.getUserName())){
            return new ReturnObject(ResponseCode.USER_NAME_REGISTERED);
        }
        return null;

    }

    public boolean isEmailExist(String email){
        logger.debug("is checking email in user table");
        UserPoExample example=new UserPoExample();
        UserPoExample.Criteria criteria=example.createCriteria();
        criteria.andEmailEqualTo(email);
        List<UserPo> userPos=userPoMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    public void setBloomFilterByName(String name,UserPo po) {
        try {
            Field field = UserPo.class.getDeclaredField(name);
            Method method=po.getClass().getMethod("get"+name.substring(0,1).toUpperCase()+name.substring(1));
            logger.debug("add value "+method.invoke(po)+" to "+field.getName()+suffixName);
            bloomFilter.addByBloomFilter(field.getName()+suffixName,method.invoke(po));
        }
        catch (Exception ex){
            logger.error("Exception happened:"+ex.getMessage());
        }
    }

    public boolean isMobileExist(String mobile){
        logger.debug("is checking mobile in user table");
        UserPoExample example=new UserPoExample();
        UserPoExample.Criteria criteria=example.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<UserPo> userPos=userPoMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    public boolean isUserNameExist(String userName){
        logger.debug("is checking userName in user table");
        UserPoExample example=new UserPoExample();
        UserPoExample.Criteria criteria=example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<UserPo> userPos=userPoMapper.selectByExample(example);
        //if(!userPos.isEmpty())logger.debug(userPos.get(0).getEmail()+"-"+AES.decrypt(userPos.get(0).getMobile(),User.AESPASS));
        return !userPos.isEmpty();
    }

    public ReturnObject createNewUserByVo(NewCustomerVo vo){
        UserPo userPo=new UserPo();
        ReturnObject returnObject=null;
        userPo.setEmail(vo.getEmail());
        userPo.setMobile(vo.getMobile());
        userPo.setPassword(vo.getPassword());
        if(vo.getGender()==0)
            userPo.setGender((byte) 0);
        else if(vo.getGender()==1)
            userPo.setGender((byte) 1);
        userPo.setRealName(vo.getReal_name());
        userPo.setBirthday(null);
        userPo.setPoint(0);
        userPo.setPoint(0);
        userPo.setBeDeleted((byte) 0);
        userPo.setGmtCreate(LocalDateTime.now());
        userPo.setGmtModified(LocalDateTime.now());
        try{
            returnObject=new ReturnObject<>(userPoMapper.selectByPrimaryKey((long) userPoMapper.insert(userPo)));
            logger.debug("success trying to insert newUser");
        }
        catch (DuplicateKeyException e){
            logger.debug("failed trying to insert newUser");
        }
        catch (Exception e){
            logger.error("Internal error Happened:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return returnObject;
    }
}
