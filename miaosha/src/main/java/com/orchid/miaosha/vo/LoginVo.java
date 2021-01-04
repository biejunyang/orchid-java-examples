package com.orchid.miaosha.vo;

import com.orchid.miaosha.validation.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginVo {
    @NotBlank
    @Length(max = 50)
    private String username;

    @NotBlank
    @Length(max = 50)
    private String password;

    @IsMobile(required = false)
    private String mobilePhone;
}
