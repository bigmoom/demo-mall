package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.CmsTopic;

public interface CmsTopicMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsTopic record);

    int insertSelective(CmsTopic record);

    CmsTopic selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsTopic record);

    int updateByPrimaryKeyWithBLOBs(CmsTopic record);

    int updateByPrimaryKey(CmsTopic record);
}