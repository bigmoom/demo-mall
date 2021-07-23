package com.cwh.mall.common.exception;

import com.cwh.mall.common.domain.bo.ResultCode;
import com.cwh.mall.common.domain.vo.ResultVO;
import io.lettuce.core.RedisConnectionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 统一异常处理
 * @author cwh
 * @date 2021/7/21 15:08
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验异常处理
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultVO handleValidException(MethodArgumentNotValidException exception){

        String message = null;
        BindingResult bindingResult = exception.getBindingResult();
        if(bindingResult.hasErrors()){
            FieldError fieldError = bindingResult.getFieldError();
            if(fieldError != null){
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }

        return new ResultVO(ResultCode.VALIDATE_FAILED,message);
    }


    @ResponseBody
    @ExceptionHandler(value = RedisConnectionException.class)
    public ResultVO handleRedisConnectException(RedisConnectionException redisConnectionException){
        return new ResultVO(ResultCode.FAILED, "redis连接失败");
    }


    @ResponseBody
    @ExceptionHandler(value = JedisConnectionException.class)
    public ResultVO handleJedisConnectException(JedisConnectionException jedisConnectionException){
        return new ResultVO(ResultCode.FAILED, "redis连接失败");
    }

}
