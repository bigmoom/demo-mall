package com.cwh.mall.service;

import com.cwh.mall.mbg.model.UmsAdmin;
import com.cwh.mall.mbg.model.UmsResource;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 后台用户管理Service
 *
 * @author cwh
 * @date 2021/7/13 11:00
 */
public interface UmsAdminService {

    /**
     * 通过用户名获取用户
     * @param username
     * @return
     */
    UmsAdmin getAdminByUsername(String username);

    /**
     * 用以实现UserDetailService
     * @param username
     * @return
     */
    UserDetails loadUserByUsername(String username);

    List<UmsResource> getResourceList(Long adminId);
}
