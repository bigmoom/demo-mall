package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.CmsPrefrenceArea;

public interface CmsPrefrenceAreaMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsPrefrenceArea record);

    int insertSelective(CmsPrefrenceArea record);

    CmsPrefrenceArea selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsPrefrenceArea record);

    int updateByPrimaryKeyWithBLOBs(CmsPrefrenceArea record);

    int updateByPrimaryKey(CmsPrefrenceArea record);
}