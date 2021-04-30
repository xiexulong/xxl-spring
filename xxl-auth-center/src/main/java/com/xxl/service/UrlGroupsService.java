package com.xxl.service;

import com.xxl.entity.po.UrlGroup;

import java.util.List;

public interface UrlGroupsService {

    /**
     * Find all UrlGroup' instance in database.
     * @return the list of {@ink UrlGroup}.
     */
    List<UrlGroup> findAll();
}
