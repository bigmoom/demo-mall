package com.cwh.mall.service;

import com.cwh.mall.mbg.model.UmsAdmin;
import com.cwh.mall.mbg.model.UmsResource;

import java.util.List;

/**
 * @author cwh
 * @date 2021/7/26 17:28
 */
public interface UmsAdminRedisService {

    void setAdmin(Long adminId);

    void setAdmin(String username);

    void setAdmin(UmsAdmin umsAdmin);

    UmsAdmin getAdmin(Long adminId);

    UmsAdmin getAdmin(String username);

    void delAdmin(Long adminId);

    void delAdmin(String username);

    List<UmsResource> getResourceList(Long adminId);

    void setResourceList(Long adminId, List<UmsResource> resourceList);

    /**
     * 删除后台用户资源列表缓存
     */
    void delResourceList(Long adminId);

    /**
     * 当角色相关资源信息改变时删除相关后台用户缓存
     */
    void delResourceListByRole(Long roleId);

    /**
     * 当资源信息改变时，删除资源项目后台用户缓存
     */
    void delResourceListByResource(Long resourceId);

}
