package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.UmsMemberRuleSetting;

public interface UmsMemberRuleSettingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberRuleSetting record);

    int insertSelective(UmsMemberRuleSetting record);

    UmsMemberRuleSetting selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UmsMemberRuleSetting record);

    int updateByPrimaryKey(UmsMemberRuleSetting record);
}