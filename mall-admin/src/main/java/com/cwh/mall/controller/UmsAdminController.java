package com.cwh.mall.controller;

import com.cwh.mall.common.domain.vo.ResultVO;
import com.cwh.mall.mbg.model.UmsAdmin;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台用户管理接口
 * @author cwh
 * @date 2021/7/14 16:38
 */
@RestController
@RequestMapping("/admin")
@Api()
public class UmsAdminController {


    @GetMapping("/register")
    public ResultVO<UmsAdmin> register(){

    }
}
