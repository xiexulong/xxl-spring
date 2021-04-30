package com.xxl.service.impl;

import com.xxl.entity.po.Role;
import com.xxl.entity.po.RolesUrlGroupsMapping;
import com.xxl.entity.po.UrlGroup;
import com.xxl.entity.po.User;
import com.xxl.exception.RoleAlreadyAssignedException;
import com.xxl.exception.RoleNotFoundException;
import com.xxl.exception.UrlGroupNotFoundException;
import com.xxl.repository.RoleRepository;
import com.xxl.service.RolesService;
import com.xxl.service.RolesUrlGroupsMappingService;
import com.xxl.service.UrlGroupsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class RolesServiceImpl implements RolesService {

    private static final Logger logger = LoggerFactory.getLogger(RolesServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UrlGroupsService urlGroupsService;

    @Autowired
    private RolesUrlGroupsMappingService rolesUrlGroupsMappingService;


    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    /**
     * add new role.
     * @param roleName role name.
     * @return Role.
     */
    @Override
    @Transactional
    public Role addNewRole(String roleName) {
        Integer sequence = roleRepository.findMaxSequence();
        Role roles = new Role();
        roles.setSequence(sequence + 1);
        roles.setRole(roleName);

        Role role;

        role = roleRepository.save(roles);
        return role;
    }

    /**
     * update role.
     * @param id for role id.
     * @param name name.
     */
    @Override
    @Transactional
    public void updateRole(Long id, String name) {

        Role role = roleRepository.findOne(id);

        if (null == role) {
            logger.error("No role find for role id: {}", id);
            throw new RoleNotFoundException("No role find to update.");
        }

        role.setRole(name);
        roleRepository.saveAndFlush(role);


    }

    @Override
    public void enableUrlGroups(Long id, List<Long> enableList, List<Long> disableList) {

        // Find the Role for specified role id. If the role dose not
        // exist, then a RuntimeException will be thrown.
        Role role = roleRepository.findOne(id);
        if (null == role) {
            logger.error("No role find for role id: {}", id);
            throw new RoleNotFoundException("Specified role dose not exist, role id:"  + id);
        }

        // Query all UrlGroup, it contains the relations between group 'id' and  group 'uid'.
        List<UrlGroup> urlGroups = urlGroupsService.findAll();

        // Query all RolesUrlGroupsMapping, it contains the relations among group 'id' 'role_id'
        // and 'granting'.
        List<RolesUrlGroupsMapping> rolesUrlGroupsMappings = rolesUrlGroupsMappingService.findAll();

        updateUrlGroups(role, enableList, true, urlGroups, rolesUrlGroupsMappings);
        updateUrlGroups(role, disableList, false, urlGroups, rolesUrlGroupsMappings);
    }

    private void updateUrlGroups(Role role, List<Long> urlGroupUIds, boolean enable,
                                 List<UrlGroup> urlGroups, List<RolesUrlGroupsMapping> rolesUrlGroupsMappings) {

        if (null == urlGroupUIds || urlGroupUIds.isEmpty()) {
            return;
        }

        // Find the {@link UrlGroup} due to 'uid' in the enableList. If no one find,
        // a RuntimeException will be thrown.
        for (Long urlGroupUId: urlGroupUIds) {
            UrlGroup urlGroup2Find = null;
            for (UrlGroup urlGroup: urlGroups) {
                if (urlGroupUId.longValue() == urlGroup.getUid().longValue()) {
                    urlGroup2Find = urlGroup;
                }
            }

            if (null == urlGroup2Find) {
                logger.error("No UrlGroup find for group uid: {}", urlGroupUId);
                throw new UrlGroupNotFoundException("No UrlGroup find for group uid: " + urlGroupUId);
            }

            Long groupId = urlGroup2Find.getId();

            RolesUrlGroupsMapping rolesUrlGroupsMapping2Find = null;
            for (RolesUrlGroupsMapping rolesUrlGroupsMapping: rolesUrlGroupsMappings) {
                if (rolesUrlGroupsMapping.getUrlgroupid().longValue() == groupId.longValue()
                        && rolesUrlGroupsMapping.getRoleid().longValue() == role.getId().longValue()) {
                    rolesUrlGroupsMapping2Find = rolesUrlGroupsMapping;
                }
            }

            // Do granting.
            if (enable) {
                if (null == rolesUrlGroupsMapping2Find) {
                    logger.info("No RolesUrlGroupsMapping find for role : {}, group id: {}",
                            role.getRole(), groupId);
                    RolesUrlGroupsMapping rolesUrlGroupsMapping = new RolesUrlGroupsMapping();
                    rolesUrlGroupsMapping.setGranting(true);
                    rolesUrlGroupsMapping.setUrlgroupid(groupId);
                    rolesUrlGroupsMapping.setRoleid(Integer.parseInt(role.getId().toString()));
                    logger.info("Insert new RolesUrlGroupsMapping, {}", rolesUrlGroupsMapping);
                    rolesUrlGroupsMappingService.saveAndFlush(rolesUrlGroupsMapping);
                } else {
                    rolesUrlGroupsMapping2Find.setGranting(true);
                    logger.info("Update RolesUrlGroupsMapping, {}", rolesUrlGroupsMapping2Find);
                    rolesUrlGroupsMappingService.saveAndFlush(rolesUrlGroupsMapping2Find);
                }
            } else {
                if (null == rolesUrlGroupsMapping2Find) {
                    // do nothing.
                } else {
                    rolesUrlGroupsMapping2Find.setGranting(false);
                    logger.info("Update RolesUrlGroupsMapping, {}", rolesUrlGroupsMapping2Find);
                    rolesUrlGroupsMappingService.saveAndFlush(rolesUrlGroupsMapping2Find);
                }
            }
        }
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findOne(id);

        if (null == role) {
            logger.error("No role find for role id: {}", id);
            throw new RoleNotFoundException("No role find to delete.");
        }

        List<User> userList = role.getUsers();
        if (null != userList && !userList.isEmpty()) {
            for (User user: userList) {
                if (user.getEnabled()) {
                    logger.info("The role is used by {}, can not delete.", user.getUsername());
                    throw new RoleAlreadyAssignedException("Role has been assigned to users!");

                }
            }
        }

        role.setRemoved(true);
        String backupRoleName = role.getRole() + "_" + UUID.randomUUID();
        role.setRole(backupRoleName);
        roleRepository.saveAndFlush(role);
    }

    @Override
    public Role findRoleById(long id) {
        return roleRepository.findRoleById(id);
    }
}
