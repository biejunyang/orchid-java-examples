package com.orchid.miaosha.config;

import cn.hutool.core.util.StrUtil;
import com.orchid.miaosha.entity.User;
import com.orchid.miaosha.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    AuthService authService;
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz=methodParameter.getParameterType();
        return clazz== User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request=nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response=nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String token=request.getParameter("token");
        if(StrUtil.isEmpty(token) && request.getCookies()!=null){
            for (Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("token")){
                    token=cookie.getValue();
                    break;
                }
            }
        }
        return authService.getUserByToken(token, response);
    }
}
