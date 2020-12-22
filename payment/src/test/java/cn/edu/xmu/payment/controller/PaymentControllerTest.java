package cn.edu.xmu.payment.controller;

import cn.edu.xmu.payment.PaymentServiceApplication;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.payment.model.vo.NewPaymentVo;
import cn.edu.xmu.payment.model.vo.NewRefundVo;
import cn.edu.xmu.payment.service.PaymentService;
import cn.edu.xmu.payment.util.PaymentPatterns;
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

    @Autowired
    private PaymentService paymentService;

    private final String createToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    //查看所有支付方式
    @Test
    public void getAllPaymentPattern() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/payment/payments/patterns").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"payPattern\":\"001\",\"name\":\"返点支付\"},{\"payPattern\":\"002\",\"name\":\"模拟支付渠道\"}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //查看所有支付状态
    @Test
    public void getAllPaymentStates() {
        String responseString = null;
        String token = createToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/payments/states").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"code\":0,\"name\":\"未支付\"},{\"code\":1,\"name\":\"已支付\"},{\"code\":2,\"name\":\"支付失败\"}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //新建两个订单支付 管理员和用户查询订单支付
    @Test
    public void createOrderPayment() throws Exception{
        String responseString=null;

        String token = createToken(1L, 0L, 100);

        //新建第一个订单支付
        NewPaymentVo vo1 =new NewPaymentVo();
        vo1.setPaymentPattern(PaymentPatterns.REBATEPAY.getCode());
        vo1.setPrice(54L);

        try{
            responseString = this.mvc.perform(post("/orders/40000/payments").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo1)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        //新建第二个订单支付
        NewPaymentVo vo2 =new NewPaymentVo();
        vo2.setPaymentPattern(PaymentPatterns.NORMALPAY.getCode());
        vo2.setPrice(1104L);

        try{
            responseString = this.mvc.perform(post("/orders/40000/payments").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo2)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        //买家查询上面创建的两个支付
        try {
            responseString = this.mvc.perform(get("/orders/40000/payments").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"amount\":54,\"actualAmount\":54,\"paymentPattern\":\"001\",\"orderId\":40000,\"state\":1,\"aftersaleId\":null},{\"amount\":1104,\"actualAmount\":1104,\"paymentPattern\":\"002\",\"orderId\":40000,\"state\":1,\"aftersaleId\":null}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        //管理员查询
        try {
            responseString = this.mvc.perform(get("/shops/1/orders/40000/payments").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"amount\":54,\"actualAmount\":54,\"paymentPattern\":\"001\",\"orderId\":40000,\"state\":1,\"aftersaleId\":null},{\"amount\":1104,\"actualAmount\":1104,\"paymentPattern\":\"002\",\"orderId\":40000,\"state\":1,\"aftersaleId\":null}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

    //新建两个售后支付 管理员和用户查询售后支付
    @Test
    public void createAftersalePayment() throws Exception{
        String responseString=null;

        String token = createToken(1L, 0L, 100);

        //新建第一个售后单支付
        NewPaymentVo vo1 =new NewPaymentVo();
        vo1.setPaymentPattern(PaymentPatterns.REBATEPAY.getCode());
        vo1.setPrice(54L);

        try{
            responseString = this.mvc.perform(post("/aftersales/1/payments").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo1)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        //新建第二个售后单支付
        NewPaymentVo vo2 =new NewPaymentVo();
        vo2.setPaymentPattern(PaymentPatterns.NORMALPAY.getCode());
        vo2.setPrice(1104L);

        try{
            responseString = this.mvc.perform(post("/aftersales/1/payments").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo2)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        //买家查询售后单支付
        try {
            responseString = this.mvc.perform(get("/aftersales/1/payments").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"amount\":54,\"actualAmount\":54,\"paymentPattern\":\"001\",\"orderId\":null,\"state\":1,\"aftersaleId\":1},{\"amount\":1104,\"actualAmount\":1104,\"paymentPattern\":\"002\",\"orderId\":null,\"state\":1,\"aftersaleId\":1}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        //管理员查询售后单支付
        try {
            responseString = this.mvc.perform(get("/shops/1/aftersales/1/payments").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"amount\":54,\"actualAmount\":54,\"paymentPattern\":\"001\",\"orderId\":null,\"state\":1,\"aftersaleId\":1},{\"amount\":1104,\"actualAmount\":1104,\"paymentPattern\":\"002\",\"orderId\":null,\"state\":1,\"aftersaleId\":1}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    //新建订单退款
    @Test
    public void createRefund() throws Exception{
        String responseString=null;
        String token = createToken(1L, 0L, 100);

        //新建订单退款
        NewRefundVo vo=new NewRefundVo();
        vo.setAmount(0L);

        try{
            responseString = this.mvc.perform(post("/shops/1/payments/1/refunds").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        //管理员查询订单退款
        try {
            responseString = this.mvc.perform(get("/shops/1/orders/1/refunds").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"paymentId\":1,\"amount\":0,\"orderId\":1,\"aftersaleId\":null,\"state\":1}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        //买家查询自己订单的退款
        try {
            responseString = this.mvc.perform(get("/orders/1/refunds").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"paymentId\":1,\"amount\":0,\"orderId\":1,\"aftersaleId\":null,\"state\":1}]}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
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
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"id\":1,\"amount\":0,\"actualAmount\":0,\"paymentPattern\":\"0\",\"payTime\":\"2020-12-10T19:29:50\",\"beginTime\":\"2020-12-10T19:29:50\",\"endTime\":\"2020-12-10T19:29:50\",\"orderId\":1,\"state\":0,\"gmtCreate\":\"2020-12-10T19:29:50\",\"gmtModified\":\"2020-12-10T19:29:50\",\"aftersaleId\":1}]}";
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

    //管理员查询自己售后单的退款
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

    @Test
    public void f(){

        if(paymentService.orderPayed(1L,13L)){
            System.out.println("yse");
        }else
            System.out.println("no");
    }


    @Test
    public void createToken(){
        String responseString = null;
        //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE0MTUyMDE4NjBDIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjotMiwiZXhwIjoxNjA4NTMwNDE4LCJ1c2VySWQiOjMsImlhdCI6MTYwNzkzMDQxOH0.u_UhJ4T9IFcdi--E1Ka-w58yfQporUZfMn7McGs6w6o

        String token=new JwtHelper().createToken(1L,-2l,60000000);
        System.out.println(token);
    }
}
