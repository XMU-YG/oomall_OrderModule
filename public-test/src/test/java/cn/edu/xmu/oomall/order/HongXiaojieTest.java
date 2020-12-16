package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 订单模块-支付服务、运费模板服务
 *
 * @author  24320182203196 洪晓杰
 * @date 2020/12/15 09:20
 */
@SpringBootTest(classes = Application.class)
@Slf4j
public class HongXiaojieTest {

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


    /**
     *买家为售后单创建支付单,success
     * @author 洪晓杰
     * @throws Exception
     */
    @Test
    public void createPaymentTestByAftersaleId() throws Exception{
        String token = this.login("13088admin", "123456");
        String paymentJson = "{\n" +
                "    \"price\": 1000,\n" +
                "    \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/aftersales/{id}/payments",47123)
                .header("authorization", token)
                .bodyValue(paymentJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }






    /**
     *买家为售后单创建支付单,创建失败
     * @author 洪晓杰
     * @throws Exception
     */
    @Test
    public void createPaymentTestByAftersaleId2() throws Exception{
        String token = this.login("13088admin", "123456");
        String paymentJson = "{\n" +
                "    \"price\": 1000,\n" +
                "    \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/aftersales/{id}/payments",59345)
                .header("authorization", token)
                .bodyValue(paymentJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }






    /**
     *买家通过aftersaleId查询自己的支付信息,success
     * @author 洪晓杰
     */
    @Test
    public void customerQueryPaymentByAftersaleId() throws Exception{
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/payment/aftersales/{id}/payments",47002)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();


        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 47003,\n" +
                "      \"amout\": 0,\n" +
                "      \"actualAmount\": 0,\n" +
                "      \"paymentPattern\": \"0\",\n" +
                "      \"payTime\": \"2020-12-02T20:33:14\",\n" +
                "      \"beginTime\": \"2020-12-02T20:33:14\",\n" +
                "      \"endTime\": \"2020-12-02T20:33:14\",\n" +
                "      \"orderId\": 47003,\n" +
                "      \"aftersaleId\": 47002,\n" +
                "      \"state\": 0,\n" +
                "      \"gmtCreated\": \"2020-12-02T20:33:14\",\n" +
                "      \"gmtModified\": \"2020-12-02T20:33:14\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     *买家查询自己的支付信息,没有对应的aftersaleId
     * @author 洪晓杰
     */
    @Test
    public void customerQueryPaymentByAftersaleId2() throws Exception{
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/payment/aftersales/{id}/payments",47989)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }



    /**
     * 买家通过shopId，aftersaleId查询自己的支付信息，success
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId1() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/payments",47001,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 47007,\n" +
                "      \"amout\": 0,\n" +
                "      \"actualAmount\": 0,\n" +
                "      \"paymentPattern\": \"0\",\n" +
                "      \"payTime\": \"2020-12-06T22:04:49\",\n" +
                "      \"beginTime\": \"2020-12-06T22:04:49\",\n" +
                "      \"endTime\": \"2020-12-06T22:04:49\",\n" +
                "      \"orderId\": 47007,\n" +
                "      \"aftersaleId\": 47011,\n" +
                "      \"state\": null,\n" +
                "      \"gmtCreated\": \"2020-12-14T17:05:00\",\n" +
                "      \"gmtModified\": \"2020-12-15T17:07:16\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 47009,\n" +
                "      \"amout\": 46,\n" +
                "      \"actualAmount\": 46,\n" +
                "      \"paymentPattern\": null,\n" +
                "      \"payTime\": null,\n" +
                "      \"beginTime\": null,\n" +
                "      \"endTime\": null,\n" +
                "      \"orderId\": 47007,\n" +
                "      \"aftersaleId\": 47011,\n" +
                "      \"state\": null,\n" +
                "      \"gmtCreated\": \"2020-12-14T17:11:14\",\n" +
                "      \"gmtModified\": null\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);


    }

    /**
     * 买家通过shopId，aftersaleId查询自己的支付信息，success
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId2() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/payments",57101,47004)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 67840,\n" +
                "      \"amout\": 0,\n" +
                "      \"actualAmount\": 0,\n" +
                "      \"paymentPattern\": null,\n" +
                "      \"payTime\": \"2020-12-06T22:05:59\",\n" +
                "      \"beginTime\": \"2020-12-06T22:05:59\",\n" +
                "      \"endTime\": \"2020-12-07T22:05:59\",\n" +
                "      \"orderId\": 123,\n" +
                "      \"aftersaleId\": 4,\n" +
                "      \"state\": 0,\n" +
                "      \"gmtCreated\": \"2020-12-06T22:05:59\",\n" +
                "      \"gmtModified\": \"2020-12-06T22:05:59\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);


    }


    /**
     * 买家通过shopId，aftersaleId查询自己的支付信息，查询不到：不存在对应的aftersaleId的记录
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId3() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/payments",57101,47007)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBody();


    }


    /**
     * 买家通过shopId，aftersaleId查询自己的支付信息，查询不到：不存在对应的aftersaleId的记录
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId4() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/payments",57101,47001)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBody();

    }


    /**
     * 买家通过shopId，aftersaleId查询自己的支付信息，查询不到：不存在对应的aftersaleId的记录
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId5() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/payments",57010,47004)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBody();

    }



    /**
     * 通过orderId查询支付记录，success
     * @author 洪晓杰
     */
    @Test
    public void userQueryPaymentTest() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/orders/{id}/payments",47123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 60840,\n" +
                "      \"amout\": 0,\n" +
                "      \"actualAmount\": 0,\n" +
                "      \"paymentPattern\": null,\n" +
                "      \"payTime\": \"2020-12-06T22:05:59\",\n" +
                "      \"beginTime\": \"2020-12-06T22:05:59\",\n" +
                "      \"endTime\": \"2020-12-07T22:05:59\",\n" +
                "      \"orderId\": 47123,\n" +
                "      \"aftersaleId\": 47004,\n" +
                "      \"state\": 0,\n" +
                "      \"gmtCreated\": \"2020-12-06T22:05:59\",\n" +
                "      \"gmtModified\": \"2020-12-06T22:05:59\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);

    }



    /**
     * 通过orderId查询支付记录，失败
     * @author 洪晓杰
     */
    @Test
    public void userQueryPaymentTest2() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/orders/{id}/payments",48230)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();

    }



