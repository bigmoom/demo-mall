package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.UmsResource;

import java.util.List;

public interface UmsResourceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UmsResource record);

    int insertSelective(UmsResource record);

    UmsResource selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UmsResource record);

    int updateByPrimaryKey(UmsResource record);

    List<UmsResource> selectAll();
}