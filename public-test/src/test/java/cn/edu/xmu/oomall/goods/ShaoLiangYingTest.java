package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-15 17:36
 */
@SpringBootTest(classes = Application.class)
public class ShaoLiangYingTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private String token = "";

    @Autowired
    private ObjectMapper mObjectMapper;

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

    /**
     * Flashsale002
     * 正确增加三天后的秒杀
     * @throws Exception
     */
    @Test
    public void createflash1() throws Exception {
        token = adminLogin("130880admin", "123456");
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":8}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/timesegments/0/flashsales").header("authorization",token).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        //恢复数据库
        responseBuffer = manageClient.delete().uri("/flashsales/"+insertId).header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 不允许增加过去和今天的秒杀
     * @throws Exception
     */
    @Test
    public void createflash2() throws Exception {
        //增加昨天的秒杀
        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":9}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/timesegments/0/flashsales").header("authorization",token).bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo("需要是一个将来的时间;")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 增加不存在时段id的秒杀
     * @throws Exception
     */
    @Test
    public void createflash3() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":-1}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/timesegments/0/flashsales").header("authorization",token).bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 增加flashDate+segment_id已经存在的秒杀
     * @throws Exception
     */
    @Test
    public void createflash4() throws Exception {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse("2021-12-17 11:25:58",df);
        //增加秒杀
        String requestJson = "{\"flashDate\":\""+ ldt.toString() +"\",\"id\":8}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/timesegments/0/flashsales").header("authorization",token).bodyValue(requestJson);

        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        responseBuffer = manageClient.delete().uri("/flashsales/"+insertId).header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }
}