    /**
     * 通过orderId、shopId查询支付记录，success
     * @author 洪晓杰
     */
    @Test
    public void queryPaymentTest() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/orders/{id}/payments",57101,47123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 67840,\n" +
                "      \"amout\": 0,\n" +
                "      \"actualAmount\": 0,\n" +
                "      \"paymentPattern\": null,\n" +
                "      \"payTime\": \"2020-12-06T22:05:59\",\n" +
                "      \"beginTime\": \"2020-12-06T22:05:59\",\n" +
                "      \"endTime\": \"2020-12-07T22:05:59\",\n" +
                "      \"orderId\": 67123,\n" +
                "      \"aftersaleId\": 123456789,\n" +
                "      \"state\": 0,\n" +
                "      \"gmtCreated\": \"2020-12-06T22:05:59\",\n" +
                "      \"gmtModified\": \"2020-12-06T22:05:59\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);


    }

    /**
     * 通过orderId查询支付记录，success
     * @author 洪晓杰
     */
    @Test
    public void queryPaymentTest1() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/orders/{id}/payments",48011,47123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBody();

    }


    /**
     * 通过orderId查询支付记录，失败
     * @author 洪晓杰
     */
    @Test
    public void queryPaymentTest2() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/orders/{id}/payments",57101,48223)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();

    }


    /**
     * 通过orderId查询支付记录，success
     * @author 洪晓杰
     */
    @Test
    public void createPaymentTest() throws Exception{
        String token = this.login("13088admin", "123456");
        String paymentJson = "{\n" +
                "    \"price\": 1000,\n" +
                "    \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/payment/orders/{id}/payments",47123)
                .header("authorization", token)
                .bodyValue(paymentJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 67835,\n" +
                "        \"amout\": 1000,\n" +
                "        \"actualAmount\": 1000,\n" +
                "        \"paymentPattern\": 2,\n" +
                "        \"paySn\": null,\n" +
                "        \"orderId\": 47123,\n" +
                "        \"aftersaleId\": null,\n" +
                "        \"state\": 0\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }




    /**
     * 通过orderId查询支付记录，success
     * @author 洪晓杰
     */
    @Test
    public void setupDefaultModelTest1() throws Exception{
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freight_models/{id}/default",47012,47008)
                .header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();


    }

    /**
     * 通过orderId查询支付记录，失败
     * @author 洪晓杰
     */
    @Test
    public void setupDefaultModelTest2() throws Exception{
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freight_models/{id}/default",47012,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.DEFAULTMODEL_EXISTED.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 通过orderId查询支付记录，失败
     * @author 洪晓杰
     */
    @Test
    public void setupDefaultModelTest3() throws Exception{
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freight_models/{id}/default",47005,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SHOP_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * 通过orderId查询支付记录，失败
     * @author 洪晓杰
     */
    @Test
    public void setupDefaultModelTest4() throws Exception{
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freight_models/{id}/default",47012,47013)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.MODEL_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 通过orderId查询支付记录，失败
     * @author 洪晓杰
     */
    @Test
    public void insertWeightFreightModelTest5() throws Exception{
        String token = this.login("13088admin", "123456");
        String weightFreightModelJson = "{\n" +
                "  \"abovePrice\": 0,\n" +
                "  \"fiftyPrice\": 0,\n" +
                "  \"firstWeight\": 0,\n" +
                "  \"firstWeightFreight\": 0,\n" +
                "  \"hundredPrice\": 0,\n" +
                "  \"regionId\": 0,\n" +
                "  \"tenPrice\": 0,\n" +
                "  \"trihunPrice\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/weightItems",47234,49314)
                .header("authorization", token)
                .bodyValue(weightFreightModelJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();


    }


    /**
     * 通过orderId查询支付记录，失败
     * @author 洪晓杰
     */
    @Test
    public void insertPieceFreightModelTest6()  throws Exception{
        String token = this.login("13088admin", "123456");
        String pieceFreightModelJson = "{\n" +
                "  \"abovePrice\": 0,\n" +
                "  \"fiftyPrice\": 0,\n" +
                "  \"firstWeight\": 0,\n" +
                "  \"firstWeightFreight\": 0,\n" +
                "  \"hundredPrice\": 0,\n" +
                "  \"regionId\": 0,\n" +
                "  \"tenPrice\": 0,\n" +
                "  \"trihunPrice\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/pieceItems",47234,49314)
                .header("authorization", token)
                .bodyValue(pieceFreightModelJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

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
        //endregion
    }


}
