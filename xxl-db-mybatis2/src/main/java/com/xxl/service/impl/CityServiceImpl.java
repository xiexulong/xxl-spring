package com.xxl.service.impl;

import com.xxl.dao.cluster.CityDao;
import com.xxl.entity.City;
import com.xxl.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityDao cityDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public City findCityById(Long id) {
        String key = "city_" + id;
        ValueOperations<String, City> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            return operations.get(key);
        }

        City city = cityDao.findById(id);
        operations.set(key,city,10, TimeUnit.SECONDS);
        return city;
    }

    @Override
    public Long saveCity(City city) {
        return cityDao.saveCity(city);
    }

    @Override
    public Long updateCity(City city) {
        Long ret = cityDao.updateCity(city);
        //如果缓存存在则删除缓存
        String key = "city_" + city.getId();
        if(redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        return ret;
    }

    @Override
    public Long deleteCity(Long id) {
        Long ret = cityDao.deleteCity(id);
        //如果缓存存在则删除缓存
        String key = "city_" + id;
        if(redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        return ret;
    }
}
