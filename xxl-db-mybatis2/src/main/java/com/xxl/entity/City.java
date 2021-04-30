package com.xxl.entity;

import java.io.Serializable;

public class City  implements Serializable {
    private static final long serialVersionUID = 3295294785869492617L;


    /**

     CREATE TABLE `xxl_jdbc`.`city` (
     `id` INT NOT NULL AUTO_INCREMENT,
     `province_id` INT NULL,
     `city_name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL,
     `description` VARCHAR(45) NULL,
     PRIMARY KEY (`id`),
     UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);


     INSERT INTO xxl_jdbc.city (id, province_id, city_name, description) VALUES (2, 2, 'cd', 'hello xxl2');
     *
     */

    /**
     * 城市编号
     */
    private Long id;

    /**
     * 省份编号
     */
    private Long provinceId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 描述
     */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
