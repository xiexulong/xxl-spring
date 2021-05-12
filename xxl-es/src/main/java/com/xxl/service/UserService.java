package com.xxl.service;

import com.xxl.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    long count();

    User save(User commodity);

    void delete(User commodity);

    List<User> getAll();

    List<User> getByName(String name);

    Page<User> pageQuery(Integer pageNo, Integer pageSize, String kw);
}
