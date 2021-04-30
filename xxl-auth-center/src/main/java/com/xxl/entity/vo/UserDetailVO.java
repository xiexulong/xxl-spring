package com.xxl.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

@ApiModel(description = "To show a user's detail information")
public class UserDetailVO {

    @ApiModelProperty(notes = "user id")
    private Long userId;

    @ApiModelProperty(notes = "user name")
    private String username;

    @ApiModelProperty(notes = "to identity if the user is enabled")
    private Boolean enabled;

    @ApiModelProperty(notes = "user's display name")
    private String displayName;

    @ApiModelProperty(notes = "user's email address")
    private String email;

    @ApiModelProperty(notes = "added time")
    private Date addedTime;

    @ApiModelProperty(notes = "user's roles")
    private List<String> roles;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Date addedTime) {
        this.addedTime = addedTime;
    }
}
