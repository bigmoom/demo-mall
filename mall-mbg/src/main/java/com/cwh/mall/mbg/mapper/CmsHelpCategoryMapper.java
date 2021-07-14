package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.CmsHelpCategory;

public interface CmsHelpCategoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsHelpCategory record);

    int insertSelective(CmsHelpCategory record);

    CmsHelpCategory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsHelpCategory record);

    int updateByPrimaryKey(CmsHelpCategory record);
}