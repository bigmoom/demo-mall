package com.cwh.mall.service.impl;

import com.cwh.mall.bo.UserDetail;
import com.cwh.mall.dao.UmsAdminRoleResourceMapper;
import com.cwh.mall.mbg.model.UmsResource;
import com.cwh.mall.service.UmsAdminService;
import com.cwh.mall.mbg.mapper.UmsAdminMapper;
import com.cwh.mall.mbg.model.UmsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cwh
 * @date 2021/7/13 11:04
 */
@Service
public class UmsAdminServiceImpl implements UmsAdminService {

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private UmsAdminRoleResourceMapper umsAdminRoleResourceMapper;

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdmin umsAdmin = umsAdminMapper.selectByUsername(username);
        if(umsAdmin == null){
            throw new UsernameNotFoundException("用户没有找到");
        }
        return umsAdmin;
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        //获取用户
        UmsAdmin umsAdmin = this.getAdminByUsername(username);
        //获取用户所拥有的权限
        Set<SimpleGrantedAuthority> authorities = getResourceList(umsAdmin.getId()).stream()
                .map(role -> new SimpleGrantedAuthority(role.getId()+":"+role.getName()))
                .collect(Collectors.toSet());


        //返回UserDetails实现对象即可
        return new UserDetail(umsAdmin,authorities);

    }

    /**
     * 获取用户对应角色权限所拥有的资源
     * @param adminId
     * @return
     */
    @Cacheable(cacheNames = "resourceList", key = "#adminId")
    @Override
    public List<UmsResource> getResourceList(Long adminId) {

        List<UmsResource> resourceList = umsAdminRoleResourceMapper.getResourceList(adminId);

        return resourceList;
    }
}
