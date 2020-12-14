package cn.edu.xmu.order.controllerTest;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.order.OrderServiceApplication;
import cn.edu.xmu.order.factory.PostOrderFactory;
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.util.PostOrderService;
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
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    PostOrderFactory postOrderFactory;

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
        //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE0MTUyMDE4NjBDIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjotMiwiZXhwIjoxNjA4NTMwNDE4LCJ1c2VySWQiOjMsImlhdCI6MTYwNzkzMDQxOH0.u_UhJ4T9IFcdi--E1Ka-w58yfQporUZfMn7McGs6w6o

        String token=new JwtHelper().createToken(3L,-2l,600000);
        System.out.println(token);
    }

    /**
     * 成功查找顾客订单概要
     * @author Gang Ye
     * @throws Exception
     */
    @Test
    public void getAllSimpleOrderTest1() throws Exception {

        String responseString = null;
        String token=new JwtHelper().createToken(1L,-2L,6000);
        try {
            responseString = this.mvc.perform(get("/order/orders?page=1&pageSize=10").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/cus_1_simpleOrder.json")));
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据不存在订单号查找顾客订单
     * @return code 504
     * @author Gang Ye
     * @throws Exception
     */
    @Test
    public void getOrderTest1() throws Exception {

        String responseString = null;
        String token=new JwtHelper().createToken(3L,-2L,6000);
        try {
            responseString = this.mvc.perform(get("/order/orders/3").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/cus_3_Order.json")));
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 根据订单号成功查找顾客订单
     * @return
     * @author Gang Ye
     * @throws Exception
     */
    @Test
    public void getOrderTest2() throws Exception {

        String responseString = null;
        String token = new JwtHelper().createToken(3L, -2L, 6000);
        try {
            responseString = this.mvc.perform(get("/order/orders/39").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/cus_3_Order.json")));
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据查找订单状态
     * @return
     * @author Gang Ye
     * @throws Exception
     */
    @Test
    public void getOrderStatesTest() throws Exception {

        String responseString = null;
        String token = new JwtHelper().createToken(3L, -2L, 6000);
        try {
            responseString = this.mvc.perform(get("/order/orders/states").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/order_states.json")));
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createNorOrderTest(){
        OrderVo orderVo=new OrderVo();
        orderVo.setAddress("厦门市");
        orderVo.setConsignee("xmu");
        orderVo.setMessage("nothing");
        orderVo.setMobile("123456");
        orderVo.setRegionId(2L);
//        String orderInfo=JacksonUtil.toJson(orderVo);
//        String responseString = null;
//        String token = new JwtHelper().createToken(3L, -2L, 6000);
//        try {
//            responseString = this.mvc.perform(post("/order/orders").header("authorization", token)
//                    .param("orderInfo",orderInfo))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/order_states.json")));
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}
