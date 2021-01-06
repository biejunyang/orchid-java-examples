package com.orchid.miaosha.config;

import com.alibaba.fastjson.JSON;
import com.orchid.core.Exception.BaseException;
import com.orchid.core.po.Result;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;



@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(0, new MappingJackson2HttpMessageConverter());
//    }

    /**
     * rest请求统一数据格式输出
     */
    @RestControllerAdvice
    static class ResultResponseAdvice implements ResponseBodyAdvice<Object> {

        @Override
        public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
            return true;
        }


        @Override
        public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
            if(body instanceof Result){
                return body;
            }
            Result result=Result.success(body);
            if(body instanceof String){
                return JSON.toJSONString(result);
            }
            return result;
        }




        @ExceptionHandler
        public Result<Object> handleException(Exception ex){
            if(ex instanceof BaseException){
                BaseException baseException=((BaseException)ex);
                return Result.error(baseException.getCode(), baseException.getMessage());
            }else if(ex instanceof BindException){
                BindException bindException=((BindException)ex);
                String msg=bindException.getAllErrors().get(0).getDefaultMessage();
                return Result.error(msg);
            }else{
                ex.printStackTrace();
                return Result.error();
            }

        }

    }





}
