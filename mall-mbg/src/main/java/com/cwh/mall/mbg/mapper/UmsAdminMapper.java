package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.UmsAdmin;
import com.cwh.mall.mbg.model.UmsMenu;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UmsAdminMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UmsAdmin record);

    int insertSelective(UmsAdmin record);

    UmsAdmin selectByPrimaryKey(Long id);

    UmsAdmin selectByUsername(String name);

    int updateByPrimaryKeySelective(UmsAdmin record);

    int updateByPrimaryKey(UmsAdmin record);

    int updateLoginTime(@Param("id")Long id, @Param("loginTime")Date loginTime);

    List<UmsMenu> getUmsAdminMenu(Long id);

    List<UmsAdmin> selectByLikeName(String name);
}