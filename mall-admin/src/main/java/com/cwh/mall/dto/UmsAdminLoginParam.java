package com.cwh.mall.dto;

import com.cwh.mall.common.validation.annotation.PasswordValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author cwh
 * @date 2021/7/15 15:20
 */
@ApiModel("用户登录参数")
@Data
public class UmsAdminLoginParam {
    @NotEmpty
    @ApiModelProperty(value = "用户名",required = true)
    private String username;

    @PasswordValidator
    @ApiModelProperty(value = "密码",required = true)
    private String password;
}
