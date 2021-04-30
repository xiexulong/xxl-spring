package com.xxl.dao.cluster;

import com.xxl.entity.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * 有两种方式： 这种使用的是注解，还有一种使用映射文件
 */
@Mapper //标志为mybatis的mapper
public interface CityDao2 {


    @Select("select * from city")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "provinceId", column = "province_id"),
            @Result(property = "cityName", column = "city_name"),
            @Result(property = "description", column = "description"),
    })
    City findByName(@Param("cityName") String cityName);


}
