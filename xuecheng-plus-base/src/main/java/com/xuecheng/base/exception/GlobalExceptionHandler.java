package com.xuecheng.base.exception;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理自定义异常
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e) {
        log.error("系统异常{},{}", e, e.getErrMessage());

        return new RestErrorResponse(e.getErrMessage());
    }

    /**
     * 处理系统异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("系统异常{}", e.getMessage(), e);

        return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
    }


    /**
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        List<String> errMessage = new ArrayList<>();
        errors.stream().forEach(item ->{
            errMessage.add(item.getDefaultMessage());
        });
        //拼接错误信息
        String join = StringUtils.join(errMessage, ",");

        log.error("系统异常{}", join);

        return new RestErrorResponse(join);
    }



}
