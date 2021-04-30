package com.xxl.service;


import com.xxl.entity.po.RolesUrlGroupsMapping;

import java.util.List;

public interface RolesUrlGroupsMappingService {
    List<RolesUrlGroupsMapping> findRolesByUrlGroupId(long urlgroupid);

    List<RolesUrlGroupsMapping> findAll();

    List<RolesUrlGroupsMapping> findByRoleId(Long roleId);

    /**
     * Save the specified RolesUrlGroupsMapping.
     * @param rolesUrlGroupsMapping {@link RolesUrlGroupsMapping}.
     * @return RolesUrlGroupsMapping.
     */
    RolesUrlGroupsMapping saveAndFlush(RolesUrlGroupsMapping rolesUrlGroupsMapping);
}
