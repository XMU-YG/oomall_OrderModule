package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
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
 * @author Cai Xinlu  24320182203165
 * @date 2020-12-14 17:28
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
public class CaiXinluTest {
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

    private String login(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }


    /**
     * 通过aftersaleId查找refund 成功
     */
    @Test
    public void getRefundTest1() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/aftersales/{id}/payments",1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .jsonPath("$.data").exists()
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过aftersaleId查找refund  找不到路径上的aftersaleId
     */
    @Test
    public void getRefundTest2() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/aftersales/{id}/payments",666666)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 通过aftersaleId查找refund  orderId不属于Token解析出来的userId
     */
    @Test
    public void getRefundTest3() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/payment/aftersales/{id}/payments",2)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId查找refund  成功
     */
    @Test
    public void getRefundTest4() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBody();
    }

    /**
     * 通过orderId查找refund  找不到路径上的orderId
     */
    @Test
    public void getRefundTest5() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",666666)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();

    }

    /**
     * 通过orderId查找refund  orderId不属于Token解析出来的userId
     */
    @Test
    public void getRefundTest6() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",2)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  通过aftersaleId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest7() throws Exception{
        String token = this.login("537300010", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",666666,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  成功
     */
    @Test
    public void getRefundTest8() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",1,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  找不到aftersaleId
     */
    @Test
    public void getRefundTest9() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",1,666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId和shopId查找refund  通过orderId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest10() throws Exception{
        String token = this.login("537300010", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/orders/{id}/refunds",666666,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过orderId和shopId查找refund  成功
     */
    @Test
    public void getRefundTest11() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/orders/{id}/refunds",1,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId和shopId查找refund  找不到orderId
     */
    @Test
    public void getRefundTest12() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/orders/{id}/refunds",1,666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 修改运费模板 成功
     * @throws Exception
     */
    @Test
    public void changeFreightModel1() throws Exception
    {
        String token = this.login("13088admin", "123456");
        String freightJson = "{\"name\": \"freightModeTest\",\"unit\": 90}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/freightmodels/{id}",1,88888)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改运费模板  路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void changeFreightModel2() throws Exception
    {
        String token = this.login("537300010", "123456");
        String freightJson = "{\"name\": \"freightModel\",\"unit\": 100}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/freightmodels/{id}",2,88888)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改运费模板  运费模板名重复
     * @throws Exception
     */
    @Test
    public void changeFreightModel3() throws Exception
    {
        String token = this.login("13088admin", "123456");
        String freightJson = "{\"name\": \"freightModel2\",\"unit\": 100}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/freightmodels/{id}",1,99999)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.FREIGHTNAME_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }


    /**
     * 修改件数运费模板 运费模板中该地区已经定义  region已存在
     * @throws Exception
     */
    @Test
    public void changePieceFreightModel1() throws Exception
    {
        String token = this.login("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33,\n" +
                "    \"regionId\": 1\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/pieceItems/{id}",1,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改件数运费模板 路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void changePieceFreightModel2() throws Exception
    {
        String token = this.login("537300010", "123456");
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/pieceItems/{id}",2,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 件数运费模板修改成功
     * @throws Exception
     */
    @Test
    public void changePieceFreightModel3() throws Exception
    {
        String token = this.login("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/pieceItems/{id}",1,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改重量运费模板 运费模板中该地区已经定义  region已存在
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel1() throws Exception
    {
        String token = this.login("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391,\n" +
                "    \"regionId\": 1\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/weightItems/{id}",1,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 重量运费模板修改成功
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel2() throws Exception
    {
        String token = this.login("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/weightItems/{id}",1,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 修改重量运费模板 路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel3() throws Exception
    {
        String token = this.login("537300010", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/shops/{shopId}/weightItems/{id}",2,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }
}
