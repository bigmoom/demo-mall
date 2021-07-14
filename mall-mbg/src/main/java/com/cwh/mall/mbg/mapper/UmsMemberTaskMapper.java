package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.UmsMemberTask;

public interface UmsMemberTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberTask record);

    int insertSelective(UmsMemberTask record);

    UmsMemberTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UmsMemberTask record);

    int updateByPrimaryKey(UmsMemberTask record);
}