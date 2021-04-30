package com.xxl.domain;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 参考官网：https://mybatis.org/mybatis-3/zh/java-api.html
 */
@Mapper
public interface StudentMapper {

    @Select("SELECT * FROM t_student WHERE NAME = #{name}")
    Student findByName(@Param("name") String name);

    @Insert("INSERT INTO t_student(NAME, AGE) VALUES(#{name}, #{age})")
    int insert(@Param("name") String name, @Param("age") Integer age);

    @Update("UPDATE t_student SET age=#{age} WHERE name=#{name}")
    void update(Student user);

    @Delete("DELETE FROM t_student WHERE id =#{id}")
    void delete(Long id);

    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "age", column = "age")
    })
    @Select("SELECT name, age FROM t_student")
    List<Student> findAll();
}