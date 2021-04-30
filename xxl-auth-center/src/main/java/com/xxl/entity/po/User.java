package com.xxl.entity.po;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 5751782241635793179L;

    //user id.
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //user name.
    @Column(name = "username")
    private String username;

    //user password.
    @Column(name = "password")
    private String password;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    //user state.
    @Column(name = "enabled")
    private Boolean enabled;

    //user display name.
    @Column(name = "display_name")
    private String displayName;

    //user email.
    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    private List<Role> roles;

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

    //user add time.
    @Column(name = "added_time")

    private Date addedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + id + ", username='" + username + '\''
                + ", password='" + password + '\''
                + ", enabled=" + enabled
                + ", displayName='" + displayName + '\''
                + ", email='" + email + '\''
                + ", roles=" + roles
                + ", addedTime=" + addedTime
                + '}';
    }
}
