package com.cwh.mall.controller;

import com.cwh.mall.common.domain.vo.ResultVO;
import com.cwh.mall.dto.UmsAdminParam;
import com.cwh.mall.mbg.model.UmsAdmin;
import com.cwh.mall.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 后台用户管理接口
 * @author cwh
 * @date 2021/7/14 16:38
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "后台用户管理")
@Slf4j
public class UmsAdminController {

    @Autowired
    private UmsAdminService umsAdminService;

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public ResultVO<UmsAdmin> register(@RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = umsAdminService.register(umsAdminParam);

        return new ResultVO<>(umsAdmin);
    }

    @ApiOperation("通过用户id获取用户信息")
    @GetMapping("/{id}")
    public ResultVO<UmsAdmin> getAdminById(@PathVariable Long id){
        log.info("get admin success");
        UmsAdmin umsAdmin = umsAdminService.getAdminById(id);
        return new ResultVO<>(umsAdmin);
    }

    /**
     * 用户登录，成功后返回token
     * @param umsAdminParam
     * @return
     */
    //@ApiOperation("用户登录")
    //@GetMapping("/login")
    //public ResultVO login(@RequestBody UmsAdminParam umsAdminParam){
    //
    //}


}
