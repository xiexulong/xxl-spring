package com.xxl.repository;

import com.xxl.entity.po.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorityTypeRepository extends JpaRepository<Role, Long> {
    /**
     * find role.
     * @param role find Role by names.
     * @return return roles.
     */
    Role findByRole(String role);

}
