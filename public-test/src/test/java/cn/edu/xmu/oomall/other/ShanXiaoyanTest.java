package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSONObject;


/**
 * 其他模块 地址服务
 * 24320182203184 单晓妍
 */
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})

//指定了测试运行的顺序
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = Application.class)
public class ShanXiaoyanTest {
    //private String token ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE1MTAzNDA4NVVVIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjoxNjA2MzUzNDc2NDkwLCJleHAiOjE2MDgwMDMyNDgsInVzZXJJZCI6MSwiaWF0IjoxNjA3OTk5NjQ4fQ.H45UXFZf9QnLhFBg6An5Kaj6TQ-C_7J2E0aW1rJIfDE";

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
     * 买家登录，获取token
     *
     * @author yang8miao
     * @param userName
     * @param password
     * @return token
     * createdBy yang8miao 2020/11/26 21:34
     * modifiedBy yang8miao 2020/11/26 21:34
     */
    private String userLogin(String userName, String password) throws Exception{

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
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }


    /**
     * 管理员登录，获取token
     *
     * @author yang8miao
     * @param userName
     * @param password
     * @return token
     * createdBy yang8miao 2020/12/12 19:48
     * modifiedBy yang8miao 2020/12/12 19:48
     */
    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    /**
     * addAddress1
     * 新增地址，参数错误，手机号码为空
     * @throws Exception
     */
    @Test
    @Order(0)
    public void addAddress1() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"1232323\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses", 1)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }
    /**addAddress2
     * 新增地址，参数错误，收件人为空
     * @throws Exception
     */
    @Test
    @Order(1)
    public void addAddress2() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"\",\n" +
                " \"mobile\":  \"18990897878\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * addAddress3
     * 新增地址，参数错误，详情为空
     * @throws Exception
     */

    @Test
    @Order(2)
    public void addAddress3() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses", 1)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * addAddress4
     * 新增地址，参数错误，地区id为空
     * @throws Exception
     */


    @Test
    @Order(3)
    public void addAddress4() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": null,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\" \n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }



    /**
     * addAddress6
     * 新增地址，地区不存在
     * @throws Exception
     */
    @Test
    @Order(4)
    public void addAddress6() throws Exception{

        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": 70,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }
    /**
     * addAddress7
     * 新增地址
     * @throws Exception
     */
    @Test
    @Order(5)
    public void addAddress7() throws Exception{

        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": 1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse= "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 15,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"测试地址1\",\n" +
                "        \"consignee\": \"测试\",\n" +
                "        \"mobile\": \"18990897878\",\n" +
                "        \"beDefault\": false\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);


    }

    /**
     * addRegion1
     * 新增地区 父地区不存在
     */
    @Test
    @Order(6)
    public void addRegion1() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/shops/0/regions/200/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }
    /**addRegion2
     * 新增地区 父地区已废弃
     */
    @Test
    @Order(7)
    public void addRegion2() throws Exception{


        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/shops/0/regions/2/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.REGION_OBSOLETE.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * addRegion3
     * 增地区
     */
    @Test
    @Order(8)
    public void addRegion3() throws Exception{


        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/shops/0/regions/1/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * addRegion4
     * 新增地区，地区名字为空,errmsg为自定义，可自行更改
     */
    @Test
    @Order(9)
    public void addRegion4() throws Exception{


        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/shops/0/regions/1/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo("不能为空;")
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * addRegion5
     * 新增地区，地区邮政编码错误
     */
    @Test
    @Order(10)
    public void addRegion5() throws Exception{


        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"1001\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/shops/0/regions/1/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }
    /**
     * diableRegion
     * 废弃地区 地区id不存在
     */

    @Test
    @Order(11)
    public void diableRegion1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = mallClient.delete().uri("/shops/0/regions/100")
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
     * diableRegion
     * 废弃地区
     */

    @Test
    @Order(12)
    public void diableRegion2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = mallClient.delete().uri("/shops/0/regions/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * addAddress5
     * 新增地址，地区已废弃
     * @throws Exception
     */

    @Test
    @Order(13)
    public void addAddress5() throws Exception{


        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": 1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.REGION_OBSOLETE.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * setAsDefault1
     * @throws Exception
     */
    @Test
    @Order(14)
    public void setAsDefault1() throws Exception {
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/1/default").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 地址id不存在
     * setAsDefault1
     * @throws Exception
     */
    @Test
    @Order(15)
    public void setAsDefault2() throws Exception {
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/100/default").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult().getResponseBodyContent();

    }



}
