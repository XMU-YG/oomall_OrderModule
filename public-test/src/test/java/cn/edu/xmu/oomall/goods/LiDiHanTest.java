package cn.edu.xmu.oomall.goods;


import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * @author  24320182203221 Li Dihan
 * @date 2020/12/09 15:15
 */

@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
public class LiDiHanTest {

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
        //endregion
    }




    @Test
    @Order(0)
    public void getGrouponAllStates() throws Exception {
        byte[] ret = mallClient.get()
                .uri("/groupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"已下线\",\"code\":0}," +
                "{\"name\":\"已上线\",\"code\":1},{\"name\":\"已删除\",\"code\":2}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(1)
    public void createGrouponAc1() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/spus/273/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":6,\"name\":null,\"goodsSpu\":" +
                "{\"id\":273,\"name\":\"金和汇景•戴荣华•古彩洛神赋瓷瓶\",\"goodsSn\":\"drh-d0001\"," +
                "\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\"" +
                ",\"gmtCreate\":\"2020-12-07 13:48:44\",\"gmtModified\":\"2020-12-07 13:48:44\"" +
                ",\"disable\":0},\"shop\":{\"id\":1,\"name\":\"Nike\"},\"strategy\":\"无\"," +
                "\"beginTime\":\"2021-12-07 11:57:39\",\"endTime\":\"2021-12-09 11:57:39\"}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(2)
    public void createGrouponAc2() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-12 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/spus/273/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(3)
    public void createGrouponAc3() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-03 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/spus/273/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(4)
    public void createGrouponAc4() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/spus/274/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(5)
    public void createGrouponAc6() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/10/spus/274/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(6)
    public void createGrouponAc7() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/spus/1/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(7)
    public void changeGrouponAc1() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"有\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/1/groupons/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(8)
    public void changeGrouponAc2() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-12 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/1/groupons/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(9)
    public void changeGrouponAc3() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-03 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/1/groupons/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(10)
    public void changeGrouponAc4() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/2/groupons/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(11)
    public void changeGrouponAc5() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/10/groupons/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(12)
    public void changeGrouponAc7() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07 11:57:39\"," +
                "\"endTime\":\"2021-12-09 11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/1/groupons/10")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(13)
    public void cancelGrouponAc1() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/1/groupons/1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(14)
    public void cancelGrouponAc2() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/2/groupons/1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(15)
    public void cancelGrouponAc3() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/10/groupons/1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(16)
    public void cancelGrouponAc5() throws Exception {
        String token = this.login("13088admin","123456");;
        byte[] ret = manageClient.delete()
                .uri("/shops/1/groupons/10")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(17)
    public void getGrouponActivity1() throws Exception {
        byte[] ret = mallClient.get()
                .uri(uriBuilder -> uriBuilder.path("/groupons")
                        .queryParam("shopId",1L)
                        .queryParam("timeline",2L)
                        .queryParam("spuId",273L)
                        .queryParam("page",1)
                        .queryParam("pageSize",2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedString = "{\"errno\":0,\"data\":" +
                "{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1," +
                "\"list\":[{\"id\":1,\"name\":\"双十一\",\"beginTime\":\"2020-12-05 11:57:39\"," +
                "\"endTime\":\"2020-12-30 11:57:39\"},{\"id\":3,\"name\":\"黑色星期五\"," +
                "\"beginTime\":\"2020-12-05 11:57:39\",\"endTime\":\"2020-12-30 11:57:39\"}]}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString, responseString, true);
    }

    @Test
    @Order(18)
    public void getGrouponActivity2() throws Exception {
        byte[] ret = mallClient.get()
                .uri(uriBuilder -> uriBuilder.path("/groupons")
                        .queryParam("shopId",2L)
                        .queryParam("timeline",0L)
                        .queryParam("spuId",273L)
                        .queryParam("page",1)
                        .queryParam("pageSize",2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":1," +
                "\"page\":1,\"list\":[{\"id\":2,\"name\":\"双十二\"," +
                "\"beginTime\":\"2020-12-29 11:57:39\",\"endTime\":\"2020-12-30 11:57:39\"}]}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString, responseString, true);
    }


    @Test
    @Order(19)
    public void getGrouponActivity4() throws Exception {
        byte[] ret = mallClient.get()
                .uri(uriBuilder -> uriBuilder.path("/groupons")
                        .queryParam("shopId",2L)
                        .queryParam("timeline",3L)
                        .queryParam("spuId",273L)
                        .queryParam("page",1)
                        .queryParam("pageSize",2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":1," +
                "\"page\":1,\"list\":[{\"id\":4,\"name\":\"儿童节\"," +
                "\"beginTime\":\"2020-06-01 11:57:39\",\"endTime\":\"2020-06-02 11:57:39\"}]}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString, responseString, true);
    }




    @Test
    @Order(20)
    public void getShopGrouponAc1() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.get()
                .uri(uriBuilder -> uriBuilder.path("/shops/1/groupons")
                        .queryParam("state",(byte) 0)
                        .queryParam("spuId",273L)
                        .queryParam("startTime","2020-12-01 11:57:39")
                        .queryParam("endTime","2020-12-12 11:57:39")
                        .build())
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1," +
                "\"pageSize\":1,\"page\":1,\"list\":[{\"id\":5,\"name\":\"劳动节\"," +
                "\"beginTime\":\"2020-12-05 11:57:39\",\"endTime\":\"2020-12-09 11:57:39\"}]}" +
                ",\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(21)
    public void getShopGrouponAc2() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.get()
                .uri(uriBuilder -> uriBuilder.path("/shops/1/groupons")
                        .queryParam("state",(byte) 0)
                        .queryParam("spuId",273L)
                        .queryParam("startTime","null")
                        .queryParam("endTime","2020-12-12 11:57:39")
                        .build())
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1," +
                "\"pageSize\":1,\"page\":1,\"list\":[{\"id\":5,\"name\":\"劳动节\"," +
                "\"beginTime\":\"2020-12-05 11:57:39\",\"endTime\":\"2020-12-09 11:57:39\"}]}" +
                ",\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(22)
    public void getShopGrouponAc3() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.get()
                .uri(uriBuilder -> uriBuilder.path("/shops/1/groupons")
                        .queryParam("state",(byte) 0)
                        .queryParam("spuId",273L)
                        .queryParam("startTime","2020-12-01 11:57:39")
                        .queryParam("endTime","null")
                        .build())
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1," +
                "\"pageSize\":1,\"page\":1,\"list\":[{\"id\":5,\"name\":\"劳动节\"," +
                "\"beginTime\":\"2020-12-05 11:57:39\",\"endTime\":\"2020-12-09 11:57:39\"}]}" +
                ",\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(23)
    public void getShopAllStates() throws Exception {
        byte[] ret = mallClient.get()
                .uri("/shops/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未审核\",\"code\":0}," +
                "{\"name\":\"未上线\",\"code\":1},{\"name\":\"上线\",\"code\":2}," +
                "{\"name\":\"关闭\",\"code\":3},{\"name\":\"审核未通过\",\"code\":4}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    @Test
    @Order(24)
    public void changeShop1() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"name\":\"麦当劳\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(25)
    public void changeShop3() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"name\":\"麦当劳\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/10")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(26)
    public void closeShop1() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/2")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    @Test
    @Order(27)
    public void closeShop3() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/10")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(28)
    public void auditShop1() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"conclusion\":\"true\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/0/newshops/1/audit")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    @Test
    @Order(29)
    public void auditShop3() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"conclusion\":\"true\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/0/newshops/5/audit")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("927")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(30)
    public void auditShop4() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"conclusion\":\"true\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/0/newshops/10/audit")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(31)
    public void onlineShop1() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/3/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(32)
    public void onlineShop2() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/10/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(33)
    public void onlineShop3() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shop/shops/5/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("928")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }



    @Test
    @Order(34)
    public void offlineShop1() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/4/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(35)
    public void offlineShop2() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/10/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(36)
    public void offlineShop3() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/5/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("929")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }







}
