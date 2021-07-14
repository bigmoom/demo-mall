package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.PmsProductAttributeValue;

public interface PmsProductAttributeValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsProductAttributeValue record);

    int insertSelective(PmsProductAttributeValue record);

    PmsProductAttributeValue selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsProductAttributeValue record);

    int updateByPrimaryKey(PmsProductAttributeValue record);
}