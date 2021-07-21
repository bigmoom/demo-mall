package com.cwh.mall.controller;

import com.alibaba.fastjson.JSONObject;
import com.cwh.mall.dto.UmsAdminLoginParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author cwh
 * @date 2021/7/21 9:39
 */
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
class UmsAdminControllerTest {

    @Autowired
    private UmsAdminController umsAdminController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        System.out.println("测试方法执行之前");
        System.out.println("controller"+umsAdminController);
        System.out.println("testRestTemplate"+mockMvc);
    }

    @Test
    public void testLogin() throws Exception{
        UmsAdminLoginParam umsAdminLoginParam = new UmsAdminLoginParam();
        umsAdminLoginParam.setUsername("cwh");
        umsAdminLoginParam.setPassword("123456");
        MvcResult mvcResult = this.mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(umsAdminLoginParam)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());

    }
}