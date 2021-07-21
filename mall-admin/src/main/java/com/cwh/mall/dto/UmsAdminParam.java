package com.cwh.mall.dto;

import com.cwh.mall.common.validation.annotation.PasswordValidator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cwh
 * @date 2021/7/15 9:39
 */
@Data
public class UmsAdminParam {
    //@NotEmpty
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @PasswordValidator
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "用户头像")
    private String icon;
    //@Email
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "备注")
    private String note;
}
