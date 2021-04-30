package com.xxl.service;


import com.xxl.entity.po.Role;

import javax.transaction.Transactional;
import java.util.List;

public interface RolesService {
    List<Role> findAll();

    Role addNewRole(String roleName);

    void updateRole(Long id, String name);

    /**
     * To enable and disable UrlGroup for specified role id.
     * @param id for role id.
     * @param enableList the list of UrlGroup 'uid'.
     * @param disableList the list of UrlGroup 'uid'.
     */
    @Transactional
    void enableUrlGroups(Long id, List<Long> enableList, List<Long> disableList);

    /**
     * To remove the specified role.
     * @param id the role id.
     */
    void deleteRole(Long id);

    Role findRoleById(long id);
}
