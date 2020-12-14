package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.Application;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import cn.edu.xmu.ooad.util.ResponseCode;

import java.nio.charset.StandardCharsets;

/**
 * 其他模块-足迹服务、商品收藏服务、购物车服务 公开测试用例
 *
 * @Description 此测试用例需要与商品模块、订单模块、权限模块集成才可跑通
 * @Description 此测试用例使用助教的买家、足迹、收藏、商品sku等表的数据，购物车表的数据为作者自己编写
 *
 * @author  24320182203318 yang8miao
 * @date 2020/12/09 15:15
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class YuanShenyangTest {

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
     * 足迹服务-管理员查看浏览记录  普通测试1，查询成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(0)
    public void getFootprints1() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=220&page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1212599,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 291,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 130000,\n" +
                "          \"price\": 130000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 足迹服务-管理员查看浏览记录  普通测试2，查询成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(1)
    public void getFootprints2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=134&page=1&pageSize=1").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1212513,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 537,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201711/file_5a10e5d95d038.jpg\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 699,\n" +
                "          \"price\": 699,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 足迹服务-管理员查看浏览记录 普通测试，查询成功，但未查到任何足迹
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(2)
    public void getFootprints3() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=17320&endTime=2019-11-11 12:00:00&page=10&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 10,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 足迹服务-管理员查看浏览记录 普通测试，查询成功，但未查到任何足迹
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(3)
    public void getFootprints4() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=17320&beginTime=2022-11-24 12:00:00&page=10&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 10,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 足迹服务-管理员查看浏览记录 普通测试，开始时间大于结束时间,返回错误码
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(4)
    public void getFootprints5() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?beginTime=2022-11-24 12:00:00&endTime=2020-11-11 12:00:00")
                .header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.Log_Bigger.getMessage())
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 足迹服务-增加足迹 增加测试1 增加成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(5)
    public void postUsersIdFootprints1() throws Exception {

        // userId = 9
        String token = this.userLogin("17857289610", "123456");

        byte[] responseString = mallClient.post().uri("/skus/300/footprints").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 管理员查询足迹，进行验证
        token = this.adminLogin("13088admin", "123456");
        byte[] queryResponseString = manageClient.get().uri("/shops/0/footprints?userId=9&page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 2,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1212388,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 574,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3acdf3a50d4.png\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 598,\n" +
                "          \"price\": 598,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
                "      },\n" +
                "      {\n" +
//                "        \"id\": 9,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 300,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58621fe110292.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 68000,\n" +
                "          \"price\": 68000,\n" +
                "          \"disable\": 0\n" +
                "        }\n" +
//                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 足迹服务-增加足迹 增加测试2 增加成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(6)
    public void postUsersIdFootprints2() throws Exception {

        // userId = 99
        String token = this.userLogin("16436076738", "123456");

        byte[] responseString = mallClient.post().uri("/skus/300/footprints").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();


        // 管理员查询足迹，进行验证
        token = this.adminLogin("13088admin", "123456");
        byte[] queryResponseString = manageClient.get().uri("/shops/0/footprints?userId=99&page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 2,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1212478,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 479,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59708f8030eb8.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 102000,\n" +
                "          \"price\": 102000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
                "      },\n" +
                "      {\n" +
//                "        \"id\": 9,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 300,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58621fe110292.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 68000,\n" +
                "          \"price\": 68000,\n" +
                "          \"disable\": 0\n" +
                "        }\n" +
//                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);

    }

    /**
     * 足迹服务-增加足迹 增加测试3 增加成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(7)
    public void postUsersIdFootprints3() throws Exception {

        // userId = 100
        String token = this.userLogin("48673740540", "123456");

        byte[] responseString = mallClient.post().uri("/skus/300/footprints").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();


        // 管理员查询足迹，进行验证
        token = this.adminLogin("13088admin", "123456");
        byte[] queryResponseString = manageClient.get().uri("/shops/0/footprints?userId=100&page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 2,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1212479,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 410,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201705/file_590d651a1290e.jpg\",\n" +
                "          \"inventory\": 0,\n" +
                "          \"originalPrice\": 520,\n" +
                "          \"price\": 520,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
                "      },\n" +
                "      {\n" +
//                "        \"id\": 9,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 300,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58621fe110292.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 68000,\n" +
                "          \"price\": 68000,\n" +
                "          \"disable\": 0\n" +
                "        }\n" +
//                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 商品收藏服务-买家查看所有收藏的商品  普通测试1，查询成功 page=1&pageSize=1
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(8)
    public void getFavorites1() throws Exception {

        // userId = 20
        String token = this.userLogin("10101113105", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=1").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 3,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3735458,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 428,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_5967a3fed2cf4.jpg\",\n" +
                "          \"inventory\": 9972,\n" +
                "          \"originalPrice\": 299,\n" +
                "          \"price\": 299,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:23\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家查看所有收藏的商品  普通测试2，查询成功 page=1&pageSize=5
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(9)
    public void getFavorites2() throws Exception {

        // userId = 20
        String token = this.userLogin("10101113105", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 5,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3735458,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 428,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_5967a3fed2cf4.jpg\",\n" +
                "          \"inventory\": 9972,\n" +
                "          \"originalPrice\": 299,\n" +
                "          \"price\": 299,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:23\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3768225,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 420,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_5967792535e80.jpg\",\n" +
                "          \"inventory\": 150,\n" +
                "          \"originalPrice\": 118000,\n" +
                "          \"price\": 118000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3800992,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 347,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58405c09ea4f3.jpg\",\n" +
                "          \"inventory\": 100,\n" +
                "          \"originalPrice\": 4028,\n" +
                "          \"price\": 4028,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家查看所有收藏的商品  普通测试3，查询成功 page=1&pageSize=2
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(10)
    public void getFavorites3() throws Exception {

        // userId = 11026
        String token = this.userLogin("30674268147", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=2").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 2,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746375,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 573,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3acd8f6d384.png\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 480,\n" +
                "          \"price\": 480,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:23\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3779142,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 460,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59707b5f08a0e.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 15000,\n" +
                "          \"price\": 15000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 商品收藏服务-买家查看所有收藏的商品  普通测试4，查询成功 page=1&pageSize=10
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(11)
    public void getFavorites4() throws Exception {

        // userId = 11026
        String token = this.userLogin("30674268147", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746375,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 573,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3acd8f6d384.png\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 480,\n" +
                "          \"price\": 480,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:23\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3779142,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 460,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59707b5f08a0e.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 15000,\n" +
                "          \"price\": 15000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3811909,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 322,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586652b49d1a7.jpg\",\n" +
                "          \"inventory\": 100,\n" +
                "          \"originalPrice\": 2070,\n" +
                "          \"price\": 2070,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 商品收藏服务-买家收藏商品  普通测试1，收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(12)
    public void postFavoritesGoodsSpuId1() throws Exception {

        // userId = 11026
        String token = this.userLogin("30674268147", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/371").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
//                "    \"id\": 574271,\n" +
                "      \"goodsSku\": {\n" +
                "        \"id\": 371,\n" +
                "        \"name\": \"+\",\n" +
                "        \"skuSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58406071f1fc7.jpg\",\n" +
                "        \"inventory\": 1,\n" +
                "        \"originalPrice\": 650000,\n" +
                "        \"price\": 650000,\n" +
                "        \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 4,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746375,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 573,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3acd8f6d384.png\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 480,\n" +
                "          \"price\": 480,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:23\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3779142,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 460,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59707b5f08a0e.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 15000,\n" +
                "          \"price\": 15000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3811909,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 322,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586652b49d1a7.jpg\",\n" +
                "          \"inventory\": 100,\n" +
                "          \"originalPrice\": 2070,\n" +
                "          \"price\": 2070,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
//                "    \"id\": 574271,\n" +
                "      \"goodsSku\": {\n" +
                "        \"id\": 371,\n" +
                "        \"name\": \"+\",\n" +
                "        \"skuSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58406071f1fc7.jpg\",\n" +
                "        \"inventory\": 1,\n" +
                "        \"originalPrice\": 650000,\n" +
                "        \"price\": 650000,\n" +
                "        \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家收藏商品  普通测试2，收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(13)
    public void postFavoritesGoodsSpuId2() throws Exception {

        // userId = 11026
        String token = this.userLogin("30674268147", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/277").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
//                "    \"id\": 574271,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 277,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\n" +
                "          \"inventory\": 10,\n" +
                "          \"originalPrice\": 16200,\n" +
                "          \"price\": 16200,\n" +
                "          \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);


        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 5,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746375,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 573,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3acd8f6d384.png\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 480,\n" +
                "          \"price\": 480,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:23\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3779142,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 460,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59707b5f08a0e.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 15000,\n" +
                "          \"price\": 15000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3811909,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 322,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586652b49d1a7.jpg\",\n" +
                "          \"inventory\": 100,\n" +
                "          \"originalPrice\": 2070,\n" +
                "          \"price\": 2070,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
//                "    \"id\": 574271,\n" +
                "      \"goodsSku\": {\n" +
                "        \"id\": 371,\n" +
                "        \"name\": \"+\",\n" +
                "        \"skuSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58406071f1fc7.jpg\",\n" +
                "        \"inventory\": 1,\n" +
                "        \"originalPrice\": 650000,\n" +
                "        \"price\": 650000,\n" +
                "        \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "      },\n" +
                "      {\n" +
//                "    \"id\": 574271,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 277,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\n" +
                "          \"inventory\": 10,\n" +
                "          \"originalPrice\": 16200,\n" +
                "          \"price\": 16200,\n" +
                "          \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家收藏商品  普通测试3，收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(14)
    public void postFavoritesGoodsSpuId3() throws Exception {

        // userId = 11026
        String token = this.userLogin("30674268147", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/447").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
//                "    \"id\": 574271,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 447,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201808/file_5b74f0e3a396d.jpg\",\n" +
                "          \"inventory\": 93,\n" +
                "          \"originalPrice\": 669,\n" +
                "          \"price\": 669,\n" +
                "          \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 6,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746375,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 573,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3acd8f6d384.png\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 480,\n" +
                "          \"price\": 480,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:23\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3779142,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 460,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59707b5f08a0e.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 15000,\n" +
                "          \"price\": 15000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3811909,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 322,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586652b49d1a7.jpg\",\n" +
                "          \"inventory\": 100,\n" +
                "          \"originalPrice\": 2070,\n" +
                "          \"price\": 2070,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
//                "    \"id\": 574271,\n" +
                "      \"goodsSku\": {\n" +
                "        \"id\": 371,\n" +
                "        \"name\": \"+\",\n" +
                "        \"skuSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58406071f1fc7.jpg\",\n" +
                "        \"inventory\": 1,\n" +
                "        \"originalPrice\": 650000,\n" +
                "        \"price\": 650000,\n" +
                "        \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "      },\n" +
                "      {\n" +
//                "    \"id\": 574271,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 277,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\n" +
                "          \"inventory\": 10,\n" +
                "          \"originalPrice\": 16200,\n" +
                "          \"price\": 16200,\n" +
                "          \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "      },\n" +
                "      {\n" +
//                "    \"id\": 574271,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 447,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201808/file_5b74f0e3a396d.jpg\",\n" +
                "          \"inventory\": 93,\n" +
                "          \"originalPrice\": 669,\n" +
                "          \"price\": 669,\n" +
                "          \"disable\": 0\n" +
                "        }\n" +
//                "    \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);

    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试1，连续删除3件商品，删除收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(15)
    public void deleteFavoritesId1() throws Exception {

        // userId = 20
        String token = this.userLogin("10101113105", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3735458").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        responseString = mallClient.delete().uri("/favorites/3768225").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        responseString = mallClient.delete().uri("/favorites/3800992").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);

    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试2，删除收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(16)
    public void deleteFavoritesId2() throws Exception {

        // userId = 14706
        String token = this.userLogin("57013605122", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3815580").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 2,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3782813,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 451,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59702131d843c.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 6000,\n" +
                "          \"price\": 6000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3750046,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 291,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 130000,\n" +
                "          \"price\": 130000,\n" +
                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:23\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);

    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试3，删除收藏失败，该用户未收藏该商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(17)
    public void deleteFavoritesId3() throws Exception {

        // userId = 14712
        String token = this.userLogin("45209106845", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3782810").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo("该用户未收藏该商品")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试4，删除收藏失败，该收藏id不存在
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(18)
    public void deleteFavoritesId4() throws Exception {

        // userId = 14712
        String token = this.userLogin("45209106845", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/37").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo("该收藏id不存在")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试5，删除收藏失败，该收藏id不存在
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(19)
    public void deleteFavoritesId5() throws Exception {

        // userId = 14712
        String token = this.userLogin("45209106845", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3227").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo("该收藏id不存在")
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家获得购物车列表  普通测试1，查询成功 page=1&pageSize=5
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(20)
    public void getCarts1() throws Exception {

        // userId = 20
        String token = this.userLogin("10101113105", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 5,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1001,\n" +
                "        \"goodsSkuId\": 393,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1002,\n" +
                "        \"goodsSkuId\": 658,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1003,\n" +
                "        \"goodsSkuId\": 77,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家获得购物车列表  普通测试2，查询成功 page=1&pageSize=3
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(21)
    public void getCarts2() throws Exception {

        // userId = 400
        String token = this.userLogin("35642539836", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=3").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1004,\n" +
                "        \"goodsSkuId\": 446,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家获得购物车列表  普通测试3，查询成功，购物车中无商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(22)
    public void getCarts3() throws Exception {

        // userId = 4000
        String token = this.userLogin("28883882732", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=20").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 20,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家获得购物车列表  普通测试4，查询成功，购物车中无商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(23)
    public void getCarts4() throws Exception {

        // userId = 9782
        String token = this.userLogin("7912044979", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=2").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 2,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家获得购物车列表  普通测试5，查询成功，购物车中无商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(24)
    public void getCarts5() throws Exception {

        // userId = 9781
        String token = this.userLogin("29970839554", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=4&pageSize=222").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 4,\n" +
                "    \"pageSize\": 222,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家将商品加入购物车  普通测试1，加入成功并查询
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(25)
    public void postCarts1() throws Exception {

        // userId = 99
        String token = this.userLogin("16436076738", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 300);
        body.put("quantity", 1111);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.post().uri("/carts").header("authorization",token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
//                "    \"id\": 0,\n" +
                "    \"goodsSkuId\": 300,\n" +
                "    \"skuName\": \"+\",\n" +
                "    \"quantity\": 1111,\n" +
                "    \"price\": 68000,\n" +
                "    \"couponActivity\": [\n" +
                "    ]\n" +
//                "    \"gmtCreate\": \"string\",\n" +
//                "    \"gmtModified\": null\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
//                "        \"id\": 1004,\n" +
                "        \"goodsSkuId\": 300,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 1111,\n" +
                "        \"price\": 68000,\n" +
//                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
//                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家将商品加入购物车  普通测试2，加入成功并查询
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(26)
    public void postCarts2() throws Exception {

        // userId = 999
        String token = this.userLogin("59506839941", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 300);
        body.put("quantity", 111);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.post().uri("/carts").header("authorization",token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
//                "    \"id\": 0,\n" +
                "    \"goodsSkuId\": 300,\n" +
                "    \"skuName\": \"+\",\n" +
                "    \"quantity\": 111,\n" +
                "    \"price\": 68000,\n" +
                "    \"couponActivity\": [\n" +
                "    ]\n" +
//                "    \"gmtCreate\": \"string\",\n" +
//                "    \"gmtModified\": \"string\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
//                "        \"id\": 1004,\n" +
                "        \"goodsSkuId\": 300,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 111,\n" +
                "        \"price\": 68000,\n" +
//                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
//                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家将商品加入购物车  普通测试3，该商品原来已经在购物车。
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(27)
    public void postCarts3() throws Exception {

        // userId = 100
        String token = this.userLogin("48673740540", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 367);
        body.put("quantity", 111);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.post().uri("/carts").header("authorization",token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 1021,\n" +
                "    \"goodsSkuId\": 367,\n" +
                "    \"skuName\": \"+\",\n" +
                "    \"quantity\": 211,\n" +
                "    \"price\": 24120,\n" +
                "    \"couponActivity\": [\n" +
                "    ],\n" +
                "    \"gmtCreate\": \"2020-11-24T17:06:28\"\n" +
//                "    \"gmtModified\": \"string\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1021,\n" +
                "        \"goodsSkuId\": 367,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 211,\n" +
                "        \"price\": 24120,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
//                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家将商品加入购物车  普通测试4，该商品原来已经在购物车。
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(28)
    public void postCarts4() throws Exception {

        // userId = 101
        String token = this.userLogin("76876407281", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 446);
        body.put("quantity", 2);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.post().uri("/carts").header("authorization",token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 1024,\n" +
                "    \"goodsSkuId\": 446,\n" +
                "    \"skuName\": \"+\",\n" +
                "    \"quantity\": 102,\n" +
                "    \"price\": 1799,\n" +
                "    \"couponActivity\": [\n" +
                "    ],\n" +
                "    \"gmtCreate\": \"2020-11-24T17:06:28\"\n" +
//                "    \"gmtModified\": \"string\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1024,\n" +
                "        \"goodsSkuId\": 446,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 102,\n" +
                "        \"price\": 1799,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
//                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1025,\n" +
                "        \"goodsSkuId\": 643,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1026,\n" +
                "        \"goodsSkuId\": 521,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家清空购物车  普通测试1，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(29)
    public void deleteCarts1() throws Exception {

        // userId = 1
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家清空购物车  普通测试2，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(30)
    public void deleteCarts2() throws Exception {

        // userId = 2
        String token = this.userLogin("36040122840", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家清空购物车  普通测试3，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(31)
    public void deleteCarts3() throws Exception {

        // userId = 3
        String token = this.userLogin("7306155755", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家清空购物车  普通测试4，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(32)
    public void deleteCarts4() throws Exception {

        // userId = 4
        String token = this.userLogin("14455881448", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家清空购物车  普通测试5，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(33)
    public void deleteCarts5() throws Exception {

        // userId = 5
        String token = this.userLogin("8906373389", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家清空购物车  普通测试6，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(34)
    public void deleteCarts6() throws Exception {

        // userId = 6
        String token = this.userLogin("39118189028", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=2&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试1，修改成功并查询，此时修改数量
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(35)
    public void putCartsId1() throws Exception {

        // userId = 10000
        String token = this.userLogin("39288437216", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 367);
        body.put("quantity", 111);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1041").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1041,\n" +
                "        \"goodsSkuId\": 367,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 111,\n" +
                "        \"price\": 24120,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
//                "        \"gmtModified\": \"2020-11-24T17:06:28\"\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1042,\n" +
                "        \"goodsSkuId\": 658,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1043,\n" +
                "        \"goodsSkuId\": 77,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试2，修改成功并查询，此时修改数量
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(36)
    public void putCartsId2() throws Exception {

        // userId = 10001
        String token = this.userLogin("41372695510", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 446);
        body.put("quantity", 101);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1044").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 2,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1044,\n" +
                "        \"goodsSkuId\": 446,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 101,\n" +
                "        \"price\": 1799,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
//                "        \"gmtModified\": \"2020-11-24T17:06:28\"\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1045,\n" +
                "        \"goodsSkuId\": 643,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }



    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试3,要修改的skuId与原先不是同一个spuId，字段不合法
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(37)
    public void putCartsId3() throws Exception {

        // userId = 10002
        String token = this.userLogin("32485307410", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 522);
        body.put("quantity", 101);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1046").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试4,要修改的skuId与原先不是同一个spuId，字段不合法
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(38)
    public void putCartsId4() throws Exception {

        // userId = 10002
        String token = this.userLogin("32485307410", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 322);
        body.put("quantity", 101);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1047").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.FIELD_NOTVALID.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家删除购物车中商品  普通测试1，删除成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(39)
    public void deleteCartsId1() throws Exception {

        // userId = 1000
        String token = this.userLogin("97142877706", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/1061").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家删除购物车中商品  普通测试2，删除成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(40)
    public void deleteCartsId2() throws Exception {

        // userId = 1001
        String token = this.userLogin("10153144607", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/1062").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1063,\n" +
                "        \"goodsSkuId\": 77,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\",\n" +
                "        \"gmtModified\": \"2020-11-24T17:06:28\",\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家删除购物车中商品  普通测试,该购物车id不存在,删除失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(41)
    public void deleteCartsId3() throws Exception {

        // userId = 10
        String token = this.userLogin("19769355952", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/45").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo("该购物车id不存在")
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家删除购物车中商品  普通测试,该购物车id所属买家与操作用户不一致,删除失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(42)
    public void deleteCartsId4() throws Exception {

        // userId = 11
        String token = this.userLogin("14902184265", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/1080").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .jsonPath("$.errmsg").isEqualTo("该购物车id所属买家与操作用户不一致")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 购物车服务-买家删除购物车中商品  普通测试,该购物车id所属买家与操作用户不一致,删除失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(43)
    public void deleteCartsId5() throws Exception {

        // userId = 12
        String token = this.userLogin("5217325133", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/1080").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .jsonPath("$.errmsg").isEqualTo("该购物车id所属买家与操作用户不一致")
                .returnResult()
                .getResponseBodyContent();
    }
}

