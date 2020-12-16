package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.oomall.LoginVo;
import org.apache.catalina.filters.ExpiresFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.io.UnsupportedEncodingException;

@SpringBootTest(classes = Application.class)
public class YangLeiTest{
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
     * 修改审核未通过的商铺的名字
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    @Order(1)
    public void modifyShop_unaudit() throws Exception{
        String shopToken = this.login("shopadmin_No3","123456");
        String requestJson = "{\"name\": \"没过审\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/6")
                .header("authorization", shopToken)
                .bodyValue(requestJson);
        byte[] responseBuffer = res.exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .jsonPath("$.data").isArray()
                .returnResult()
                .getResponseBodyContent();
        try {
            String response = new String(responseBuffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改商铺的名字为空
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    @Order(2)
    public void updateShop_null() throws Exception {
        String shopToken = this.login("shopadmin_No1", "123456");
        String requestJson = "{\"name\": \"  \"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/5")
                .header("authorization", shopToken)
                .bodyValue(requestJson);
        byte[] responseBuffer = res.exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .jsonPath("$.data").isArray()
                .returnResult()
                .getResponseBodyContent();
        try {
            String response = new String(responseBuffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 企图修改商铺的状态
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    @Order(3)
    public void updateShop_state() throws Exception {
        String shopToken = this.login("shopadmin_No1", "123456");
        String requestJson = "{\"name\": \"状态不会变\",\"state\":4}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/5")
                .header("authorization", shopToken)
                .bodyValue(requestJson);
        byte[] responseBuffer = res.exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.data").isArray()
                .returnResult()
                .getResponseBodyContent();
        try {
            String response = new String(responseBuffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 企图修改商铺的ID
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    @Order(4)
    public void updateShop_ID() throws Exception {
        String shopToken = this.login("shopadmin_No1", "123456");
        String requestJson = "{\"name\": \"ID不会变\",\"id\":120}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/5")
                .header("authorization", shopToken)
                .bodyValue(requestJson);
        byte[] responseBuffer = res.exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.data").isArray()
                .returnResult()
                .getResponseBodyContent();
        try {
            String response = new String(responseBuffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 企图修改不属于自己的商铺
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    @Order(5)
    public void updateShop_other() throws Exception {
        String shopToken = this.login("shopadmin_No2", "123456");
        String requestJson = "{\"name\": \"别人的店铺\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/5")
                .header("authorization", shopToken)
                .bodyValue(requestJson);
        byte[] responseBuffer = res.exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .jsonPath("$.data").isArray()
                .returnResult()
                .getResponseBodyContent();
        try {
            String response = new String(responseBuffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
