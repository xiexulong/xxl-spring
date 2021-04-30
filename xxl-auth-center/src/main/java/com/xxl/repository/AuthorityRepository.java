
package com.xxl.repository;

import com.xxl.entity.po.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    @Query(value = "select a from Authority a where a.user.username = ?1")
    List<Authority> findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "delete from Authority a where username = ?1")
    void deleteByUsername(String username);
}
