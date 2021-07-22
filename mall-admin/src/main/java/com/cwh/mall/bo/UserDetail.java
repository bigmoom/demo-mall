package com.cwh.mall.bo;

import com.cwh.mall.mbg.model.UmsAdmin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 继承springsecurity提供的封装类
 * 作为authentication的用户对象
 * @author cwh
 * @date 2021/7/13 10:43
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class UserDetail extends User {

    private UmsAdmin umsAdmin;


    public UserDetail(UmsAdmin umsAdmin, Collection<? extends GrantedAuthority> authorities) {
        super(umsAdmin.getUsername(), umsAdmin.getPassword(), authorities);
        this.umsAdmin = umsAdmin;
    }
}
