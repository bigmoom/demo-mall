package com.cwh.mall.common.domain.bo;

import lombok.Getter;

/**
 * @author cwh
 * @date 2021/7/7 17:49
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),

    FAILED(500, "操作失败"),

    VALIDATE_FAILED(404, "参数检验失败"),

    UNAUTHORIZED(401, "暂未登录或token已经过期"),

    FORBIDDEN(403, "没有相关权限");

    /**
     * 响应码
     */
    private long code;
    /**
     * 响应消息
     */
    private String message;

    /**
     * 自定义响应
     * @param code
     * @param message
     */
    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }
}

