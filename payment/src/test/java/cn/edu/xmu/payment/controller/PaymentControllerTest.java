package cn.edu.xmu.payment.controller;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.payment.PaymentServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;



import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = PaymentServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(PaymentControllerTest.class);

    @Autowired
    private MockMvc mvc;


    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

/*
@Test
//测试用户查找自己的分享
public void getShares1() throws Exception{

    String token = creatTestToken(2L,1L,100);

    String responseString = this.mvc.perform(get("/payment/orders/1/refunds").header("authorization",token))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andReturn().getResponse().getContentAsString();

    String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":1,\"orderId\":1,\"aftersaleId\":1,\"amount\":1,\"actualAmount\":1,\"aftersaleId\":1\"gmtCreate\":\"2020-12-15T11:09:22\",\"gmtModified\":\"2020-12-23T11:09:28\"}}";
    JSONAssert.assertEquals(expectedResponse, responseString, flase);


}
*/
}
