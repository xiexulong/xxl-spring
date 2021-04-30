package com.xxl.service.impl;

import java.util.List;

import com.xxl.entity.po.UrlGroup;
import com.xxl.repository.UrlGroupRepository;
import com.xxl.service.UrlGroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UrlGroupsServiceImpl implements UrlGroupsService {

    @Autowired
    private UrlGroupRepository urlGroupRepository;
    
    public UrlGroupsServiceImpl(UrlGroupRepository urlGroupRepository) {
        this.urlGroupRepository = urlGroupRepository;
    }

    @Override
    public List<UrlGroup> findAll() {
        return urlGroupRepository.findAll();
    }
}
