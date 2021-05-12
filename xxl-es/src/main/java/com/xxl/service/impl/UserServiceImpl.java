package com.xxl.service.impl;

import com.xxl.dao.UserRepository;
import com.xxl.entity.User;
import com.xxl.service.UserService;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        userRepository.findAll().forEach(e->list.add(e));
        return list;
    }

    @Override
    public List<User> getByName(String name) {

//        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name", name);
//        Iterable<User> iterable = userRepository.search(matchQueryBuilder);
        List<User> list = new ArrayList<>();
//        iterable.forEach(e->list.add(e));
        return list;
    }

    @Override
    public Page<User> pageQuery(Integer pageNo, Integer pageSize, String kw) {
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhraseQuery("name", kw))
                .withPageable(pageRequest)
                .build();
        return null;//userRepository.search(searchQuery);
    }
}
