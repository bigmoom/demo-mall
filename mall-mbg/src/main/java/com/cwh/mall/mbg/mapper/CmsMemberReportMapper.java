package com.cwh.mall.mbg.mapper;

import com.cwh.mall.mbg.model.CmsMemberReport;

public interface CmsMemberReportMapper {
    int insert(CmsMemberReport record);

    int insertSelective(CmsMemberReport record);
}