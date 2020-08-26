package com.yinlz.controller;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局的系统异常处理json数据格式提示
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2019-09-12 13:39
 * @QQ号码 444141300
 * @Email service@yinlz.com
 * @官网 http://www.yinlz.com
*/
@ControllerAdvice
public class ResponseException{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void exceptionHandler(final Exception exception,final HttpServletResponse response){
        logger.error(exception.getMessage());
        logger.error(exception.getClass()+"");
        if(exception instanceof MissingServletRequestPartException){
            ToolClient.responseJson(ToolClient.exceptionJson("请选择文件再操作"),response);
            return;
        }
        if(exception instanceof MultipartException){
            ToolClient.responseJson(ToolClient.exceptionJson("请选择上传文件"),response);
            return;
        }
        if(exception instanceof MaxUploadSizeExceededException || exception instanceof FileUploadBase.SizeLimitExceededException){
            ToolClient.responseJson(ToolClient.exceptionJson("上传的文件过大"),response);
            return;
        }
        if(exception instanceof HttpRequestMethodNotSupportedException){
            ToolClient.responseJson(ToolClient.exceptionJson("不支持该请求方式"),response);
            return;
        }
        ToolClient.responseException(response);
    }
}