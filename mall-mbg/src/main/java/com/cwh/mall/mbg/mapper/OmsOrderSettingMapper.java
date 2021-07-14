package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.OmsOrderSetting;

public interface OmsOrderSettingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderSetting record);

    int insertSelective(OmsOrderSetting record);

    OmsOrderSetting selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OmsOrderSetting record);

    int updateByPrimaryKey(OmsOrderSetting record);
}