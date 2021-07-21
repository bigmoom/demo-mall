package com.cwh.mall.common.validation.annotation;

import com.cwh.mall.common.validation.PasswordValidatorClass;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cwh
 * @date 2021/7/21 14:18
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidatorClass.class)
public @interface PasswordValidator {

    int min() default 4;

    int max() default 10;

    String message() default "密码格式错误，请重新输入密码";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
