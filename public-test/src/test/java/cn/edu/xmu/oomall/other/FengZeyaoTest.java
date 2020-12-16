package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 其他模块测试 收藏
 * @author ：Zeyao Feng 21620172203301
 * @date ：Created in 2020/12/15 15:31
 */
@SpringBootTest(classes = Application.class)
public class FengZeyaoTest {
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
    * 1. 买家查看收藏，分页信息错误
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 15:41
    */
    @Test
    @Order(1)
    public void getFavorites1() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=-1&pageSize=-1").header("authorization",token).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
       }



    /**
    * 2. 买家查看收藏，不输入分页信息（采用默认分页page=1，pageSize=10）
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 15:41
    */
    @Test
    @Order(2)
    public void getFavorites2() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=&pageSize=").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 12,\n" +
                "    \"pages\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3833754,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "          \"originalPrice\": 980000,\n" +
                "          \"inventory\": 1,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 980000,\n" +
                "          \"id\": 273\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:10\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833755,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\n" +
                "          \"originalPrice\": 850,\n" +
                "          \"inventory\": 99,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 850,\n" +
                "          \"id\": 274\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:13\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833756,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\n" +
                "          \"originalPrice\": 4028,\n" +
                "          \"inventory\": 10,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 4028,\n" +
                "          \"id\": 275\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:14\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833757,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\n" +
                "          \"originalPrice\": 6225,\n" +
                "          \"inventory\": 10,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 6225,\n" +
                "          \"id\": 276\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:16\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833758,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\n" +
                "          \"originalPrice\": 16200,\n" +
                "          \"inventory\": 10,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 16200,\n" +
                "          \"id\": 277\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:17\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833759,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\n" +
                "          \"originalPrice\": 1199,\n" +
                "          \"inventory\": 46100,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 1199,\n" +
                "          \"id\": 278\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:19\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833760,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\n" +
                "          \"originalPrice\": 1199,\n" +
                "          \"inventory\": 500,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 1199,\n" +
                "          \"id\": 279\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:20\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833761,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\n" +
                "          \"originalPrice\": 2399,\n" +
                "          \"inventory\": 1834,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 2399,\n" +
                "          \"id\": 280\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:22\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833762,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\n" +
                "          \"originalPrice\": 1380000,\n" +
                "          \"inventory\": 1,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 1380000,\n" +
                "          \"id\": 281\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:23\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833763,\n" +
                "        \"goodSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\n" +
                "          \"originalPrice\": 120000,\n" +
                "          \"inventory\": 1,\n" +
                "          \"disable\": 0,\n" +
                "          \"price\": 120000,\n" +
                "          \"id\": 282\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-15T23:46:25\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
    * 3. 买家查看收藏，返回为空
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 16:46
    */
    @Test
    @Order(3)
    public void getFavorites3() throws Exception {

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": []\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
    * 4. 买家重复收藏，返回他当前收藏的商品
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 16:50
    */
    @Test
    @Order(4)
    public void createFavorites1() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/favorites/goods/273").header("authorization",token).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 3833754,\n" +
                "    \"goodSku\": {\n" +
                "      \"name\": \"+\",\n" +
                "      \"skuSn\": null,\n" +
                "      \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "      \"originalPrice\": 980000,\n" +
                "      \"inventory\": 1,\n" +
                "      \"disable\": 0,\n" +
                "      \"price\": 980000,\n" +
                "      \"id\": 273\n" +
                "    },\n" +
                "    \"gmtCreate\": \"2020-12-16T00:56:35.0780998\"\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
    * 5. 买家收藏的商品不存在
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 16:52
    */
    @Test
    @Order(5)
    public void createFavorites2() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/favorites/goods/12345678").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
    * 6. 新增地址，买家地址已经达到上限
    * @author: Zeyao Feng
    * @date: Created in 2020/12/15 17:08
    */
    @Test
    @Order(6)
    public void addAddress1() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"12345678910\",\n" +
                "  \"regionId\": 1\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADDRESS_OUTLIMIT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }



    /**
    * 7. 修改地址 地址Id不存在
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 3:55
    */
    @Test
    @Order(7)
    public void updateAddress1() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"12345678910\",\n" +
                "  \"regionId\": 1\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
    * 8. 修改地址 手机号格式错误
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:02
    */
    @Test
    @Order(8)
    public void updateAddress2() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"123456\",\n" +
                "  \"regionId\": 1\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
    * 9. 修改地址 地区id不存在
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:06
    */
    @Test
    @Order(9)
    public void updateAddress3() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"123456\",\n" +
                "  \"regionId\": -1\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
    * 10. 删除地址，地址Id不存在
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:12
    */
    @Test
    @Order(10)
    public void deleteAddress1() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/10000").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
    * 11. 查询某个地区的所有上级地区，该地区为顶级地区（例如北京）,pid=0
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:20
    */
    @Test
    @Order(11)
    public void selectAncestorRegion1() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/1/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
    * 12. 查询某个地区的所有上级地区，该地区为1级地区（例如厦门市）,pid>0
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:24
    */
    @Test
    @Order(12)
    public void selectAncestorRegion2() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/13/ancestor").header("authorization",token).exchange()
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
                "      \"id\": 13,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"福建省\",\n" +
                "      \"postalCode\": 350000,\n" +
                "      \"state\": 0,\n" +
                "      \"gmtCreate\": \"2020-12-01T12:46:41\",\n" +
                "      \"gmtModified\": null\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
    * 13. 查询某个地区的所有上级地区，该地区为2级地区（例如思明区）,pid>0
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:27
    */
    @Test
    @Order(13)
    public void selectAncestorRegion3() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/1407/ancestor").header("authorization",token).exchange()
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
                "      \"id\": 147,\n" +
                "      \"pid\": 13,\n" +
                "      \"name\": \"厦门市\",\n" +
                "      \"postalCode\": 350200,\n" +
                "      \"state\": 0,\n" +
                "      \"gmtCreate\": \"2020-12-01T12:46:41\",\n" +
                "      \"gmtModified\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 13,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"福建省\",\n" +
                "      \"postalCode\": 350000,\n" +
                "      \"state\": 0,\n" +
                "      \"gmtCreate\": \"2020-12-01T12:46:41\",\n" +
                "      \"gmtModified\": null\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }



    /**
    * 14. 查询某个地区的所有上级地区，该地区不存在
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:32
    */
    @Test
    @Order(14)
    public void selectAncestorRegion4() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/140700/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }



    /**
    * 15. 删除地址，成功，27号买家仅有一条地址
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:18
    */
    @Test
    @Order(15)
    public void deleteAddress2() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/21").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult().getResponseBodyContent();

        //再次查询该买家的地址
        byte[] responseString2 = mallClient.get().uri("/addresses?page=&pageSize=").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": []\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString2, StandardCharsets.UTF_8), false);
    }


    /**
    * 16. 买家查询地址 第一页
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:54
    */
    @Test
    @Order(16)
    public void selectAddress1() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.put().uri("/addresses?page=1&pageSize=7").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 7,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:23:03\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 2,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:47:57\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:33\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:34\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 5,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:36\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 6,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:36\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 7,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:36\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
    * 17. 买家查询地址，第二页
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:56
    */
    @Test
    @Order(17)
    public void selectAddress2() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.put().uri("/addresses?page=2&pageSize=3").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 7,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:34\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 5,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:36\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 6,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:36\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
    * 18. 买家查询地址 第三页显示 未完全显示
    * @author: Zeyao Feng
    * @date: Created in 2020/12/16 4:59
    */
    @Test
    @Order(18)
    public void selectAddress3() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.put().uri("/addresses?page=4&pageSize=6").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 4,\n" +
                "    \"pageSize\": 6,\n" +
                "    \"page\": 4,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 19,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:41\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 20,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"gmtCreate\": \"2020-12-16T03:48:41\",\n" +
                "        \"gmtModified\": null,\n" +
                "        \"default\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }









}
