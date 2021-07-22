package com.cwh.mall.dto;

import com.cwh.mall.mbg.model.UmsMenu;
import com.cwh.mall.mbg.model.UmsRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cwh
 * @date 2021/7/22 11:23
 */
@Data
@ApiModel(value = "用户登录信息")
public class UmsAdminInfo implements Serializable {
    /**
     * 用户姓名
     */
    @ApiModelProperty("用户姓名")
    private String username;

    /**
     * 用户对应可操作性菜单
     */
    @ApiModelProperty("用户可操作菜单列表")
    private List<UmsMenu> menus;

    /**
     * 用户头像
     */
    @ApiModelProperty("用户图像图标地址")
    private String icon;

    /**
     * 用户所拥有的权限
     */
    @ApiModelProperty("用户拥有权限名称列表")
    private List<String> resources;

}
