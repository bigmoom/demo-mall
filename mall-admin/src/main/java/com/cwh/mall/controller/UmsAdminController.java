package com.cwh.mall.controller;

import com.cwh.mall.common.domain.bo.ResultCode;
import com.cwh.mall.common.domain.vo.ResultVO;
import com.cwh.mall.dto.UmsAdminLoginParam;
import com.cwh.mall.dto.UmsAdminParam;
import com.cwh.mall.mbg.model.UmsAdmin;
import com.cwh.mall.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @Value("${security.jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private UmsAdminService umsAdminService;

    /**
     * 注册操作
     * @param umsAdminParam 包含username,password
     * @return
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public ResultVO<UmsAdmin> register(@RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = umsAdminService.register(umsAdminParam);

        return new ResultVO<>(umsAdmin);
    }

    /**
     * 根据用户id获取用户信息
     * @param id
     * @return
     */
    @ApiOperation("通过用户id获取用户信息")
    @GetMapping("/{id}")
    public ResultVO<UmsAdmin> getAdminById(@PathVariable Long id){
        log.info("get admin success");
        UmsAdmin umsAdmin = umsAdminService.getAdminById(id);
        return new ResultVO<>(umsAdmin);
    }

    /**
     * 用户登录，成功后返回token
     * @param umsAdminLoginParam
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResultVO login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam){
        String token = umsAdminService.login(umsAdminLoginParam);
        //token为null即登录失败
        if(token == null){
            return new ResultVO(ResultCode.VALIDATE_FAILED.getCode(),"账号密码错误",null);
        }

        Map<String,String> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        tokenMap.put("tokenHead",tokenHead);
        return new ResultVO<>(tokenMap);
    }


    /**
     * 更新用户数据
     * @param umsAdmin 用户信息
     * @return
     */
    @ApiOperation("更新用户数据")
    @PostMapping("/update/{id}")
    public ResultVO update(@PathVariable Long id ,@Validated @RequestBody UmsAdmin umsAdmin){
        //设置用户id
        umsAdmin.setId(id);
        //修改成功
        if(umsAdminService.updateUmsAdmin(umsAdmin)){
            return new ResultVO(ResultCode.SUCCESS.getCode(),"更新用户信息成功",umsAdmin);
        }else {
            //修改失败
            return new ResultVO(ResultCode.FAILED.getCode(),"更新失败",null);
        }
    }


    /**
     * 根据用户id删除用户
     * @param id
     * @return
     */
    @ApiOperation("根据id删除用户")
    @DeleteMapping("/delete/{id}")
    public ResultVO deleteUmsAdmin(@PathVariable Long id){
        if(umsAdminService.deleteUmsAdmin(id)){
            return new ResultVO(ResultCode.SUCCESS.getCode(),"删除成功",null);
        }else {
            return new ResultVO(ResultCode.FAILED,null);
        }
    }

}
