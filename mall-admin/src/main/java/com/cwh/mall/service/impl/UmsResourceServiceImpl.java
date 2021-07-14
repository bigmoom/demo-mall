package com.cwh.mall.service.impl;

import com.cwh.mall.mbg.mapper.UmsResourceMapper;
import com.cwh.mall.mbg.model.UmsResource;
import com.cwh.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cwh
 * @date 2021/7/13 16:39
 */
@Service
public class UmsResourceServiceImpl implements UmsResourceService {

    @Autowired
    private UmsResourceMapper umsResourceMapper;

    @Override
    public List<UmsResource> listAll() {
        return umsResourceMapper.selectAll();
    }
}
