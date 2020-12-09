package cn.edu.xmu.order.controllerTest;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.order.OrderServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    MockMvc mvc;

    /**
     * 买家成功取消自己发货前的订单
     * @throws Exception
     */
    @Test
    public void deleteCustomerOrder1() throws Exception {

        String token=new JwtHelper().createToken(1l,-2l,60);
        String responseString = this.mvc.perform(delete("/order/orders/1").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.errno").value(ResponseCode.ORDER_STATENOTALLOW.getCode()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
    }

    /**
     * 买家不能取消别人发货前的订单
     * @throws Exception
     */
    @Test
    public void deleteCustomerOrder2() throws Exception {

        String token=new JwtHelper().createToken(1l,-2l,60);
        String responseString = this.mvc.perform(delete("/order/orders/2").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
    }

    @Test
    public void createToken(){
        String responseString = null;
        String token=new JwtHelper().createToken(1l,-2l,600000);
        System.out.println(token);
    }

    @Test
    public void getAllSimpleOrderTest1() throws Exception {

        String responseString = null;
        String token=new JwtHelper().createToken(1l,-1l,6000);
        System.out.println("**"+token);
        try {
            responseString = this.mvc.perform(get("/order/orders?orderSn=2016102378405&state=6&page=1&pageSize=10").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
//        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":23,\"name\":\"管理员\",\"desc\":\"超级管理员，所有权限都有\",\"createdBy\":1,\"departId\":0},{\"id\":80,\"name\":\"财务\",\"desc\":null,\"createdBy\":1,\"departId\":0}]},\"errmsg\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

}
