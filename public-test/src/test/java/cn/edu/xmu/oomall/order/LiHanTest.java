package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

/**
 * 测试用例：运费模板模块
 *
 * @author 李涵 19720182203919
 * Created at 14/12/2020 8:21 上午
 * Modified by Han Li at 14/12/2020 8:21 上午
 */
public class LiHanTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    /**
     * 初始化 Client
     */
    @BeforeEach
    public void setUp() {
        System.out.println("LHTest 管理端 Gate = " + managementGate);
        System.out.println("LHTest 商城 Gate = " + mallGate);

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://" + managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://" + mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     * 用户登入
     * @param userName 用户名
     * @param password 密码
     * @throws Exception parse error
     */
    private String login(String userName, String password) {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        assert requireJson != null;
        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        assert ret != null;
        return JacksonUtil.parseString(new String(ret, StandardCharsets.UTF_8), "data");
    }

    /*
    运费模板部分
     */

    private Long fmId = null;
    private String fmName = null;

    /**
     * 定义运费模板 (件数)
     * @throws Exception
     */
    @Test
    @Order(1)
    public void defFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"LH运费模板1\",\"type\":0,\"unit\":500}";
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        // 新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/shops/1/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expectedResponse, defString, false);

        // 查询运费模板
        JSONObject responseData = JSONObject
                .parseObject(defString)
                .getJSONObject("data");
        Long id = responseData.getLong("id");
        String name = responseData.getString("name");
        byte[] queryResponseString = manageClient
                .get()
                .uri("/shops/1/freightmodels/"+id)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        assert queryResponseString != null;
        String queryString = new String(queryResponseString, StandardCharsets.UTF_8);

        // 定义的模板头要和查询到的一致
        JSONAssert.assertEquals(queryString, defString, true);
        this.fmId = id;
        this.fmName = name;
    }

    /**
     * 定义 (件数) 运费模板 (模板名重复)
     * @throws Exception
     */
    @Test
    @Order(2)
    public void defDuplicateFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"LH运费模板1\",\"type\":1,\"unit\":500}";
        Integer expectErrNo = 802;

        // 尝试新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/shops/1/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONObject responseObj = JSONObject.parseObject(defString);
        Integer errNo = responseObj.getInteger("errno");
        String errMsg = responseObj.getString("errmsg");

        // errno
        Assert.isTrue(expectErrNo.equals(errNo), "errno 不是 " + expectErrNo + " 而是 " + errNo + " errMsg 是 " + errMsg);
    }

    /**
     * 定义 (重量) 运费模板 (即使 type 不同也存在模板名重复)
     * @throws Exception
     */
    @Test
    @Order(3)
    public void defDuplicateDifferentTypeFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"LH运费模板1\",\"type\":0,\"unit\":500}";
        Integer expectErrNo = 802;

        // 尝试新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/shops/1/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONObject responseObj = JSONObject.parseObject(defString);
        Integer errNo = responseObj.getInteger("errno");
        String errMsg = responseObj.getString("errmsg");

        // errno
        Assert.isTrue(expectErrNo.equals(errNo), "errno 不是 " + expectErrNo + " 而是 " + errNo + " errMsg 是 " + errMsg);
    }

    /**
     * 定义件数运费模板明细 (地区码为 200)
     * @throws Exception
     */
    @Test
    @Order(4)
    public void defPieceFreightModelRule() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\n" +
                "  \"regionId\": 200,\n" +
                "  \"firstItem\": 2,\n" +
                "  \"firstItemPrice\": 10000,\n" +
                "  \"additionalItems\": 2,\n" +
                "  \"additionalItemsPrice\": 5000\n" +
                "}";

        // 新建运费模板明细
        byte[] responseString = manageClient
                .post()
                .uri("/shops/1/freightmodels/" + fmId + "/pieceItems")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;

        // 查询运费模板明细
        byte[] queryResponseString = manageClient
                .get()
                .uri("/shops/1/freightmodels/" + fmId + "/pieceItems")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        assert queryResponseString != null;
        String queryString = new String(queryResponseString, StandardCharsets.UTF_8);
        JSONArray pieceItems = JSONObject
                .parseObject(queryString)
                .getJSONArray("data");

        // 定义的模板明细要和查询到的一致
        Assert.isTrue(pieceItems.size() == 1, "查询到的运费模板明细数量不是 1！fmId = " + fmId + " size=" + pieceItems.size());
        JSONObject pieceItem = pieceItems.getJSONObject(0);
        JSONAssert.assertEquals(pieceItem.toJSONString(), json, true);
    }

    private Long clonedFmId;
    private String clonedFmName;

    /**
     * 克隆运费模板 (件数)
     * @throws Exception
     */
    @Test
    @Order(5)
    public void cloneFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseBytes = manageClient
                .post()
                .uri("/shops/1/freightmodels/" + fmId +"/clone")
                .header("authorization",token)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

        // 获取定义的运费模板
        JSONObject queryResponse = JSONObject.parseObject(responseString);
        JSONObject clonedModel = queryResponse
                .getJSONObject("data");
        clonedFmId = clonedModel.getLong("id");
        clonedFmName = clonedModel.getString("name");

        // 查询定义的运费模板能否查出来

        byte[] queryResponseString = manageClient
                .get()
                .uri("/shops/1/freightmodels/" + clonedFmId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        assert queryResponseString != null;
        JSONAssert.assertEquals(
                new String(queryResponseString, StandardCharsets.UTF_8),
                clonedModel.toJSONString(),
                true);
    }

    /**
     * 克隆运费模板 (找不到源)
     * @throws Exception
     */
    @Test
    @Order(6)
    public void cloneFreightModelNotFound() throws Exception {
        String token = this.login("13088admin", "123456");
        manageClient
                .post()
                .uri("/shops/1/freightmodels/16576287412549612354963/clone")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 查询商家的运费模板 (无分页)
     * @throws Exception
     */
    @Test
    @Order(8)
    public void findFreightModels() throws Exception {
        String token = this.login("13088admin", "123456");

        // 新建运费模板
        byte[] responseString = manageClient
                .get()
                .uri("/shops/1/freightmodels")
                .header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);

        JSONObject response = JSONObject.parseObject(defString);

        Assert.isTrue(response.getString("errmsg").equals("成功"), "查询不成功");
        JSONArray modelArr = response
                .getJSONObject("data")
                .getJSONArray("list");

        boolean firstChecked = false;
        boolean clonedChecked = false;
        for (int i = 0; i < modelArr.size(); i++) {
            JSONObject model = modelArr.getJSONObject(i);
            if (model.getLong("id").equals(this.fmId) &&
                    model.getString("name").equals(this.fmName)) {
                firstChecked = true;
            } else if (model.getLong("id").equals(this.clonedFmId) &&
                    model.getString("name").equals(this.clonedFmName)) {
                clonedChecked = true;
            }
        }
        Assert.isTrue(firstChecked, "第一个运费模板没有找到，id=" + this.fmId);
        Assert.isTrue(clonedChecked, "克隆的运费模板没有找到，id=" + this.clonedFmId);
    }

    /**
     * 删除运费模板 (克隆源)
     * @throws Exception
     */
    @Test
    @Order(9)
    public void delFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseBytes = manageClient
                .delete()
                .uri("/shops/1/freightmodels/" + fmId)
                .header("authorization",token)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 看看还能不能查出来
        manageClient.get()
                .uri("/freightmodels/" + fmId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 删除运费模板 (找不到)
     * @throws Exception
     */
    @Test
    @Order(10)
    public void delClonedFreightModelNotFound() throws Exception {
        String token = this.login("13088admin", "123456");
        manageClient
                .delete()
                .uri("/shops/1/freightmodels/" + fmId) // 删过了
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /*
    订单部分
     */

    /**
     * 店家修改订单
     */
    @Test
    @Order(11)
    public void shopEditOrder() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/orders/19720182203919")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/19720182203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 19720182203919,\n" +
                "    \"message\": \"我愛你\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家修改订单 (无权限)
     */
    @Test
    @Order(12)
    public void shopEditOrderNoRights() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/orders/19720182203923") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isForbidden() // 不被批准
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE)
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/19720182203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"message\": \"你好锁\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家修改订单 (不存在订单)
     * @throws Exception
     */
    @Test
    @Order(13)
    public void shopEditOrderNotExist() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        manageClient
                .put()
                .uri("/orders/19720182203928") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单 (字段不合法)
     * @throws Exception
     */
    @Test
    @Order(14)
    public void shopEditOrderFieldIllegal() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{}";
        manageClient
                .put()
                .uri("/orders/19720182203919") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单 (伪造 JWT)
     * @throws Exception
     */
    @Test
    @Order(15)
    public void shopEditOrderTokenIllegal() throws Exception {
        // depart = 1
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        manageClient
                .put()
                .uri("/orders/19720182203928") // depart=2
                .header("authorization", "12u8789781379127312ui3y1i3")
                .bodyValue(body)
                .exchange()
                .expectStatus().isUnauthorized() // 不合法
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT)
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货 (字段不合法)
     * @throws Exception
     */
    @Test
    @Order(16)
    public void shopDeliverFieldIllegal() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{}";
        manageClient
                .put()
                .uri("/shops/1/orders/19720182203919/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID)
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货
     * @throws Exception
     */
    @Test
    @Order(17)
    public void shopDeliver() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/shops/1/orders/19720182203919/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/19720182203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 19720182203919,\n" +
                "    \"state\": 2,\n" +
                "    \"subState\": 24,\n" +
                "    \"shipmentSn\": \"1212121212123\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家发货 (已发货过)
     * @throws Exception
     */
    @Test
    @Order(18)
    public void shopDeliverAlreadyDelivered() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        manageClient
                .put()
                .uri("/shops/1/orders/19720182203919/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (状态不允许)
     * @throws Exception
     */
    @Test
    @Order(19)
    public void shopDeliverStateNotAllow() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        manageClient
                .put()
                .uri("/shops/1/orders/19720182203921/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (无权限)
     */
    @Test
    @Order(20)
    public void shopDeliverNoRights() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/shops/1/orders/19720182203923/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isForbidden() // 不被批准
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE)
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/19720182203923")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        // 仍是未发货
        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 19720182203923,\n" +
                "    \"state\": 2,\n" +
                "    \"subState\": 21,\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家发货 (不存在订单)
     * @throws Exception
     */
    @Test
    @Order(21)
    public void shopDeliverNotExist() throws Exception {
        // depart = 1
        String token = this.login("13088admin", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        manageClient
                .put()
                .uri("/shops/1/orders/19720182203928/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST)
                .returnResult()
                .getResponseBodyContent();
    }
}
