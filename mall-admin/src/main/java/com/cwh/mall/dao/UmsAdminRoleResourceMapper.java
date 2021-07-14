package com.cwh.mall.dao;

import com.cwh.mall.mbg.model.UmsResource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author cwh
 * @date 2021/7/14 10:06
 */
@Mapper
public interface UmsAdminRoleResourceMapper {

    List<UmsResource> getResourceList(Long adminId);
}
