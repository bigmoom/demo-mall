package com.cwh.mall.service;

import com.cwh.mall.dto.UmsAdminLoginParam;
import com.cwh.mall.dto.UmsAdminParam;
import com.cwh.mall.mbg.model.UmsAdmin;
import com.cwh.mall.mbg.model.UmsMenu;
import com.cwh.mall.mbg.model.UmsResource;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

/**
 * 后台用户管理Service
 *
 * @author cwh
 * @date 2021/7/13 11:00
 */
public interface UmsAdminService {

    UmsAdmin getAdminByUsername(String username);

    UserDetails loadUserByUsername(String username);

    List<UmsResource> getResourceList(Long adminId);

    UmsAdmin register(UmsAdminParam umsAdminParam);

    UmsAdmin getAdminById(Long id);

    Map<String,Object> login(UmsAdminLoginParam umsAdminLoginParam);

    Map<String,Object> refreshToken(String refreshToken);

    UmsAdmin updateUmsAdmin(UmsAdmin umsAdmin);

    Boolean deleteUmsAdmin(UmsAdmin umsAdmin);

    List<UmsMenu> getUmsAdminMenu(Long id);

    List<UmsAdmin> getUmsAdminByNameLike(String name, Integer pageNum, Integer pageSize);

    void logout(String username);

    Boolean updateUmsAdminRole(Long umsAdminId, Long umsRoleId);
}
