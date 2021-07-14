package com.cwh.mall.common.domain.vo;

import com.cwh.mall.common.domain.bo.ResultCode;
import lombok.Data;

/**
 * 统一返回视图类
 *
 * @author cwh
 * @date 2021/7/7 17:49
 */
@Data
public class ResultVO<T> {
    /**
     * 状态码
     */
    private long code;
    /**
     * 响应信息，说明响应情况
     */
    private String message;
    /**
     * 数据封装
     */
    private T data;


    public ResultVO(T data){
        this(ResultCode.SUCCESS,data);
    }

    public ResultVO(ResultCode resultCode, T data){
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    @Override
    public String toString(){
        return String.format("{\"code\":%d,\"msg\":\"%s\",\"data\":\"%s\"}",code,message,data);
    }
}
