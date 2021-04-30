package com.xxl.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel(description = "The user's information needed to add for adding a user")
public class AddUserVO {

    @ApiModelProperty(notes = "user name")
    @Size(min = 1,max = 50)
//    @Pattern(regexp = "^[0-9a-zA-Z_]{1,}$", message = "User Name is not formatted correctly")
    String username;

    @ApiModelProperty(notes = "user display name")
    @Size(min = 1,max = 50)
    @Pattern(regexp = "^[0-9a-zA-Z_]{1,}$", message = "DisplayName is not formatted correctly")
    String displayName;

    @ApiModelProperty(notes = "user password")
    @NotBlank(message = "passWord is empty")
    String password;

    @ApiModelProperty(notes = "user role")
    @NotEmpty(message = "There is not any roles for a user.")
    List<String> roles;

    @ApiModelProperty(notes = "user email address")
    @Pattern(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)"
            + "|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])"
            + "|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$|",
        message = "Email is not formatted correctly")
    String email;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;

    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
