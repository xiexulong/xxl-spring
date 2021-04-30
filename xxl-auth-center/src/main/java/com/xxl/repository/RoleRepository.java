package com.xxl.repository;

import com.xxl.entity.po.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT MAX(a.sequence) FROM Role a")
    Integer findMaxSequence();

    @Query(value = "select a from Role a where a.id = ?1")
    Role findRoleById(long id);
}
