package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.JacksonUtil;
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
 * 测试
 * @author cxr
 * @date 2020/12/11 7:54
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
public class ChenXiaoruTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp(){
        manageClient = WebTestClient.bindToServer()
                .baseUrl(managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        mallClient = WebTestClient.bindToServer()
                .baseUrl(mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     * 未登录获得广告的所有状态
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest1() throws Exception {
        byte[] ret = manageClient.get().uri("/advertisement/states")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 使用伪造token获得广告的所有状态
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest2() throws Exception {
        byte[] ret = manageClient.get().uri("/advertisement/states")
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 获得广告的所有状态
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        //String token = this.userLogin("8606245097","123456");
        byte[] responseString = manageClient.get().uri("/advertisement/states").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                //"  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"code\": 0,\n" +
                "      \"name\": \"待审核\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 4,\n" +
                "      \"name\": \"上架\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 6,\n" +
                "      \"name\": \"下架\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员设置默认广告
     * 用户设置默认广告，没有权限
     * @throws Exception
     */
    @Test
    public void advertiseTest4() throws Exception {
        String token = this.userLogin("8606245097","123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/default",1,421)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult().getResponseBodyContent();
    }

    /**
     * 管理员设置默认广告
     * 操作的广告不存在
     * @throws Exception
     */
    @Test
    public void advertiseTest5() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/1/default")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 管理员设置默认广告
     * 上架态的广告从默认变为非默认
     * @throws Exception
     */
    @Test
    public void advertiseTest6() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/default",0,421).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='421')].beDefault").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 管理员设置默认广告
     * 下架态的广告从非默认变为默认
     * @throws Exception
     */
    @Test
    public void advertiseTest7() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/422/default").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data.list[?(@.id=='422')].beDefault").isEqualTo(1)
                .returnResult()
                .getResponseBodyContent();
        log.error(new String(ret,"UTF-8"));
    }

    /**
     * 管理员设置默认广告
     * 审核态的广告从非默认变为默认
     * @throws Exception
     */
    @Test
    public void advertiseTest8() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/423/default").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data.list[?(@.id=='422')].beDefault").isEqualTo(1)
                .returnResult()
                .getResponseBodyContent();
        log.error(new String(ret,"UTF-8"));
    }

    /**
     * 管理员修改广告内容
     * 操作的广告不存在
     * @throws Exception
     */
    @Test
    public void advertiseTest9() throws Exception{
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("content", "广告内容1");
        body.put("beginDate", "2020-12-1");
        body.put("endDate", "2020-12-2");
        body.put("weight", "5");
        body.put("repeat", true);
        body.put("link", "link1");
        String requireJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/1")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员修改广告内容
     * 传入一个空的body
     * @throws Exception
     */
    @Test
    public void advertiseTest10() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 121)
                .header("authorization", token)
                .bodyValue("")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入开始日期和结束日期格式错误
     * @throws Exception
     */
    @Test
    public void advertiseTest11() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"beginDate\": \"2020/01/12\",\n" +
                "  \"endDate\": \"2020/03/06\",\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 121)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入开始日期值不合理
     * @throws Exception
     */
    @Test
    public void advertiseTest12() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"beginDate\": \"2020-02-30\",\n" +
                "  \"endDate\": \"2020-03-06\",\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 121)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入开始日期大于结束日期
     * @throws Exception
     */
    @Test
    public void advertiseTest13() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"beginDate\": \"2020-12-12\",\n" +
                "  \"endDate\": \"2020-03-06\",\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 121)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入的广告排序权重不合法(无法转换为数字）
     * @throws Exception
     */
    @Test
    public void advertiseTest14() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\"weight\": \"abc\"}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 121)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入的是否为每日重复广告值不合理(无法转换为布尔值）
     * @throws Exception
     */
    @Test
    public void advertiseTest15() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\"repeat\": abc}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 121)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 修改开始日期和结束日期之后该时段下的广告数超过限制
     * @throws Exception
     */
    @Test
    public void advertiseTest16() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"beginDate\": \"2000-01-20\",\n" +
                "  \"endDate\": \"2020-12-12\",\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 122)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 修改开始日期和结束日期之后该时段下的广告数超过限制
     * @throws Exception
     */
    @Test
    public void advertiseTest17() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"广告内容1\",\n" +
                "  \"beginDate\": \"2020-12-1\",\n" +
                "  \"endDate\": \"2020-12-2\",\n" +
                "  \"weight\": \"5\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"link1\"\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 122)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除某一个广告
     * 操作的资源不存在
     * @throws Exception
     */
    @Test
    public void advertiseTest18() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{did}/advertisement/{id}", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员删除某一个广告
     * 上架态广告删除成功
     * @throws Exception
     */
    @Test
    public void advertiseTest19() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{did}/advertisement/{id}", 0, 147)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除某一个广告
     * 下架态广告删除成功
     * @throws Exception
     */
    @Test
    public void advertiseTest20() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{did}/advertisement/{id}", 0, 146)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除某一个广告
     * 审核态广告删除成功
     * @throws Exception
     */
    @Test
    public void advertiseTest21() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{did}/advertisement/{id}", 0, 145)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员上架广告
     * 操作的资源不存在
     * @throws Exception
     */
    @Test
    public void advertiseTest22() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员上架广告
     * 操作的广告状态为审核态（失败）
     * @throws Exception
     */
    @Test
    public void advertiseTest23() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 144)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员上架广告
     * 操作的广告状态为上架态（失败）
     * @throws Exception
     */
    @Test
    public void advertiseTest24() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 143)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员上架广告
     * 操作的广告状态为下架态（成功）
     * @throws Exception
     */
    @Test
    public void advertiseTest25() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 142)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员下架广告
     * 操作的广告状态为审核态（失败）
     * @throws Exception
     */
    @Test
    public void advertiseTest26() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/offshelves", 0, 141)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员下架广告
     * 操作的广告状态为下架态（失败）
     * @throws Exception
     */
    @Test
    public void advertiseTest27() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/offshelves", 0, 140)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员下架广告
     * 操作的广告状态为上架态（成功）
     * @throws Exception
     */
    @Test
    public void advertiseTest28() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/offshelves", 0, 139)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核广告
     * 操作的广告状态为上架态（失败）
     * @throws Exception
     */
    @Test
    public void advertiseTest29() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"conclusion\": true,\n" +
                "  \"message\": \"审核通过\"\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/audit", 0, 138)
                .header("authorization",token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核广告
     * 操作的广告状态为下架态（失败）
     * @throws Exception
     */
    @Test
    public void advertiseTest30() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"conclusion\": true,\n" +
                "  \"message\": \"审核通过\"\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 137)
                .header("authorization",token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核广告
     * 操作的广告状态为审核态（成功）
     * @throws Exception
     */
    @Test
    public void advertiseTest31() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"conclusion\": true,\n" +
                "  \"message\": \"审核通过\"\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/audit", 0, 136)
                .header("authorization",token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核广告
     * 审核结果失败
     * @throws Exception
     */
    @Test
    public void advertiseTest32() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"conclusion\": false,\n" +
                "  \"message\": \"审核不通过\"\n" +
                "}";
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/audit", 0, 135)
                .header("authorization",token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 广告时段不存在
     * @throws Exception
     */
    @Test
    public void advertiseTest33() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement?beginDate=2020-01-10&endDate=2020-12-12", 0, 100)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 广告日期格式错误
     * @throws Exception
     */
    @Test
    public void advertiseTest34() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement?beginDate=2020/01/10&endDate=2020/12/12", 0, 100)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 广告日期开始日期比结束日期晚
     * @throws Exception
     */
    @Test
    public void advertiseTest35() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement?" +
                "beginDate=2020-12-10&endDate=2020-6-12", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 成功
     * @throws Exception
     */
    @Test
    public void advertiseTest36() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement?" +
                "beginDate=2020-01-10&endDate=2020-12-12", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 设置页数
     * @throws Exception
     */
    @Test
    public void advertiseTest37() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement?" +
                "beginDate=2020-01-10&endDate=2020-12-12&page=1&pageSize=3", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 时段为0
     * @throws Exception
     */
    @Test
    public void advertiseTest38() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement?" +
                "beginDate=2020-01-10&endDate=2020-12-12&page=1&pageSize=3", 0, 0)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员在广告时段下新建广告
     * 时段不存在
     * @throws Exception
     */
    @Test
    public void advertiseTest39() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"内容\",\n" +
                "  \"weight\": \"1\",\n" +
                "  \"beginDate\": \"2020-01-20\",\n" +
                "  \"endDate\": \"2020-02-01\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"链接\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisement", 0, 100)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 达到时段广告上限
     * @throws Exception
     */
    @Test
    public void advertiseTest40() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"内容\",\n" +
                "  \"weight\": \"1\",\n" +
                "  \"beginDate\": \"2020-01-20\",\n" +
                "  \"endDate\": \"2020-02-01\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"链接\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisement", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_OUTLIMIT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_OUTLIMIT.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 达到时段广告上限
     * @throws Exception
     */
    @Test
    public void advertiseTest41() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"内容\",\n" +
                "  \"weight\": \"1\",\n" +
                "  \"beginDate\": \"2020-01-20\",\n" +
                "  \"endDate\": \"2020-02-01\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"链接\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisement", 0, 2)
                .header("authorization",token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_OUTLIMIT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_OUTLIMIT.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 传入开始日期和结束日期格式错误
     * @throws Exception
     */
    @Test
    public void advertiseTest42() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"内容\",\n" +
                "  \"weight\": \"1\",\n" +
                "  \"beginDate\": \"2020/01/20\",\n" +
                "  \"endDate\": \"2020/02/01\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"链接\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisemnt", 0, 1)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 传入开始日期值不合理
     * @throws Exception
     */
    @Test
    public void advertiseTest43() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"内容\",\n" +
                "  \"weight\": \"1\",\n" +
                "  \"beginDate\": \"2020-02-30\",\n" +
                "  \"endDate\": \"2020-03-06\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"链接\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisemnt", 0, 1)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 传入开始日期大于结束日期
     * @throws Exception
     */
    @Test
    public void advertiseTest44() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"内容\",\n" +
                "  \"weight\": \"1\",\n" +
                "  \"beginDate\": \"2020-12-12\",\n" +
                "  \"endDate\": \"2020-03-06\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"链接\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisemnt", 0, 1)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 传入的广告排序权重不合法(无法转换为数字）
     * @throws Exception
     */
    @Test
    public void advertiseTest45() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"内容\",\n" +
                "  \"weight\": \"abc\",\n" +
                "  \"beginDate\": \"2020-02-12\",\n" +
                "  \"endDate\": \"2020-12-12\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"链接\"\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisemnt", 0, 1)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 传入的是否为每日重复广告值不合理(无法转换为布尔值）
     * @throws Exception
     */
    @Test
    public void advertiseTest46() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"内容\",\n" +
                "  \"weight\": \"1\",\n" +
                "  \"beginDate\": \"2020-02-12\",\n" +
                "  \"endDate\": \"2020-12-12\",\n" +
                "  \"repeat\": 123,\n" +
                "  \"link\": \"链接\"\n" +
                "}";
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisemnt", 0, 1)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 成功
     * @throws Exception
     */
    @Test
    public void advertiseTest47() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requestJson = "{\n" +
                "  \"content\": \"广告内容1\",\n" +
                "  \"weight\": \"5\",\n" +
                "  \"beginDate\": \"2020-12-1\",\n" +
                "  \"endDate\": \"2020-12-2\",\n" +
                "  \"repeat\": true,\n" +
                "  \"link\": \"link1\"\n" +
                "}";
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisemnt", 0, 1)
                .header("authorization", token)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员在广告时段下增加广告
     * 时段id不存在
     * @throws Exception
     */
    @Test
    public void advertiseTest48() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{tid}/advertisemnt/{id}", 0, 100,130)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下增加广告
     * 广告id不存在
     * @throws Exception
     */
    @Test
    public void advertiseTest49() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{tid}/advertisemnt/{id}", 0, 1,20)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下增加广告
     * 达到时段广告上限
     * @throws Exception
     */
    @Test
    public void advertiseTest50() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{tid}/advertisemnt/{id}", 0, 2,130)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下增加广告
     * 成功
     * @throws Exception
     */
    @Test
    public void advertiseTest51() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{tid}/advertisemnt/{id}", 0, 1,130)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 用户登录
     *
     * @author 王显伟
     */
    public String userLogin(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString)).getString("data");
    }

    private String adminLogin(String userName, String password) throws Exception{
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


}
