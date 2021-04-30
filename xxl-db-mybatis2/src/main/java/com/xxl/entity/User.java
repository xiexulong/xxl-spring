package com.xxl.entity;


import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -1724273397574256042L;
    /**

     CREATE TABLE `xxl_jdbc2`.`user` (
     `id` INT NOT NULL AUTO_INCREMENT,
     `user_name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL,
     `description` VARCHAR(45) NULL,
     PRIMARY KEY (`id`),
     UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);


     INSERT INTO xxl_jdbc2.user (id, user_name, description) VALUES (2, "xxl", 'hello xxl2');
     *
     */

    private Long id;


    private String userName;

    /**
     * 描述
     */
    private String description;

    private City city;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
