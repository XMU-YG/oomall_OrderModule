package cn.edu.xmu.user.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.user.dao.NewCustomerDao;
import cn.edu.xmu.user.dao.CustomerDao;
import cn.edu.xmu.user.model.vo.NewCustomerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewCustomerService {
    private Logger logger = LoggerFactory.getLogger(NewCustomerService.class);

    @Autowired
    NewCustomerDao newCustomerDao;

    @Autowired
    CustomerDao customerDao;
    @Transactional
    public ReturnObject register(NewCustomerVo vo) {
        return newCustomerDao.createNewUserByVo(vo);
    }
}
