package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 用户修改自己信息测试类
 *
 * @author 24320182203175 陈晓如
 * createdBy 陈晓如 2020/11/30 13:42
 * modifiedBy 陈晓如 2020/11/30 13:42
 **/
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
public class UserTest {

    private final WebTestClient webClient;

    public UserTest(){
        this.webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    //获取token
    private String login(String userName, String password) throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = webClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    /**
     * 修改自己的信息测试1: 管理员修改自己的信息 成功
     * @author 24320182203175 陈晓如
     * createdBy 陈晓如 2020/11/30 13:56
     * modifiedBy 陈晓如 2020/11/30 13:56
     */
    @Test
    public void changeMyAdminselfInfo1() throws Exception{
        String token = this.login("13088admin", "123456");
        String userJson = "{\"name\": \"oomall\"," +
                "\"avatar\": \"1.png\"," +
                "\"mobile\": \"123456789\"," +
                "\"email\": \"12345678@qq.com\"}";
        byte[] responseString = webClient.put().uri("/adminusers")
                .header("authorization", token)
                .bodyValue(userJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, "UTF-8"),true);
    }




    /**
     * 修改自己的信息测试2: 管理员未登录修改自己的信息
     * @author 24320182203175 陈晓如
     * createdBy 陈晓如 2020/12/01 10:43
     * modifiedBy 陈晓如 2020/12/01 10:43
     */
    @Test
    public void changeMyAdminselfInfo2() throws Exception{
        String userJson = "{\"name\": \"oomall\"," +
                "\"avatar\": \"1.png\"," +
                "\"mobile\": \"123456789\"," +
                "\"email\": \"12345678@qq.com\"}";
        byte[] responseString = webClient.put().uri("/adminusers")
                .bodyValue(userJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

//    /**
//     * 修改自己的信息测试3: 使用伪造的token修改自己的信息
//     * @author 24320182203175 陈晓如
//     * createdBy 陈晓如 2020/12/01 10:51
//     * modifiedBy 陈晓如 2020/12/01 10:51
//     */
//    @Test
//    public void changeMyAdminselfInfo3() throws Exception{
//        byte[] responseString = webClient.post().uri("/privilege/adminusers")
//                .header("authorization", "test")
//                .exchange()
//                .expectStatus().isUnauthorized()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getMessage())
//                .returnResult()
//                .getResponseBodyContent();
//
//    }
//
//    /**
//     * 修改自己的信息测试4: 修改自己的信息内容为空
//     * @author 24320182203175 陈晓如
//     * createdBy 陈晓如 2020/12/01 10:55
//     * modifiedBy 陈晓如 2020/12/01 10:55
//     */
//    @Test
//    public void changeMyAdminselfInfo4() throws Exception{
//        String requireJson = "";
//        byte[] responseString = webClient.post().uri("/privilege/adminusers")
//                .header("authorization", "test")
//                .bodyValue(requireJson)
//                .exchange()
//                .expectStatus().isNoContent()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getMessage())
//                .returnResult()
//                .getResponseBodyContent();
//
//    }


}

