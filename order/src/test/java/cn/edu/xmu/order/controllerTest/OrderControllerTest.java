package cn.edu.xmu.order.controllerTest;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.order.OrderServiceApplication;
import cn.edu.xmu.order.factory.PostOrderFactory;
import cn.edu.xmu.order.model.vo.AddressVo;
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

import java.io.IOException;
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

    @Test
    public void createToken(){
        String responseString = null;
        //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE0MTUyMDE4NjBDIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjotMiwiZXhwIjoxNjA4NTMwNDE4LCJ1c2VySWQiOjMsImlhdCI6MTYwNzkzMDQxOH0.u_UhJ4T9IFcdi--E1Ka-w58yfQporUZfMn7McGs6w6o

        String token=new JwtHelper().createToken(1L,-2l,600000);
        System.out.println(token);
    }

    @Test
    public void aa() throws IOException {
        System.out.println(" add: "+ new String(Files.readAllBytes(Paths.get("src/test/resources/modifyAddress.json"))));
        System.out.println("98: "+ new String(Files.readAllBytes(Paths.get("src/test/resources/confirm_3_9999998.json"))));
        System.out.println("97:  "+new String(Files.readAllBytes(Paths.get("src/test/resources/trans_3_9999997.json"))));
        System.out.println("mess: "+new String(Files.readAllBytes(Paths.get("src/test/resources/modMess_shop.json"))));
        System.out.println("shop:  "+new String(Files.readAllBytes(Paths.get("src/test/resources/confirm_shop.json"))));
    }

    /**
     * 修改地址成功
     * @throws Exception
     */
    @Test
    public void modifyAddressTest1() throws Exception {
        String token=new JwtHelper().createToken(520123L,-2L,60);
        AddressVo addressVo=new AddressVo("yg",1L,"xmu","123456");
        String json=JacksonUtil.toJson(addressVo);
        System.out.println(json);
        String responseString=this.mvc.perform(put("/order/orders/998887778999").header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        try {
            responseString = this.mvc.perform(get("/order/orders/998887778999").header("authorization", token))
                    .andExpect(status().isOk())

                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse= "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 998887778999,\n" +
                "    \"customer\": {\n" +
                "      \"customerId\": 520123\n" +
                "    },\n" +
                "    \"orderSn\": \"20161023ooooo\",\n" +
                "    \"pid\": null,\n" +
                "    \"consignee\": \"yg\",\n" +
                "    \"regionId\": 1,\n" +
                "    \"address\": \"xmu\",\n" +
                "    \"mobile\": \"123456\",\n" +
                "    \"message\": null,\n" +
                "    \"state\": 1,\n" +
                "    \"substate\": 11\n" +
                "\n" +
                "  }\n" +
                "}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改地址失败，不是本人的 forbidden
     * @throws Exception
     */
    @Test
    public void modifyAddressTest2() throws Exception {
        String token=new JwtHelper().createToken(520123L,-2L,600000);
        AddressVo addressVo=new AddressVo("yg",1L,"xmu","123456");
        String json=JacksonUtil.toJson(addressVo);
        String responseString=this.mvc.perform(put("/order/orders/1").header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 修改地址失败，不存在 404 not found
     * @throws Exception
     */
    @Test
    public void modifyAddressTest3() throws Exception {
        String token=new JwtHelper().createToken(520123L,-2L,600000);
        AddressVo addressVo=new AddressVo("yg",1L,"xmu","123456");
        String json=JacksonUtil.toJson(addressVo);
        String responseString=this.mvc.perform(put("/order/orders/88854544154").header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 修改地址失败，状态禁止801
     * @throws Exception
     */
    @Test
    public void modifyAddressTest4() throws Exception {
        String token=new JwtHelper().createToken(520123L,-2L,600);
        AddressVo addressVo=new AddressVo("yg",1L,"xmu","123456");
        String json=JacksonUtil.toJson(addressVo);
        String responseString=this.mvc.perform(put("/order/orders/999999999").header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(ResponseCode.ORDER_STATENOTALLOW.getCode()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 修改地址失败，参数错误
     * @throws Exception
     */
    @Test
    public void modifyAddressTest5() throws Exception {
        String token=new JwtHelper().createToken(520123L,-2L,600000);
        AddressVo addressVo=new AddressVo("yg",1L,"","123456");
        String json=JacksonUtil.toJson(addressVo);
        String responseString=this.mvc.perform(put("/order/orders/999999999").header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }


    /**
     * 卖家确认收货成功
     * @throws Exception
     */
    @Test
    public void confirmByCusTest1() throws Exception {
        String token=new JwtHelper().createToken(520123L,-2l,60);
        String responseString = this.mvc.perform(put("/order/orders/9999998/confirm")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        try {
            responseString = this.mvc.perform(get("/order/orders/9999998").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/confirm_3_9999998.json")));
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 卖家确认收货失败 801
     * @throws Exception
     */
    @Test
    public void confirmByCusTest2() throws Exception {
        String token=new JwtHelper().createToken(520123L,-2l,60);
        String responseString = this.mvc.perform(put("/order/orders/998887778999/confirm")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(ResponseCode.ORDER_STATENOTALLOW.getCode()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * 卖家确认收货失败 404
     * @throws Exception
     */
    @Test
    public void confirmByCusTest3() throws Exception {
        String token=new JwtHelper().createToken(3l,-2l,60);
        String responseString = this.mvc.perform(put("/order/orders/789451111/confirm")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * 卖家确认收货失败  forbidden
     * @throws Exception
     */
    @Test
    public void confirmByCusTest4() throws Exception {
        String token=new JwtHelper().createToken(3l,-2l,60);
        String responseString = this.mvc.perform(put("/order/orders/1/confirm")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 转换成功
     * @throws Exception
     */
    @Test
    public void transOrderTest1() throws Exception {
        String token=new JwtHelper().createToken(520123L,-2l,60);
        String responseString = this.mvc.perform(post("/order/orders/9999997/groupon-normal")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        try {
            responseString = this.mvc.perform(get("/order/orders/9999997").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/trans_3_9999997.json")));
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 店家修改自己的订单留言成功
     * @throws Exception
     */
    @Test
    public void modifyMessageByShop1() throws Exception {
        String token=new JwtHelper().createToken(520560L,131499560L,60);
        String message="这是个很好的测试";
        String responseString = this.mvc.perform(put("/order/shops/131499560/orders/9999999999")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(message))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        try {
            responseString = this.mvc.perform(get("/order/shops/131499560/orders/9999999999").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/modMess_shop.json")));
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家修改自己的订单留言失败 返回404
     * @throws Exception
     */
    @Test
    public void modifyMessageByShop2() throws Exception {
        String token=new JwtHelper().createToken(520560L,131499560L,60);
        String message="这是个很好的测试";
        String responseString = this.mvc.perform(put("/order/shops/131499560/orders/9999900055")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(message))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }


    /**
     * 店家修改自己的订单留言失败  forbidden
     * @throws Exception
     */
    @Test
    public void modifyMessageByShop3() throws Exception {
        String token=new JwtHelper().createToken(520560L,131499560L,60);
        String message="这是个很好的测试";
        String responseString = this.mvc.perform(put("/order/shops/131499560/orders/1")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(message))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 店家修改自己的订单留言失败  参数错误
     * @throws Exception
     */
    @Test
    public void modifyMessageByShop4() throws Exception {
        String token=new JwtHelper().createToken(520560L,131499560L,60);
        String message="";
        String responseString = this.mvc.perform(put("/order/shops/131499560/orders/1")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(message))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 店铺成功标记发货
     * @throws Exception
     */
    @Test
    public void confirmByShop1() throws Exception {
        String shipSn="111133333";
        String token=new JwtHelper().createToken(520560L,131499560L,60);
        String responseString = this.mvc.perform(put("/order/shops/131499560/orders/9999999999/deliver")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(shipSn))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        try {
            responseString = this.mvc.perform(get("/order/shops/131499560/orders/9999999999").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/confirm_shop.json")));
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店铺标记发货 失败 404
     * @throws Exception
     */
    @Test
    public void confirmByShop2() throws Exception {
        String shipSn="111133333";
        String token=new JwtHelper().createToken(520560l,131499560L,60);
        String responseString = this.mvc.perform(put("/order/shops/131499560/orders/999999999999/deliver")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(shipSn))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * 店铺标记发货 失败 forbidden
     * @throws Exception
     */
    @Test
    public void confirmByShop3() throws Exception {
        String shipSn="111133333";
        String token=new JwtHelper().createToken(520560l,131499560L,60);
        String responseString = this.mvc.perform(put("/order/shops/131499560/orders/1/deliver")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(shipSn))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 店铺标记发货 失败 运费单号为空
     * @throws Exception
     */
    @Test
    public void confirmByShop4() throws Exception {
        String shipSn="";
        String token=new JwtHelper().createToken(520560l,131499560L,60);
        String responseString = this.mvc.perform(put("/order/shops/131499560/orders/1/deliver")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(shipSn))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

//String json= "{\"consignee\":\"yg\",\"regionId\":1,\"address\":\"xmu\",\"mobile\":\"123456\"}";
//
//    /**
//     * 买家成功取消自己发货前的订单
//     * @throws Exception
//     */
//    @Test
//    public void deleteCustomerOrder1() throws Exception {
//        String token=new JwtHelper().createToken(3l,-2l,60);
//        String responseString = this.mvc.perform(post("/order/orders/9999997/groupon-normal")
//                .header("authorization",token)
//                .contentType("application/json;charset=UTF-8"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//
//        try {
//            responseString = this.mvc.perform(get("/order/orders/9999997").header("authorization", token))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/trans_3_9999997.json")));
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 买家不能取消别人发货前的订单
//     * @throws Exception
//     */
//    @Test
//    public void deleteCustomerOrder2() throws Exception {
//
//        String token=new JwtHelper().createToken(1l,-2l,60);
//        String responseString = this.mvc.perform(delete("/order/orders/2").header("authorization",token).contentType("application/json;charset=UTF-8"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        //System.out.println(responseString);
//    }
//
//
//
//
//
//
//
//
//    /**
//     * 成功查找顾客订单概要
//     * @author Gang Ye
//     * @throws Exception
//     */
//    @Test
//    public void getAllSimpleOrderTest1() throws Exception {
//
//        String responseString = null;
//        String token=new JwtHelper().createToken(1L,-2L,6000);
//        try {
//            responseString = this.mvc.perform(get("/order/orders?page=1&pageSize=10").header("authorization", token))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/cus_1_simpleOrder.json")));
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 根据不存在订单号查找顾客订单
//     * @return code 504
//     * @author Gang Ye
//     * @throws Exception
//     */
//    @Test
//    public void getOrderTest1() throws Exception {
//
//        String responseString = null;
//        String token=new JwtHelper().createToken(3L,-2L,6000);
//        try {
//            responseString = this.mvc.perform(get("/order/orders/3").header("authorization", token))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String expectedResponse= new String(Files.readAllBytes(Paths.get("src/test/resources/cus_3_Order.json")));
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 根据订单号成功查找顾客订单
//     * @return
//     * @author Gang Ye
//     * @throws Exception
//     */
//    @Test
//    public void getOrderTest2() throws Exception {
//
//        String responseString = null;
//        String token = new JwtHelper().createToken(3L, -2L, 6000);
//        try {
//            responseString = this.mvc.perform(get("/order/orders/39").header("authorization", token))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/cus_3_Order.json")));
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 根据查找订单状态
//     * @return
//     * @author Gang Ye
//     * @throws Exception
//     */
//    @Test
//    public void getOrderStatesTest() throws Exception {
//
//        String responseString = null;
//        String token = new JwtHelper().createToken(3L, -2L, 6000);
//        try {
//            responseString = this.mvc.perform(get("/order/orders/states").header("authorization", token))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/order_states.json")));
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void createNorOrderTest(){
//        OrderVo orderVo=new OrderVo();
//        orderVo.setAddress("厦门市");
//        orderVo.setConsignee("xmu");
//        orderVo.setMessage("nothing");
//        orderVo.setMobile("123456");
//        orderVo.setRegionId(2L);
////        String orderInfo=JacksonUtil.toJson(orderVo);
////        String responseString = null;
////        String token = new JwtHelper().createToken(3L, -2L, 6000);
////        try {
////            responseString = this.mvc.perform(post("/order/orders").header("authorization", token)
////                    .param("orderInfo",orderInfo))
////                    .andExpect(status().isOk())
////                    .andExpect(content().contentType("application/json;charset=UTF-8"))
////                    .andReturn().getResponse().getContentAsString();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        //String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/order_states.json")));
////        try {
////            JSONAssert.assertEquals(expectedResponse, responseString, false);
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
//    }
}
