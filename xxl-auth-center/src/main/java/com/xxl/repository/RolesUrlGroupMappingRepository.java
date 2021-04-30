package com.xxl.repository;

import com.xxl.entity.po.RolesUrlGroupsMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RolesUrlGroupMappingRepository extends JpaRepository<RolesUrlGroupsMapping, Long> {

    @Query(value = "select a from RolesUrlGroupsMapping a where a.urlgroupid = ?1")
    List<RolesUrlGroupsMapping> findByUrlGroupId(long urlgroupid);

    List<RolesUrlGroupsMapping> findByRoleId(Integer roleId);
}
