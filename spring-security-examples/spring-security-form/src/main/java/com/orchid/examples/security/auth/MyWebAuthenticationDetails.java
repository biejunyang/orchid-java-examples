package com.orchid.examples.security.auth;

import lombok.Data;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;


@Data
public class MyWebAuthenticationDetails extends WebAuthenticationDetails {

    private String code;

    public MyWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.code=request.getParameter("code");
    }
}
