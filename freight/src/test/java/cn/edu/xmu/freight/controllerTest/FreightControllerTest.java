package cn.edu.xmu.freight.controllerTest;

import cn.edu.xmu.freight.FreightServiceApplication;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.encript.AES;
import org.json.JSONException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = FreightServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FreightControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(FreightControllerTest.class);
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    @Test
    public void getFreModelByIdTest()
    {
        String responseString=null;
        String token = creatTestToken(1L, 0L, 100);
        try
        {
            responseString=this.mvc.perform(get("/shops/1/freightmodels/1").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":1,\"name\":\"weight\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-16T17:16:00\",\"gmtModified\":null}}";
     try {
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    } catch (
    JSONException e) {
        e.printStackTrace();
    }
}
    /**
     * 测试获取模板概要功能
     * 操作的资源id不存在
     *
     * @throws Exception
     */
    @Test
    @Order(3)
    public void getFreightModelSummary() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/shops/1/freightmodels/200").header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504}";
        System.out.println(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 测试获取模板概要功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void getFreightModelSummary1() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/shops/1/freightmodels/9").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 测试获取模板概要功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getFreightModelSummary2() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/shops/1/freightmodels/13").header("authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse,responseString, false);
    }


    /**
     * 测试获取运费模板功能
     * 全部获取
     *
     * @throws Exception
     */
    @Test
    @Order(6)
    public void getFreightModels() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/shops/1/freightmodels").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":10,\"total\":6,\"pages\":1,\"list\":[{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":10,\"name\":\"测试模板2\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":11,\"name\":\"测试模板3\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":22,\"name\":\"ight model/100g\",\"type\":0,\"unit\":100,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":23,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    /**
     * 测试获取运费模板功能
     * 按名字获取
     *
     * @throws Exception
     */
    @Test
    @Order(7)
    public void getFreightModels1() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/shops/1/freightmodels?name=测试模板4").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":10,\"total\":1,\"pages\":1,\"list\":[{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试获取运费模板功能
     * 指定页大小
     *
     * @throws Exception
     */
    @Test
    @Order(8)
    public void getFreightModels2() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/shops/1/freightmodels?pageSize=2").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":2,\"total\":6,\"pages\":3,\"list\":[{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":10,\"name\":\"测试模板2\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试获取运费模板功能
     * 指定页大小和页数
     *
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getFreightModels3() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/shops/1/freightmodels?pageSize=2&page=2").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":2,\"pageSize\":2,\"total\":6,\"pages\":3,\"list\":[{\"id\":11,\"name\":\"测试模板3\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";
        JSONAssert.assertEquals(expectedResponse,responseString, true);
    }

    /**
     * 测试克隆模板功能
     * 资源不存在
     *
     * @throws Exception
     */
    @Test
    @Order(10)
    public void cloneFreightModel() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/shops/1/freightmodels/200/clone").header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 测试克隆模板功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    @Order(11)
    public void cloneFreightModel1() throws Exception {

                String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/shops/1/freightmodels/9/clone").header("authorization", token))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse,responseString, false);

        String temp = new String(responseString);
        int startIndex = temp.indexOf("id");
        int endIndex = temp.indexOf("name");
        String id = temp.substring(startIndex + 4, endIndex - 2);

        String queryResponseString = this.mvc.perform(get("/shops/1/freightmodels/"+ id).header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(new String(queryResponseString), new String(responseString), true);
    }

    /**
     * 测试克隆模板功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    @Order(12)
    public void cloneFreightModel2() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/shops/1/freightmodels/13/clone").header("authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString), false);
    }

    /**
     * 测试定义默认模板功能
     * 操作资源不存在
     *
     * @throws Exception
     */
    @Test
    @Order(13)
    public void defineDefaultFreightModel() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/shops/1/freight_models/200/default").header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString), false);
    }

    /**
     * 测试定义默认模板功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    @Order(14)
    public void defineDefaultFreightModel1() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/shops/1/freight_models/22/default").header("authorization", token))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString), true);


        String queryResponseString = this.mvc.perform(get("/shops/1/freightmodels/22").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        JSONAssert.assertEquals(expectedResponse, new String(queryResponseString), false);
    }

    /**
     * 测试定义默认模板功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    @Order(15)
    public void defineDefaultFreightModel2() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/shops/1/freight_models/13/default").header("authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString), false);

    }


    /**
     * 测试定义模板功能
     *
     * @throws Exception
     */
    @Test
    @Order(16)
    public void defineFreightModel() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String json = "{\"name\":\"测试名\",\"type\":0,\"unit\":500}";
        String responseString = this.mvc.perform(post("/shops/1/freightmodels").header("authorization", token).contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString), false);

        String temp = new String(responseString);
        int startIndex = temp.indexOf("id");
        int endIndex = temp.indexOf("name");
        String id = temp.substring(startIndex + 4, endIndex - 2);
        String queryResponseString = this.mvc.perform(get("/shops/1/freightmodels/"+id).header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();



        JSONAssert.assertEquals(new String(queryResponseString), new String(responseString), true);
    }
   

}