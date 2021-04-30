package com.xxl.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel(description = "Modified user's detail information")
public class ModifyUserVO {

    @ApiModelProperty(notes = "user's display name")
    @Size(min = 1,max = 50)
    @Pattern(regexp = "^[0-9a-zA-Z_]{1,}$", message = "DisplayName is not formatted correctly")
    private String displayName;

    @ApiModelProperty(notes = "user's email address")
    @Pattern(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)"
            + "|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])"
            + "|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$|",
            message = "Email is not formatted correctly")
    private String email;

    @ApiModelProperty(notes = "to identity if the user is enabled")
    private String enabled;

    @ApiModelProperty(notes = "user's roles")
    @NotEmpty(message = "There is not any roles for a user.")
    private List<String> roles;

    @Override
    public String toString() {
        return "ModifyUserVO{" + "displayName='" + displayName + '\''
                + ", email='" + email + '\'' + ", enabled=" + enabled
                + ", roles=" + roles + '}';
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
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



    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
