package cn.edu.xmu.freight.controllerTest;

import cn.edu.xmu.freight.FreightServiceApplication;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.encript.AES;
import org.json.JSONException;
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

   

}