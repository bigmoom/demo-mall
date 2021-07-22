package com.cwh.mall.common.domain.vo;

import com.cwh.mall.common.domain.bo.ResultCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 统一返回视图类
 *
 * @author cwh
 * @date 2021/7/7 17:49
 */
@Data
@ApiModel(value = "返回视图对象")
public class ResultVO<T> {
    /**
     * 状态码
     */
    @ApiModelProperty(value = "返回状态码")
    private Long code;
    /**
     * 响应信息，说明响应情况
     */
    @ApiModelProperty(value = "返回信息")
    private String message;
    /**
     * 数据封装
     */
    @ApiModelProperty(value = "返回数据对象")
    private T data;


    public ResultVO(T data){
        this(ResultCode.SUCCESS,data);
    }

    public ResultVO(ResultCode resultCode, T data){
        this(resultCode.getCode(),resultCode.getMessage(),data);
    }

    public ResultVO(Long resultCode, String message, T data){
        this.code = resultCode;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString(){
        return String.format("{\"code\":%d,\"msg\":\"%s\",\"data\":\"%s\"}",code,message,data);
    }
}
