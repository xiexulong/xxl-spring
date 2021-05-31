
package com.xxl.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


@ApiModel
public class ConfigListVO {

    private List<Configuration> configs;

    public List<Configuration> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Configuration> configs) {
        this.configs = configs;
    }

    public static class Configuration {
        @ApiModelProperty(notes = "config id")
        private long id;
        @ApiModelProperty(notes = "config application")
        private String application;
        @ApiModelProperty(notes = "config profile")
        private String profile;
        @ApiModelProperty(notes = "config label")
        private String label;
        @ApiModelProperty(notes = "config item")
        private String item;
        @ApiModelProperty(notes = "config value")
        private String value;
        @ApiModelProperty(notes = "config remark")
        private String remark;
        @ApiModelProperty(notes = "config type default 0, 0-no refresh 1-refresh 2-read only")
        private int type;
        @ApiModelProperty(notes = "favorite field")
        private Integer favorite;

        public Configuration() {
        }

        public Configuration(long id, String application, String profile, String label, String item,
                             String value, String remark, int type, Integer favorite) {
            this.id = id;
            this.application = application;
            this.profile = profile;
            this.label = label;
            this.item = item;
            this.value = value;
            this.remark = remark;
            this.type = type;
            this.favorite = favorite;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Integer getFavorite() {
            return favorite;
        }

        public void setFavorite(Integer favorite) {
            this.favorite = favorite;
        }
    }
}
