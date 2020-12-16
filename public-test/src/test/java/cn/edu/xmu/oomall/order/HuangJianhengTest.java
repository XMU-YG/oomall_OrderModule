package cn.edu.xmu.oomall.order;


import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author 24320182203202 Jianheng HUANG
 * @date 2020-12-12
 */

@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
public class HuangJianhengTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;


    @BeforeEach
    public void setUp(){

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    private String login(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }


    /**
     * 1 获取商铺订单概要测试1
     * 正常访问本商铺的订单
     * @throws Exception
     */
    @Test
    @Order(0)
    public void getAllShopOrdersTest1() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?pageSize=2")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":1,\"pageSize\":2,\"total\":26,\"pages\":13,\"list\":[" +
                "{\"id\":240000,\"customerId\":2830,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607455773," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}," +
                "{\"id\":240001,\"customerId\":4298,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 2 获取商铺订单概要测试2
     * 正常访问本商铺的订单（分页）
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getAllShopOrdersTest2() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?pageSize=2&pageSize=5")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":2,\"pageSize\":5,\"total\":26,\"pages\":6,\"list\":[" +
                "{\"id\":240005,\"customerId\":5344,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}," +
                "{\"id\":240006,\"customerId\":2830,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}," +
                "{\"id\":240007,\"customerId\":4298,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}," +
                "{\"id\":240008,\"customerId\":5344,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}," +
                "{\"id\":240009,\"customerId\":2830,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 3 获取商铺订单概要测试3
     * 正常访问本商铺的订单（分页大小）
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getAllShopOrdersTest3() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?pageSize=2&pageSize=2")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":2,\"pageSize\":2,\"total\":26,\"pages\":13,\"list\":[" +
                "{\"id\":240002,\"customerId\":5344,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}," +
                "{\"id\":240003,\"customerId\":2830,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 4 获取商铺订单概要测试4
     * 正常访问本商铺的订单（订单号）
     * @throws Exception
     */
    @Test
    @Order(3)
    public void getAllShopOrdersTest4() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?orderSn=2020121229742&pageSize=1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":1,\"pageSize\":1,\"total\":1,\"pages\":1,\"list\":[" +
                "{\"id\":240025,\"customerId\":7,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":3,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 5 获取商铺订单概要测试5
     * 访问非本商铺的订单（订单号）
     * @throws Exception
     */
    @Test
    @Order(4)
    public void getAllShopOrdersTest5() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?orderSn=2020121229742&pageSize=1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不是自己的对象")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 6 获取商铺订单概要测试6
     * 访问不存在的订单（订单号）
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getAllShopOrdersTest6() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?orderSn=20190712576690000&pageSize=1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 7 获取商铺订单概要测试7
     * 正常访问本商铺的订单（顾客id）
     * @throws Exception
     */
    @Test
    @Order(6)
    public void getAllShopOrdersTest7() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?customerId=7&pageSize=1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse =  "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":1,\"pageSize\":1,\"total\":3,\"pages\":3,\"list\":[" +
                "{\"id\":240022,\"customerId\":7,\"shopId\":1," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null," +
                "\"freightPrice\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 8 获取商铺订单概要测试8
     * 访问非本商铺的订单（顾客id）
     * （顾客没下过本商铺的订单，但下过其他的订单）
     * （操作的资源id不存在）
     * @throws Exception
     */
    @Test
    @Order(7)
    public void getAllShopOrdersTest8() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?customerId=734&pageSize=1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 9 获取商铺订单概要测试9
     * 访问非本商铺的订单（顾客id+订单序列号）
     * （订单序列号存在但不属于该顾客）
     * （操作的资源id不存在）
     * @throws Exception
     */
    @Test
    @Order(8)
    public void getAllShopOrdersTest9() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?customerId=734&orderSn=2019121224844&pageSize=1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 10 获取商铺订单概要测试10
     * 访问非本商铺的订单（顾客id+订单序列号）
     * （订单序列号存在且属于该顾客但不属于本商铺）
     * （操作的资源id不存在）
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getAllShopOrdersTest10() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders?customerId=5344&orderSn=2019121224844&pageSize=1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不是自己的对象")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 11 获取商铺订单详细内容测试1
     * 访问不存在的订单
     * @throws Exception
     */
    @Test
    @Order(10)
    public void getShopOrderDetailsTest1() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders/66666666")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 12 获取商铺订单详细内容测试2
     * 访问不属于本商铺的订单
     * @throws Exception
     */
    @Test
    @Order(11)
    public void getShopOrderDetailsTest2() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders/2")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不是自己的对象")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 13 获取商铺订单详细内容测试3
     * 访问属于本商铺的订单
     * @throws Exception
     */
    @Test
    @Order(12)
    public void getShopOrderDetailsTest3() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.get()
                .uri("/shops/1/orders/240000")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不是自己的对象")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":240000," +
                "\"customer\":{\"id\":2830,\"userName\":\"99501125327\",\"name\":\"81671430060\"}," +
                "\"shop\":{\"id\":1,\"name\":\"super shop\",\"state\":null," +
                "\"gmtCreate\":\"2020-12-10T19:29:33\",\"gmtModified\":\"2020-12-10T19:29:33\"}," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607455773," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null,\"message\":null," +
                "\"regionId\":null,\"address\":null,\"mobile\":\"13959288888\",\"consignee\":\"刘慧\"," +
                "\"couponId\":null,\"grouponId\":null,\"orderItems\":[]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 14 店家留言测试1
     * 访问不存在的订单
     * @throws Exception
     */
    @Test
    @Order(13)
    public void addShopOrderMessageTest1() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/1/orders/66666666")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 15 店家留言测试2
     * 访问不属于本商铺的订单
     * @throws Exception
     */
    @Test
    @Order(14)
    public void addShopOrderMessageTest2() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/1/orders/2")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不是自己的对象")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 16 店家留言测试3
     * 访问属于本商铺的订单
     * @throws Exception
     */
    @Test
    @Order(15)
    public void addShopOrderMessageTest3() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/1/orders/240020")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        ret = manageClient.get()
                .uri("/shops/1/orders/240020")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        responseString = new String(ret, "UTF-8");
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":240020," +
                "\"customer\":{\"id\":4298,\"userName\":\"87996891967\",\"name\":\"57036045991\"}," +
                "\"shop\":{\"id\":1,\"name\":\"super shop\",\"state\":null," +
                "\"gmtCreate\":\"2020-12-10T19:29:33\",\"gmtModified\":\"2020-12-10T19:29:33\"}," +
                "\"pid\":null,\"orderType\":null,\"state\":6,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null,\"message\":\"6666\"," +
                "\"regionId\":null,\"address\":null,\"mobile\":\"13959288888\",\"consignee\":\"刘媛媛\"," +
                "\"couponId\":null,\"grouponId\":null,\"orderItems\":[]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }


    /**
     * 17 店家取消订单测试1
     * 访问不存在的订单
     * @throws Exception
     */
    @Test
    @Order(16)
    public void cancelShopOrderTest1() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/1/orders/66666666")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 18 店家取消订单测试2
     * 访问不属于本商铺的订单
     * @throws Exception
     */
    @Test
    @Order(17)
    public void cancelShopOrderTest2() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/1/orders/2")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不是自己的对象")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 19 店家取消订单测试3
     * 访问属于本商铺的订单
     * @throws Exception
     */
    @Test
    @Order(18)
    public void cancelShopOrderTest3() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/1/orders/240021")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        ret = manageClient.get()
                .uri("/shops/1/orders/240021")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        responseString = new String(ret, "UTF-8");
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":240021," +
                "\"customer\":{\"id\":1,\"userName\":\"8606245097\",\"name\":\"16886485849\"}," +
                "\"shop\":{\"id\":1,\"name\":\"super shop\",\"state\":null," +
                "\"gmtCreate\":\"2020-12-10T19:29:33\",\"gmtModified\":\"2020-12-10T19:29:33\"}," +
                "\"pid\":null,\"orderType\":null,\"state\":4,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null,\"message\":null," +
                "\"regionId\":null,\"address\":null,\"mobile\":\"13959288888\",\"consignee\":\"刘勤\"," +
                "\"couponId\":null,\"grouponId\":null,\"orderItems\":[]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 20 店家取消订单测试4
     * 访问属于本商铺的订单
     * 但当前订单状态为“已完成”
     * @throws Exception
     */
    @Test
    @Order(19)
    public void cancelShopOrderTest4() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/1/orders/240021")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("800")
                .jsonPath("$.errmsg").isEqualTo("订单状态禁止")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":800,\"errmsg\":\"订单状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 20 店家标记订单发货1
     * 访问不存在的订单
     * @throws Exception
     */
    @Test
    @Order(19)
    public void markShopOrderDeliverTest1() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/1/orders/66666666/deliver")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不存在")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 21 店家取消订单测试2
     * 访问不属于本商铺的订单
     * @throws Exception
     */
    @Test
    @Order(20)
    public void markShopOrderDeliverTest2() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/1/orders/2/deliver")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .jsonPath("$.errmsg").isEqualTo("操作的资源id不是自己的对象")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 22 店家取消订单测试2
     * 访问属于本商铺的订单
     * 但当前订单状态并非“付款完成”
     * @throws Exception
     */
    @Test
    @Order(21)
    public void markShopOrderDeliverTest3() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/1/orders/240024/deliver")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        ret = manageClient.get()
                .uri("/shops/1/orders/240024")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        responseString = new String(ret, "UTF-8");
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":240024," +
                "\"customer\":{\"id\":2,\"userName\":\"36040122840\",\"name\":\"92371771011\"}," +
                "\"shop\":{\"id\":1,\"name\":\"super shop\",\"state\":null," +
                "\"gmtCreate\":\"2020-12-10T19:29:33\",\"gmtModified\":\"2020-12-10T19:29:33\"}," +
                "\"pid\":null,\"orderType\":null,\"state\":24,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null,\"message\":null," +
                "\"regionId\":null,\"address\":null,\"mobile\":\"13959288888\",\"consignee\":\"刘恩羽\"," +
                "\"couponId\":null,\"grouponId\":null,\"orderItems\":[]}}";

        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 23 店家取消订单测试4
     * 访问属于本商铺的订单
     * 当前订单状态为“付款完成”
     * @throws Exception
     */
    @Test
    @Order(22)
    public void markShopOrderDeliverTest4() throws Exception {
        String token = this.login("537300010","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/1/orders/240019/deliver")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        ret = manageClient.get()
                .uri("/shops/1/orders/240019")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        responseString = new String(ret, "UTF-8");
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":" +
                "{\"id\":240019," +
                "\"customer\":{\"id\":2830,\"userName\":\"99501125327\",\"name\":\"81671430060\"}," +
                "\"shop\":{\"id\":1,\"name\":\"super shop\",\"state\":null," +
                "\"gmtCreate\":\"2020-12-10T19:29:33\",\"gmtModified\":\"2020-12-10T19:29:33\"}," +
                "\"pid\":null,\"orderType\":null,\"state\":24,\"subState\":null,\"gmtCreate\":1607628573," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null,\"message\":null," +
                "\"regionId\":null,\"address\":null,\"mobile\":\"13959288888\",\"consignee\":\"刘慧\"," +
                "\"couponId\":null,\"grouponId\":null,\"orderItems\":[]}}";

        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    

}

