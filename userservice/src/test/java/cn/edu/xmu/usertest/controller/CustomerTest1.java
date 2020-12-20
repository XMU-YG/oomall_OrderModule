package cn.edu.xmu.usertest.controller;


import cn.edu.xmu.user.UserServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
public class CustomerTest1 {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAllStatesTest1() throws Exception{
        String token = "login";
        String responseString = this.mvc.perform(get("/user/users/states").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void registerTest1() throws Exception{
        String token = "login";
        String requireJson="{\"userName\":\"123\",\"password\":\"123456\",\"real_name\":\"12334\",\"gender\":\"0\",\"birthday\":\"2020-01-11\"" +
                ",\"email\":\"123@qq.com\",\"mobile\":\"123234\"}";
        ResultActions responseString = this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
    }

    @Test
    public void getUserSelfTest1() throws Exception{
//        String token = "login";
//        String requireJson="{\"\"}";
//        String responseString = this.mvc.perform(get("/user/users").header("authorization",token))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void modifyUserInfoTest1() throws Exception{
        String token = "login";
        String json="{\"realname\":\"123\",\"gender\":\"0\",\"birthday\":\"2020-11-11\"}";
        String responseString = this.mvc.perform(put("/user/users").contentType("application/json;charset=UTF-8").content(json))
//                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }
}
