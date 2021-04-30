package com.xxl.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "To show user information")
public class UserVO {

    @ApiModelProperty(notes = "user name")
    private String username;

    @ApiModelProperty(notes = "user's display name")
    private String displayName;

    @Override
    public String toString() {
        return "UserVO{"
                + "username='" + username + '\''
                + ", displayName='" + displayName + '\''
                + '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
