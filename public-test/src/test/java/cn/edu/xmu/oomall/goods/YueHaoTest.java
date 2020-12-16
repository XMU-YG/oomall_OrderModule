package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;


/**
 * @author 岳皓
 * 学号：24320182203319
 * created at 2020/12/3
 * modified at 2020/12/14
 *
 */
@SpringBootTest(classes = Application.class)
public class YueHaoTest {
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

    /** 1
     * 不需要登录-查询商品分类关系1-存在该分类
     **/
    @Test
    public void getCategorySubs() throws Exception {
        byte[] responseString=mallClient.get().uri("/categories/122/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":123,\"pid\":122,\"name\":\"大师原作\",\"parentId\":122,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":124,\"pid\":122,\"name\":\"艺术衍生品\",\"parentId\":122,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 2
     * 不需要登录-查询商品分类关系2-不存在该分类
     **/
    @Test
    public void getCategorySubs2() throws Exception {//检测如果没有此id
        byte[]responseString=mallClient.get().uri("/categories/1000/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"categoryId不存在：1000\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 3
     * 不需要登录-查询商品分类关系3-该分类下无子分类
     **/
    @Test
    public void getCategorySubs3() throws Exception {
        byte[]responseString=mallClient.get().uri("/categories/123/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 4
     * 无需登录
     * 查询sku状态
     **/
    @Test
    public void getSkuStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未上架\",\"code\":0},{\"name\":\"上架\",\"code\":4},{\"name\":\"已删除\",\"code\":6}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 5
     * 无需登录
     * 查询所有sku-不加任何条件
     **/
    @Test
    public void getSkus1() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":406,\"pages\":41,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disable\":false,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disable\":false,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disable\":false,\"price\":16200},{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disable\":false,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disable\":false,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disable\":false,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disable\":false,\"price\":1380000},{\"id\":282,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"disable\":false,\"price\":120000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 6
     * 无需登录
     * 查询所有sku-spuId存在
     **/
    @Test
    public void getSkus2() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?spuId=273")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 7
     * 无需登录
     * 查询所有sku-shopId不存在
     **/
    @Test
    public void getSkus5() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?shopId=100")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 8
     * 无需登录
     * 查询所有sku-pageSize=20
     **/
    @Test
    public void getSkus6() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?pageSize=20")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":406,\"pages\":21,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disable\":false,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disable\":false,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disable\":false,\"price\":16200},{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disable\":false,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disable\":false,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disable\":false,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disable\":false,\"price\":1380000},{\"id\":282,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"disable\":false,\"price\":120000},{\"id\":283,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":780000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57faed5b5da32.jpg\",\"inventory\":1,\"disable\":false,\"price\":780000},{\"id\":284,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620c598a78f.jpg\",\"inventory\":1,\"disable\":false,\"price\":880000},{\"id\":285,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620cca773f3.jpg\",\"inventory\":1,\"disable\":false,\"price\":1880000},{\"id\":286,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1950000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620dd2a854e.jpg\",\"inventory\":1,\"disable\":false,\"price\":1950000},{\"id\":287,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2600000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621005f229a.jpg\",\"inventory\":1,\"disable\":false,\"price\":2600000},{\"id\":288,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":550000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586211ad843f6.jpg\",\"inventory\":1,\"disable\":false,\"price\":550000},{\"id\":289,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":480000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586213438566e.jpg\",\"inventory\":1,\"disable\":false,\"price\":480000},{\"id\":290,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":180000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214e020ab2.jpg\",\"inventory\":1,\"disable\":false,\"price\":180000},{\"id\":291,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":130000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\"inventory\":1,\"disable\":false,\"price\":130000},{\"id\":292,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":200000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621bd1768b4.jpg\",\"inventory\":1,\"disable\":false,\"price\":200000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 9
     * 无需登录
     * 查询所有sku-pageSize=100
     **/
    @Test
    public void getSkus7() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?pageSize=100")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":406,\"pages\":5,\"pageSize\":100,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disable\":false,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disable\":false,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disable\":false,\"price\":16200},{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disable\":false,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disable\":false,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disable\":false,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disable\":false,\"price\":1380000},{\"id\":282,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"disable\":false,\"price\":120000},{\"id\":283,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":780000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57faed5b5da32.jpg\",\"inventory\":1,\"disable\":false,\"price\":780000},{\"id\":284,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620c598a78f.jpg\",\"inventory\":1,\"disable\":false,\"price\":880000},{\"id\":285,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620cca773f3.jpg\",\"inventory\":1,\"disable\":false,\"price\":1880000},{\"id\":286,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1950000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620dd2a854e.jpg\",\"inventory\":1,\"disable\":false,\"price\":1950000},{\"id\":287,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2600000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621005f229a.jpg\",\"inventory\":1,\"disable\":false,\"price\":2600000},{\"id\":288,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":550000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586211ad843f6.jpg\",\"inventory\":1,\"disable\":false,\"price\":550000},{\"id\":289,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":480000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586213438566e.jpg\",\"inventory\":1,\"disable\":false,\"price\":480000},{\"id\":290,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":180000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214e020ab2.jpg\",\"inventory\":1,\"disable\":false,\"price\":180000},{\"id\":291,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":130000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\"inventory\":1,\"disable\":false,\"price\":130000},{\"id\":292,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":200000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621bd1768b4.jpg\",\"inventory\":1,\"disable\":false,\"price\":200000},{\"id\":293,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861e6897de94.jpg\",\"inventory\":1,\"disable\":false,\"price\":880000},{\"id\":294,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":650000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862058d26ef7.jpg\",\"inventory\":1,\"disable\":false,\"price\":650000},{\"id\":295,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":260000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621ca2ec201.jpg\",\"inventory\":1,\"disable\":false,\"price\":260000},{\"id\":296,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621d72c8718.jpg\",\"inventory\":1,\"disable\":false,\"price\":120000},{\"id\":297,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":320000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620a8cb4e13.jpg\",\"inventory\":1,\"disable\":false,\"price\":320000},{\"id\":298,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":260000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621edebc533.jpg\",\"inventory\":1,\"disable\":false,\"price\":260000},{\"id\":299,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621f74c8ec6.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":300,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":68000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621fe110292.jpg\",\"inventory\":1,\"disable\":false,\"price\":68000},{\"id\":301,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":250000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58622084a52a4.jpg\",\"inventory\":1,\"disable\":false,\"price\":250000},{\"id\":302,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":800000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862284b3e0e0.jpg\",\"inventory\":1,\"disable\":false,\"price\":800000},{\"id\":303,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":320000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586220ea9180c.jpg\",\"inventory\":1,\"disable\":false,\"price\":320000},{\"id\":304,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":78000,\"imageUrl\":\"http://47.52.88.176/file/images/201701/file_5871dca12fe88.jpg\",\"inventory\":1,\"disable\":false,\"price\":78000},{\"id\":305,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":48000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586221c3b00cb.jpg\",\"inventory\":1,\"disable\":false,\"price\":48000},{\"id\":306,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":228000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586222971edb1.jpg\",\"inventory\":1,\"disable\":false,\"price\":228000},{\"id\":307,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":28000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fb131b380b0.jpg\",\"inventory\":1,\"disable\":false,\"price\":28000},{\"id\":308,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":78000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862230d20162.jpg\",\"inventory\":1,\"disable\":false,\"price\":78000},{\"id\":309,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":42000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fb31a8e6a25.jpg\",\"inventory\":1,\"disable\":false,\"price\":42000},{\"id\":310,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":26000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862235ce6acc.jpg\",\"inventory\":1,\"disable\":false,\"price\":26000},{\"id\":311,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":52000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586223cd6a466.jpg\",\"inventory\":1,\"disable\":false,\"price\":52000},{\"id\":312,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":280000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862262768fc8.jpg\",\"inventory\":1,\"disable\":false,\"price\":280000},{\"id\":313,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":18000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586224fc291e5.jpg\",\"inventory\":1,\"disable\":false,\"price\":18000},{\"id\":314,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":18000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586225ad2376c.jpg\",\"inventory\":1,\"disable\":false,\"price\":18000},{\"id\":315,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":45000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586226eb05127.jpg\",\"inventory\":1,\"disable\":false,\"price\":45000},{\"id\":316,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":128340,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5862277f60948.jpg\",\"inventory\":1,\"disable\":false,\"price\":128340},{\"id\":317,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":396,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d8adf3266.jpg\",\"inventory\":100,\"disable\":false,\"price\":396},{\"id\":318,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2688,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d7fa1c348.jpg\",\"inventory\":10,\"disable\":false,\"price\":2688},{\"id\":319,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":585,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fb5a07592c4.jpg\",\"inventory\":100,\"disable\":false,\"price\":585},{\"id\":320,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1820,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58664cd98be85.jpg\",\"inventory\":100,\"disable\":false,\"price\":1820},{\"id\":321,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1320,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58665118bfe59.jpg\",\"inventory\":100,\"disable\":false,\"price\":1320},{\"id\":322,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2070,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586652b49d1a7.jpg\",\"inventory\":100,\"disable\":false,\"price\":2070},{\"id\":323,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3860,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5866536619b9f.jpg\",\"inventory\":99,\"disable\":false,\"price\":3860},{\"id\":324,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1760,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5866561948aa9.jpg\",\"inventory\":100,\"disable\":false,\"price\":1760},{\"id\":325,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3980,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58665950873eb.jpg\",\"inventory\":100,\"disable\":false,\"price\":3980},{\"id\":326,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1320,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5866586f8e89a.jpg\",\"inventory\":100,\"disable\":false,\"price\":1320},{\"id\":327,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":21600,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58665aa3ef4e3.jpg\",\"inventory\":100,\"disable\":false,\"price\":21600},{\"id\":328,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":660,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58665bc3eb222.jpg\",\"inventory\":100,\"disable\":false,\"price\":660},{\"id\":329,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":17800,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58665d14dbead.jpg\",\"inventory\":100,\"disable\":false,\"price\":17800},{\"id\":330,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3970,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58665f189b145.jpg\",\"inventory\":10,\"disable\":false,\"price\":3970},{\"id\":331,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":5490,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58666054b0bc6.jpg\",\"inventory\":99,\"disable\":false,\"price\":5490},{\"id\":332,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":24800,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5866618849b2f.jpg\",\"inventory\":100,\"disable\":false,\"price\":24800},{\"id\":333,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3600,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5866629229288.jpg\",\"inventory\":100,\"disable\":false,\"price\":3600},{\"id\":334,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":24120,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c9703663a.jpg\",\"inventory\":10,\"disable\":false,\"price\":24120},{\"id\":335,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":44820,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fb708c2187c.jpg\",\"inventory\":10,\"disable\":false,\"price\":44820},{\"id\":336,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":151200,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fb71658bfc6.jpg\",\"inventory\":10,\"disable\":false,\"price\":151200},{\"id\":337,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3420,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fb720b02c15.jpg\",\"inventory\":10,\"disable\":false,\"price\":3420},{\"id\":338,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":15120,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fb72dc14919.jpg\",\"inventory\":10,\"disable\":false,\"price\":15120},{\"id\":340,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1999,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cf32b5eb1f.jpg\",\"inventory\":72,\"disable\":false,\"price\":1999},{\"id\":341,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":11,\"imageUrl\":\"\",\"inventory\":16,\"disable\":false,\"price\":11},{\"id\":342,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":0,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580ccf1e80279.jpg\",\"inventory\":0,\"disable\":false,\"price\":0},{\"id\":343,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":0,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cd22dd92ef.jpeg\",\"inventory\":2,\"disable\":false,\"price\":0},{\"id\":344,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":0,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cd22dd92ef.jpeg\",\"inventory\":149,\"disable\":false,\"price\":0},{\"id\":345,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":15120,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405bb23272d.jpg\",\"inventory\":10,\"disable\":false,\"price\":15120},{\"id\":346,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405bdf95675.jpg\",\"inventory\":100,\"disable\":false,\"price\":850},{\"id\":347,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405c09ea4f3.jpg\",\"inventory\":100,\"disable\":false,\"price\":4028},{\"id\":348,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405c3a51b2f.jpg\",\"inventory\":100,\"disable\":false,\"price\":6225},{\"id\":349,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405c653942d.jpg\",\"inventory\":10,\"disable\":false,\"price\":16200},{\"id\":350,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":396,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405c88a906e.jpg\",\"inventory\":100,\"disable\":false,\"price\":396},{\"id\":351,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2688,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405caec994c.jpg\",\"inventory\":100,\"disable\":false,\"price\":2688},{\"id\":352,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":585,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405ce8c5146.jpg\",\"inventory\":100,\"disable\":false,\"price\":585},{\"id\":353,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1820,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405d1303c05.jpg\",\"inventory\":10,\"disable\":false,\"price\":1820},{\"id\":354,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1320,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405d7747c80.jpg\",\"inventory\":10,\"disable\":false,\"price\":1320},{\"id\":355,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2070,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405da8b4146.jpg\",\"inventory\":10,\"disable\":false,\"price\":2070},{\"id\":356,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3860,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405dd66f63c.jpg\",\"inventory\":10,\"disable\":false,\"price\":3860},{\"id\":357,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1760,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405dfb775fd.jpg\",\"inventory\":10,\"disable\":false,\"price\":1760},{\"id\":358,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3980,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405e1f634e8.jpg\",\"inventory\":10,\"disable\":false,\"price\":3980},{\"id\":359,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1320,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405e3fe2d34.jpg\",\"inventory\":10,\"disable\":false,\"price\":1320},{\"id\":360,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":21600,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405e6a98a57.jpg\",\"inventory\":10,\"disable\":false,\"price\":21600},{\"id\":361,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":660,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405e8e92dae.jpg\",\"inventory\":10,\"disable\":false,\"price\":660},{\"id\":362,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":17800,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405eb2c2268.jpg\",\"inventory\":10,\"disable\":false,\"price\":17800},{\"id\":363,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3970,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405ed90f653.jpg\",\"inventory\":10,\"disable\":false,\"price\":3970},{\"id\":364,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":5490,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405f02aedf7.jpg\",\"inventory\":10,\"disable\":false,\"price\":5490},{\"id\":365,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":24800,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405f34a6d3c.jpg\",\"inventory\":10,\"disable\":false,\"price\":24800},{\"id\":366,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3600,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405f8517095.jpg\",\"inventory\":10,\"disable\":false,\"price\":3600},{\"id\":367,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":24120,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405fb2b92d4.png\",\"inventory\":10,\"disable\":false,\"price\":24120},{\"id\":368,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":3420,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58405fdf8ae75.jpg\",\"inventory\":10,\"disable\":false,\"price\":3420},{\"id\":369,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":44820,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5840601669568.jpg\",\"inventory\":10,\"disable\":false,\"price\":44820},{\"id\":370,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":151200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5840604bca747.jpg\",\"inventory\":10,\"disable\":false,\"price\":151200},{\"id\":371,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":650000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58406071f1fc7.jpg\",\"inventory\":1,\"disable\":false,\"price\":650000},{\"id\":372,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58406096594d8.jpg\",\"inventory\":1,\"disable\":false,\"price\":880000},{\"id\":373,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_584060bce8891.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 10
     * 无需登录
     * 查询所有sku-page=2
     **/
    @Test
    public void getSkus8() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?page=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse =  "{\"errno\":0,\"data\":{\"total\":406,\"pages\":41,\"pageSize\":10,\"page\":2,\"list\":[{\"id\":283,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":780000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57faed5b5da32.jpg\",\"inventory\":1,\"disable\":false,\"price\":780000},{\"id\":284,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620c598a78f.jpg\",\"inventory\":1,\"disable\":false,\"price\":880000},{\"id\":285,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620cca773f3.jpg\",\"inventory\":1,\"disable\":false,\"price\":1880000},{\"id\":286,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1950000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620dd2a854e.jpg\",\"inventory\":1,\"disable\":false,\"price\":1950000},{\"id\":287,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2600000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621005f229a.jpg\",\"inventory\":1,\"disable\":false,\"price\":2600000},{\"id\":288,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":550000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586211ad843f6.jpg\",\"inventory\":1,\"disable\":false,\"price\":550000},{\"id\":289,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":480000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586213438566e.jpg\",\"inventory\":1,\"disable\":false,\"price\":480000},{\"id\":290,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":180000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214e020ab2.jpg\",\"inventory\":1,\"disable\":false,\"price\":180000},{\"id\":291,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":130000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\"inventory\":1,\"disable\":false,\"price\":130000},{\"id\":292,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":200000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621bd1768b4.jpg\",\"inventory\":1,\"disable\":false,\"price\":200000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 11
     * 无需登录
     * 查询所有sku-page=40
     **/
    @Test
    public void getSkus9() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?page=40")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":406,\"pages\":41,\"pageSize\":10,\"page\":40,\"list\":[{\"id\":664,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":380,\"imageUrl\":\"http://47.52.88.176/file/images/201903/file_5c7896020673b.jpg\",\"inventory\":30,\"disable\":false,\"price\":380},{\"id\":665,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":380,\"imageUrl\":\"http://47.52.88.176/file/images/201903/file_5c7896ceec463.jpg\",\"inventory\":30,\"disable\":false,\"price\":380},{\"id\":666,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":600,\"imageUrl\":\"http://47.52.88.176/file/images/201903/file_5c7899a3523ce.jpg\",\"inventory\":30,\"disable\":false,\"price\":600},{\"id\":667,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":780,\"imageUrl\":\"http://47.52.88.176/file/images/201903/file_5c789a1c83af4.jpg\",\"inventory\":30,\"disable\":false,\"price\":780},{\"id\":668,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380,\"imageUrl\":\"http://47.52.88.176/file/images/201903/file_5c789a75c34c7.jpg\",\"inventory\":30,\"disable\":false,\"price\":1380},{\"id\":669,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":799,\"imageUrl\":\"http://47.52.88.176/file/images/201903/file_5c789d34764c4.jpg\",\"inventory\":30,\"disable\":false,\"price\":799},{\"id\":670,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1299,\"imageUrl\":\"http://47.52.88.176/file/images/201903/file_5c789d915ae53.jpg\",\"inventory\":30,\"disable\":false,\"price\":1299},{\"id\":671,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":899,\"imageUrl\":\"http://47.52.88.176/file/images/201904/file_5cc2d8abb5a06.jpg\",\"inventory\":9998,\"disable\":false,\"price\":899},{\"id\":672,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":999,\"imageUrl\":\"http://47.52.88.176/file/images/201904/file_5cc2d9396546b.jpg\",\"inventory\":100000,\"disable\":false,\"price\":999},{\"id\":673,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":899,\"imageUrl\":\"http://47.52.88.176/file/images/201904/file_5cc8102eec10f.jpg\",\"inventory\":10006,\"disable\":false,\"price\":899}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 12
     * 无需登录
     * 查询评论的所有状态
     **/
    @Test
    public void getCommentStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/comments/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未审核\",\"code\":0},{\"name\":\"审核通过\",\"code\":1},{\"name\":\"审核不通过\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 13
     * 无需登录
     * 查询优惠券的所有状态
     **/
    @Test
    public void getCouponStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/coupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"不可用\",\"code\":0},{\"name\":\"可用\",\"code\":1},{\"name\":\"已使用\",\"code\":2},{\"name\":\"失效\",\"code\":3}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 14
     * 无需登录
     * 查询预售活动的所有状态
     **/
    @Test
    public void getPreSaleStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/presales/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"已下线\",\"code\":0},{\"name\":\"已上线\",\"code\":1},{\"name\":\"已删除\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 15
     * 无需登录
     * 查询团购活动的所有状态
     **/
    @Test
    public void getGrouponStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/groupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"已下线\",\"code\":0},{\"name\":\"已上线\",\"code\":1},{\"name\":\"已删除\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 16
     * 无需登录
     * 查询店铺的所有状态
     **/
    @Test
    public void getShopStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/shops/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未通过\",\"code\":0},{\"name\":\"上架\",\"code\":1},{\"name\":\"下架\",\"code\":2}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 17
     * 需管理员登录
     * 新增类目
     **/
    @Test
    public void insertCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"test\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/categories/123/subcategories")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/123/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":140,\"pid\":123,\"name\":\"test\",\"parentId\":123,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 18
     * 需管理员登录
     * 修改分类
     **/
    @Test
    public void modifyCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testCategory\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/categories/126")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/125/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":126,\"pid\":125,\"name\":\"testCategory\",\"parentId\":125,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":133,\"pid\":125,\"name\":\"腕表\",\"parentId\":125,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}],\"errmsg\":\"成功\"}\n";
                JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 19
     * 需管理员登录
     * 删除分类-删除子分类
     **/
    @Test
    public void deleteCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/123")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/122/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":124,\"pid\":122,\"name\":\"艺术衍生品\",\"parentId\":122,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 20
     * 需管理员登录
     * 删除分类-删除分类
     **/
    @Test
    public void deleteCategoryTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/131")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/131/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":504,\"errmsg\":\"categoryId不存在：131\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 21
     * 无需登录
     * 获取品牌
     **/
    @Test
    public void getBrands() throws Exception {
        byte[]responseString=mallClient.get().uri("/brands")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":49,\"pages\":5,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":71,\"name\":\"戴荣华\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":73,\"name\":\"黄卖九\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":74,\"name\":\"李进\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":75,\"name\":\"李菊生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":76,\"name\":\"李小聪\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":77,\"name\":\"刘伟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":78,\"name\":\"陆如\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":79,\"name\":\"秦锡麟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":80,\"name\":\"舒慧娟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 22
     * 无需登录
     * 获取品牌-page=5
     **/
    @Test
    public void getBrands2() throws Exception {
        byte[]responseString=mallClient.get().uri("/brands?page=5")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":49,\"pages\":5,\"pageSize\":10,\"page\":5,\"list\":[{\"id\":111,\"name\":\"孙星池\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":112,\"name\":\"林祺龙\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":113,\"name\":\"故宫\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":114,\"name\":\"方毅\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":115,\"name\":\"李晓辉\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":116,\"name\":\"揭金平\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":117,\"name\":\"杨曙华\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":118,\"name\":\"方冬生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":119,\"name\":\"皇家窑火\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 23
     * 管理员登录
     * 修改品牌-品牌ID不存在
     **/
    @Test
    public void modifyBrandTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/126")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"brandId 不存在：126\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    /** 24
     * 需管理员登录
     * 修改品牌信息
     **/
    @Test
    public void modifyBrandTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/71")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[]responseString2=mallClient.get().uri("/brands")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 ="{\"errno\":0,\"data\":{\"total\":49,\"pages\":5,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":71,\"name\":\"testBrand\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-15T13:54:29\"},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":73,\"name\":\"黄卖九\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":74,\"name\":\"李进\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":75,\"name\":\"李菊生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":76,\"name\":\"李小聪\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":77,\"name\":\"刘伟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":78,\"name\":\"陆如\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":79,\"name\":\"秦锡麟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":80,\"name\":\"舒慧娟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 25
     * 需管理员登录
     * 修改品牌信息
     **/
    @Test
    public void modifyBrandTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand\",\"detail\": \"testBrandDetail\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/119")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[]responseString2=mallClient.get().uri("/brands")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 ="{\"errno\":0,\"data\":{\"total\":49,\"pages\":5,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":71,\"name\":\"testBrand\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-15T13:54:29\"},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":73,\"name\":\"黄卖九\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":74,\"name\":\"李进\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":75,\"name\":\"李菊生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":76,\"name\":\"李小聪\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":77,\"name\":\"刘伟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":78,\"name\":\"陆如\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":79,\"name\":\"秦锡麟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":80,\"name\":\"舒慧娟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 26
     * 需管理员登录
     * 删除品牌信息-品牌id不存在
     **/
    @Test
    public void deleteBrandTest() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/10000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"brandId 不存在：10000\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
     }
    /** 27
     * 需管理员登录
     * 删除商品分类-分类id存在
     **/
    @Test
    public void deleteCategoryTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/10000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
    }
    /** 28
     * 需管理员登录
     * 删除品牌
     **/
    @Test
    public void deleteBrandTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/71")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[] responseString2=mallClient.get().uri("/brands")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":{\"total\":48,\"pages\":5,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":73,\"name\":\"黄卖九\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":74,\"name\":\"李进\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":75,\"name\":\"李菊生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":76,\"name\":\"李小聪\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":77,\"name\":\"刘伟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":78,\"name\":\"陆如\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":79,\"name\":\"秦锡麟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":80,\"name\":\"舒慧娟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":81,\"name\":\"王怀俊\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), true);
    }
    /** 29
     * 需管理员登录
     * 修改分类-分类id不存在
     **/
    @Test
    public void modifyCategoryTest() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testCategory\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/categories/10000")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
     }
    /** 30
     * 需管理员登录
     * 删除商品sku-skuid存在
     **/
    @Test
    public void deleteSkuTest() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/skus/10000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
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
}
