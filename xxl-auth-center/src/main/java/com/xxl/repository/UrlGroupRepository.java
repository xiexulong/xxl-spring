package com.xxl.repository;

import com.xxl.entity.po.UrlGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UrlGroupRepository extends JpaRepository<UrlGroup, Long>,JpaSpecificationExecutor<UrlGroup> {

}
