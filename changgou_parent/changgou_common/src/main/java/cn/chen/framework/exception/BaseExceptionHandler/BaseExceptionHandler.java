package cn.chen.framework.exception.BaseExceptionHandler;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 公共异常处理类
 * @author haixin
 * @time 2019-10-29
 * */
@ControllerAdvice
public class BaseExceptionHandler {
    /***
     * 异常处理
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}