package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.UmsMenu;

public interface UmsMenuMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UmsMenu record);

    int insertSelective(UmsMenu record);

    UmsMenu selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UmsMenu record);

    int updateByPrimaryKey(UmsMenu record);
}