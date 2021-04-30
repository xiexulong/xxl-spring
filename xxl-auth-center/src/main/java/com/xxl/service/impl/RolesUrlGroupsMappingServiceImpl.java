package com.xxl.service.impl;

import java.util.List;

import com.xxl.entity.po.RolesUrlGroupsMapping;
import com.xxl.repository.RolesUrlGroupMappingRepository;
import com.xxl.service.RolesUrlGroupsMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RolesUrlGroupsMappingServiceImpl implements RolesUrlGroupsMappingService {

    @Autowired
    private RolesUrlGroupMappingRepository rolesUrlGroupMappingRepository;
    

    @Override
    public List<RolesUrlGroupsMapping> findRolesByUrlGroupId(long urlgroupid) {
        return rolesUrlGroupMappingRepository.findByUrlGroupId(urlgroupid);
    }

    @Override
    public List<RolesUrlGroupsMapping> findAll() {
        return rolesUrlGroupMappingRepository.findAll();
    }

    @Override
    public List<RolesUrlGroupsMapping> findByRoleId(Long roleId) {
        return rolesUrlGroupMappingRepository.findByRoleId(roleId.intValue());
    }

    @Override
    public RolesUrlGroupsMapping saveAndFlush(RolesUrlGroupsMapping rolesUrlGroupsMapping) {
        return rolesUrlGroupMappingRepository.saveAndFlush(rolesUrlGroupsMapping);
    }
}
