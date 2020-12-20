package cn.edu.xmu.user.service;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.user.dao.CustomerDao;
import cn.edu.xmu.user.model.User;
import cn.edu.xmu.user.model.po.UserPo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ：Zeyao Feng
 * @date ：Created in 2020/12/18 0:32
 */

public class CustomerInnerService implements  cn.edu.xmu.user.dubbo.UserService{

    @Autowired
    private CustomerDao customerDao;

    @Override
    public String findCustomerById(Long customerId) {
        ReturnObject ret= customerDao.findUserById(customerId);
        if(ret.getData()==null) return null;
        UserPo userPo = (UserPo) ret.getData();
        //组装DTO
        cn.edu.xmu.user.model.User user=new User();
        user.setId(userPo.getId());
        user.setName(userPo.getRealName());
        user.setUserName(userPo.getUserName());

        //转换为JSON
        String json= JacksonUtil.toJson(user);
        return json;
    }

    @Override
    public boolean reduceRebate(Long userId, Long rebate) {
        ReturnObject ret= customerDao.updateRebateById(userId,rebate);
        return ret.getCode().equals(ResponseCode.OK);
    }
}
