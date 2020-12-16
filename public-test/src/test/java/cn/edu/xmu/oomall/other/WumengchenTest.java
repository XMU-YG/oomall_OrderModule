package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * @author 24320182203293+wumengchen
 * @date 2020-12-13
 */
@SpringBootTest(classes = Application.class)
@Slf4j
public class WumengchenTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;


    @BeforeEach
    public void setUp() {

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://" + managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://" + mallGate)
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
     * @author 24320182203293+wumengchen
     * 测试管理员审核广告
     * id不存在
     */
    @Test
    public void messageAd2() throws Exception {

        String token = this.login("13088admin", "123456");
        String mesJson = "{\"conclusion\": \"false\",\"message\": \"hhh\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/300/audit").header("authorization", token)
                .bodyValue(mesJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                //.jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }



    /**
     * @author 24320182203293+wumengchen
     * 测试管理员审核广告
     * 广告状态禁止（上架）
     */
    @Test
    public void messageAd3() throws Exception {

        String token = this.login("13088admin", "123456");
        String mesJson = "{\"conclusion\": \"false\",\"message\": \"hhh\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/202/audit").header("authorization", token)
                .bodyValue(mesJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员审核广告
     * 广告状态禁止（下架）
     */
    @Test
    public void messageAd4() throws Exception {

        String token = this.login("13088admin", "123456");
        String mesJson = "{\"conclusion\": \"false\",\"message\": \"hhh\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/209/audit").header("authorization", token)
                .bodyValue(mesJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员审核广告(审核通过)
     * 成功
     */
    @Test
    public void messageAd() throws Exception {


        String mesJson = "{\"conclusion\": \"true\",\"message\": \"ok\"}";
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/210/audit").header("authorization", token)
                .bodyValue(mesJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
              //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] responseString3 = manageClient.put().uri("/shops/0/advertisement/210/onshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();


        byte[] ret = manageClient.get().uri("/shops/0/timesegments/110/advertisement").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();


        String responseString1 = new String(ret, "UTF-8");
        String expectedString = "{\"errno\":0,\"data\":" +
                "{\"total\":1,\"pages\":1,\"pageSize\":1,\"page\":1," +
                "\"list\":[{\"id\":210,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1475991949547324589.jpg\"," +
                "\"content\":null,\"segId\":110,\"state\":\"4\",\"weight\":null,\"bedefault\":false,\"beginDate\":\"2020-12-15\",\"endDate\":\"2021-10-10\",\"repeats\":true,\"gmt_created\":\"2020-12-06T22:49:31\",\"gmt_modified\":\"2020-12-06T22:49:31\"}]}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString, responseString1, true);


    }



    /**
     * @author 24320182203293+wumengchen
     * 测试管理员设置默认广告
     * 资源不存在
     */
    @Test
    public void becomeDefault() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/300/default").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();

    }




    /**
     * @author 24320182203293+wumengchen
     * 测试管理员上架广告
     * 操作资源的id不存在
     */
    @Test
    public void onshelf0() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/300/onshelves").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
             //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }


    /**
     * @author 24320182203293+wumengchen
     * 测试管理员上架广告
     * 广告状态禁止
     */
    @Test
    public void onshelf() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/203/onshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员上架广告
     * 成功
     */
    @Test
    public void onshelf1() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/204/onshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/0/timesegments/104/advertisement").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String responseString1 = new String(ret, "UTF-8");
        String expectedString = "{\"errno\":0,\"data\":" +
                "{\"total\":1,\"pages\":1,\"pageSize\":1,\"page\":1," +
                "\"list\":[{\"id\":204,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1475991949547324589.jpg\","+
                "\"content\":null,\"segId\":104,\"state\":\"4\",\"weight\":null,\"bedefault\":false,\"beginDate\":\"2020-12-15\",\"endDate\":\"2021-10-10\",\"repeats\":true,\"gmt_created\":\"2020-12-06T22:49:31\",\"gmt_modified\":\"2020-12-06T22:49:31\"}]}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString, responseString1, true);


    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员下架广告
     * 操作资源的id不存在
     */
    @Test
    public void offshelf0() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/300/offshelves").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
              //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员下架广告
     * 成功
     */
    @Test
    public void offshelf() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/205/offshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/0/timesegments/105/advertisement").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                //.jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String responseString1 = new String(ret, "UTF-8");
        String expectedString = "{\"errno\":0,\"data\":" +
                "{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1," +
                "\"list\":[]}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString, responseString1, true);
    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员下架广告
     * 广告状态禁止
     */
    @Test
    public void offshelf1() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/206/offshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员删除广告
     * 操作资源的id不存在
     */
    @Test
    public void deleteAd0() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/300").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
             //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }


    /**
     * @author 24320182203293+wumengchen
     * 测试管理员删除广告（下架态）
     * 成功
     */
    @Test
    public void deleteAd() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/207").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();


        byte[] ret = manageClient.get().uri("/shops/0/timesegments/107/advertisement").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
               // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String responseString1 = new String(ret, "UTF-8");
        String expectedString = "{\"errno\":0,\"data\":" +
                "{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1," +
                "\"list\":[]}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString, responseString1, true);

    }


    /**
     * @author 24320182203293+wumengchen
     * 测试管理员删除广告(审核态)
     * 成功
     */
    @Test
    public void deleteAd2() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/211").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
              //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();


        byte[] ret = manageClient.get().uri("/shops/0/timesegments/111/advertisement").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
              //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String responseString1 = new String(ret, "UTF-8");
        String expectedString = "{\"errno\":0,\"data\":" +
                "{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1," +
                "\"list\":[]}," +
                "\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString, responseString1, true);

    }




    /**
     * @author 24320182203293+wumengchen
     * 测试管理员删除广告
     * 广告状态禁止
     */
    @Test
    public void deleteAd1() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/208").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
             //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }


}


