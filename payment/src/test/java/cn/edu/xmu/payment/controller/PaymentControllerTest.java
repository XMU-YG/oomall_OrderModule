package cn.edu.xmu.payment.controller;

import cn.edu.xmu.payment.PaymentServiceApplication;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.encript.AES;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;




@SpringBootTest(classes = PaymentServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(PaymentControllerTest.class);

    @Autowired
    private MockMvc mvc;


    private final String createToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    //买家查询自己订单的支付信息
    @Test
    public void getOrderPaymentSelf() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/orders/1/payments").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"id\":1,\"amount\":0,\"actualAmount\":0,\"paymentPattern\":\"0\",\"payTime\":\"2020-12-10T19:29:50\",\"beginTime\":\"2020-12-10T19:29:50\",\"endTime\":\"2020-12-10T19:29:50\",\"orderId\":1,\"state\":0,\"gmtCreate\":\"2020-12-10T19:29:50\",\"gmtModified\":\"2020-12-10T19:29:50\",\"aftersaleId\":null},{\"id\":2,\"amount\":0,\"actualAmount\":0,\"paymentPattern\":\"0\",\"payTime\":\"2020-12-10T19:29:50\",\"beginTime\":\"2020-12-10T19:29:50\",\"endTime\":\"2020-12-10T19:29:50\",\"orderId\":1,\"state\":0,\"gmtCreate\":\"2020-12-10T19:29:50\",\"gmtModified\":\"2020-12-10T19:29:50\",\"aftersaleId\":null}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //管理员查询售后单的支付信息
    @Test
    public void getAftersalePaymentShop() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/shops/1/aftersales/1/payments").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"id\":20828,\"amount\":1104,\"actualAmount\":1104,\"paymentPattern\":\"001\",\"payTime\":\"2020-12-17T19:05:34\",\"beginTime\":\"2020-12-17T19:05:34\",\"endTime\":\"2020-12-17T19:35:34\",\"orderId\":null,\"state\":1,\"gmtCreate\":\"2020-12-17T19:05:34\",\"gmtModified\":null,\"aftersaleId\":1},{\"id\":20829,\"amount\":1104,\"actualAmount\":1104,\"paymentPattern\":\"002\",\"payTime\":\"2020-12-17T19:06:21\",\"beginTime\":\"2020-12-17T19:06:21\",\"endTime\":\"2020-12-17T19:36:21\",\"orderId\":null,\"state\":1,\"gmtCreate\":\"2020-12-17T19:06:21\",\"gmtModified\":null,\"aftersaleId\":1}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //买家查询自己售后单的支付信息
    @Test
    public void getAftersalePaymentSelf() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/aftersales/1/payments").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"id\":20828,\"amount\":1104,\"actualAmount\":1104,\"paymentPattern\":\"001\",\"payTime\":\"2020-12-17T19:05:34\",\"beginTime\":\"2020-12-17T19:05:34\",\"endTime\":\"2020-12-17T19:35:34\",\"orderId\":null,\"state\":1,\"gmtCreate\":\"2020-12-17T19:05:34\",\"gmtModified\":null,\"aftersaleId\":1},{\"id\":20829,\"amount\":1104,\"actualAmount\":1104,\"paymentPattern\":\"002\",\"payTime\":\"2020-12-17T19:06:21\",\"beginTime\":\"2020-12-17T19:06:21\",\"endTime\":\"2020-12-17T19:36:21\",\"orderId\":null,\"state\":1,\"gmtCreate\":\"2020-12-17T19:06:21\",\"gmtModified\":null,\"aftersaleId\":1}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //管理员查询订单的支付信息
    @Test
    public void getOrderPaymentShop() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/shops/1/orders/1/payments").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"id\":1,\"amount\":0,\"actualAmount\":0,\"paymentPattern\":\"0\",\"payTime\":\"2020-12-10T19:29:50\",\"beginTime\":\"2020-12-10T19:29:50\",\"endTime\":\"2020-12-10T19:29:50\",\"orderId\":1,\"state\":0,\"gmtCreate\":\"2020-12-10T19:29:50\",\"gmtModified\":\"2020-12-10T19:29:50\",\"aftersaleId\":null},{\"id\":2,\"amount\":0,\"actualAmount\":0,\"paymentPattern\":\"0\",\"payTime\":\"2020-12-10T19:29:50\",\"beginTime\":\"2020-12-10T19:29:50\",\"endTime\":\"2020-12-10T19:29:50\",\"orderId\":1,\"state\":0,\"gmtCreate\":\"2020-12-10T19:29:50\",\"gmtModified\":\"2020-12-10T19:29:50\",\"aftersaleId\":null}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }
    //管理员查询订单的退款
    @Test
    public void getOrderRefundShop() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/shops/1/orders/1/refunds").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":1,\"paymentId\":1,\"amount\":0,\"orderId\":1,\"aftersaleId\":null,\"state\":null,\"gmtCreate\":\"2020-12-17T11:42:04\",\"gmtModified\":null}}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //买家查询自己售后单的退款
    @Test
    public void getAftersaleRefundShop() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/shops/1/aftersales/1/refunds").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":2,\"paymentId\":2,\"amount\":0,\"orderId\":null,\"aftersaleId\":1,\"state\":null,\"gmtCreate\":\"2020-12-17T11:42:04\",\"gmtModified\":null}}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //买家查询自己订单的退款
    @Test
    public void getOrderRefundSelf() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/orders/1/refunds").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":1,\"paymentId\":1,\"amount\":0,\"orderId\":1,\"aftersaleId\":null,\"state\":null,\"gmtCreate\":\"2020-12-17T11:42:04\",\"gmtModified\":null}}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //买家查询自己售后单的退款
    @Test
    public void getAftersaleRefundSelf() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/aftersales/1/refunds").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":2,\"paymentId\":2,\"amount\":0,\"orderId\":null,\"aftersaleId\":1,\"state\":null,\"gmtCreate\":\"2020-12-17T11:42:04\",\"gmtModified\":null}}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }


}
