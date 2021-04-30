package com.xxl.service;


import com.xxl.entity.po.Authority;
import com.xxl.entity.po.UrlGroup;

import java.util.List;


public interface AuthorityService {

    List<UrlGroup> findAllUrlGroups();

    List<Authority> findRolesByUsername(String username);
}
