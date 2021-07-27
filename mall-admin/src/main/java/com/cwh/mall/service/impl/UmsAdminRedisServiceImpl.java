package com.cwh.mall.service.impl;

import com.cwh.mall.common.service.RedisService;
import com.cwh.mall.mbg.mapper.UmsAdminMapper;
import com.cwh.mall.mbg.model.UmsAdmin;
import com.cwh.mall.mbg.model.UmsResource;
import com.cwh.mall.service.UmsAdminRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author cwh
 * @date 2021/7/26 17:29
 */
@Service
public class UmsAdminRedisServiceImpl implements UmsAdminRedisService {

    private static final String ADMIN_PREFIX = "UmsAdminCache";

    private static final String ID = "AdminId";

    private static final String RESOURCE_PREFIX = "UmsResourceCache";

    @Autowired
    private RedisService redisService;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    private String generateAdminKey(Long adminId){
        StringJoiner stringJoiner = new StringJoiner(":");
        stringJoiner.add(ADMIN_PREFIX);
        stringJoiner.add(ID);
        stringJoiner.add(adminId.toString());

        return stringJoiner.toString();
    }


    @Override
    public void setAdmin(Long adminId) {

        UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(adminId);
        String key = generateAdminKey(adminId);
        redisService.set(key, umsAdmin);
    }

    @Override
    public void setAdmin(String username) {

        UmsAdmin umsAdmin = umsAdminMapper.selectByUsername(username);
        setAdmin(umsAdmin.getId());

    }

    @Override
    public void setAdmin(UmsAdmin umsAdmin) {

        setAdmin(umsAdmin.getId());
    }

    @Override
    public UmsAdmin getAdmin(Long adminId) {

        String key = generateAdminKey(adminId);

        return (UmsAdmin)redisService.get(key);
    }

    @Override
    public UmsAdmin getAdmin(String username) {

        UmsAdmin umsAdmin = umsAdminMapper.selectByUsername(username);

        return getAdmin(umsAdmin.getId());
    }

    @Override
    public void delAdmin(Long adminId) {

        String key = generateAdminKey(adminId);
        redisService.del(key);
    }

    @Override
    public void delAdmin(String username) {


    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        return null;
    }

    @Override
    public void setResourceList(Long adminId, List<UmsResource> resourceList) {

    }

    @Override
    public void delResourceList(Long adminId) {

    }

    @Override
    public void delResourceListByRole(Long roleId) {

    }

    @Override
    public void delResourceListByResource(Long resourceId) {

    }
}
