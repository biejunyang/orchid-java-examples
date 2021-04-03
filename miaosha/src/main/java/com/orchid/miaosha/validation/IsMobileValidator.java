package com.orchid.miaosha.validation;

import cn.hutool.core.util.StrUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean required=false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        this.required=constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            //mobile validate
        }else{
            if(StrUtil.isEmpty(s)){
                return true;
            }else{
                // valiteion
            }
        }
        return false;
    }
}
