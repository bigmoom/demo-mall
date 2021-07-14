package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.CmsTopicCategory;

public interface CmsTopicCategoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsTopicCategory record);

    int insertSelective(CmsTopicCategory record);

    CmsTopicCategory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsTopicCategory record);

    int updateByPrimaryKey(CmsTopicCategory record);
}