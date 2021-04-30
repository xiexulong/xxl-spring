package com.xxl.service.impl;

import com.xxl.entity.po.Authority;
import com.xxl.entity.po.UrlGroup;
import com.xxl.repository.AuthorityRepository;
import com.xxl.repository.UrlGroupRepository;
import com.xxl.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private UrlGroupRepository urlGroupRepository;

    @Autowired
    private AuthorityRepository authorityRepository;
    


    @Override
    public List<UrlGroup> findAllUrlGroups() {
        return urlGroupRepository.findAll();
    }

    @Override
    public List<Authority> findRolesByUsername(String username) {
        return authorityRepository.findByUsername(username);
    }
}
