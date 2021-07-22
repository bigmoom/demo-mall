package com.cwh.mall.controller;

import com.cwh.mall.common.domain.bo.ResultCode;
import com.cwh.mall.common.domain.vo.ResultVO;
import com.cwh.mall.dto.UmsAdminInfo;
import com.cwh.mall.dto.UmsAdminLoginParam;
import com.cwh.mall.dto.UmsAdminParam;
import com.cwh.mall.mbg.model.UmsAdmin;
import com.cwh.mall.mbg.model.UmsMenu;
import com.cwh.mall.mbg.model.UmsResource;
import com.cwh.mall.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.awt.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @ApiOperation(value = "用户登录", notes = "返回token，且tokenHeader = Bear")
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
    @ApiOperation(value = "更新用户数据", notes = "更新成功返回更新后的umsAdmin，失败返回Null")
    @PostMapping("/update/{id}")
    public ResultVO updateUmsAdmin(@PathVariable Long id ,@Validated @RequestBody UmsAdmin umsAdmin){
        //设置用户id
        umsAdmin.setId(id);
        //修改成功
        if(umsAdminService.updateUmsAdmin(umsAdmin)!=null){
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
    @ApiOperation(value = "根据id删除用户")
    @DeleteMapping("/delete/{id}")
    public ResultVO deleteUmsAdmin(@PathVariable Long id){
        //if(umsAdminService.deleteUmsAdmin(id)){
        //    return new ResultVO(ResultCode.SUCCESS.getCode(),"删除成功",null);
        //}else {
        //    return new ResultVO(ResultCode.FAILED,null);
        //}
        UmsAdmin umsAdmin = umsAdminService.getAdminById(id);
        if(umsAdminService.deleteUmsAdmin(umsAdmin)){
            return new ResultVO(ResultCode.SUCCESS.getCode(),"删除成功",null);
        }else {
            return new ResultVO(ResultCode.FAILED.getCode(), "删除失败", null);
        }
    }


    /**
     * 根据principal获取当前登录对象
     * 并返回用户姓名，可操作性菜单列表，头像，权限列表
     * @param principal
     * @return
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public ResultVO getCurrentUmsAdminInfo(Principal principal) throws AuthenticationException {
        //判断当前是否登录
        if(principal == null){
            throw new DisabledException("未登录状态");
        }else {
            UmsAdminInfo umsAdminInfo = new UmsAdminInfo();
            //获取当前登录用户姓名
            String username = principal.getName();
            umsAdminInfo.setUsername(username);
            //获取当前登录用户头像地址
            UmsAdmin umsAdmin = umsAdminService.getAdminByUsername(username);
            umsAdminInfo.setIcon(umsAdmin.getIcon());
            //获取当前登录用户拥有权限名称列表
            List<UmsResource> umsResourceList = umsAdminService.getResourceList(umsAdmin.getId());
            //判断该用户拥有资源
            if(!umsResourceList.isEmpty()){
                List<String> umsResourceNameList = umsResourceList.stream().map(umsResource -> umsResource.getName()).collect(Collectors.toList());
                umsAdminInfo.setResources(umsResourceNameList);
            }else {
                umsAdminInfo.setResources(null);
            }
            //获取用户可操作菜单列表
            List<UmsMenu> umsMenuList = umsAdminService.getUmsAdminMenu(umsAdmin.getId());
            umsAdminInfo.setMenus(umsMenuList);

            return new ResultVO(ResultCode.SUCCESS.getCode(),"获取信息成功",umsAdminInfo);
        }


    }

    /**
     * 通过字段查询姓名或者用户名类似的用户
     * 并且分页表示
     * @param keyword 查询字段
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 返回页列表对象
     */
    @ApiOperation(value = "根据姓名或者用户名获取分页列表",notes = "根据提供的word查询姓名或者用户名类似的用户")
    @GetMapping("/list")
    public ResultVO getUmsAdminListByNameLike(@RequestParam("keyword")String keyword,
                                              @RequestParam("pageNum")Integer pageNum,
                                              @RequestParam("pageSize")Integer pageSize){

        List<UmsAdmin> umsAdminLikeList = umsAdminService.getUmsAdminByNameLike(keyword, pageNum, pageSize);
        return new ResultVO(ResultCode.SUCCESS,umsAdminLikeList);
    }
}
