package com.cwh.mall.common.validation;

import com.cwh.mall.common.validation.annotation.PasswordValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义密码校验器
 * @author cwh
 * @date 2021/7/21 14:17
 */
public class PasswordValidatorClass implements ConstraintValidator<PasswordValidator,String> {
    /**
     * 密码长度最小值和最大值
     */
    private int min ;
    private int max ;

    /**
     * 初始化
     * @param passwordValidator 注解上设置的值
     */
    @Override
    public void initialize(PasswordValidator passwordValidator) {
            this.min = passwordValidator.min();
            this.max = passwordValidator.max();
    }

    /**
     * 校验
     * 校验规则：
     * 长度必须在min和max之间
     * 必须仅有数字字母构成
     * 必须同时拥有大小写
     * @param value 被校验的值
     * @param context 校验上下文
     * @return 返回值，true标明校验通过，false标明不通过
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //统计多少个数字
        int countNumber = 0;
        //统计多少个大写字母
        int countUpperCase = 0;
        //统计多少个小写字母
        int countLowerCase = 0;
        //长度校验
        if(value.length()<min || value.length()>max ){
            return false;
        }
        //password的char数组，用以检查是否合规
        char[] password = value.toCharArray();
        for(char letter : password){
            if (!Character.isLetter(letter)) {
                //既不是字母也不是数字，非法
                if (!Character.isDigit(letter)) {
                    return false;
                }else{
                    countNumber ++ ;
                }
            }else{
                if(Character.isUpperCase(letter)){
                    countUpperCase ++ ;
                }else {
                    countLowerCase ++ ;
                }
            }
        }
        //不满足有数字和大小写字母构成
        if(countNumber == 0 || countUpperCase == 0 || countLowerCase == 0){
            return false;
        }
            return true;
    }
}
