package com.xxl.entity.po;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role implements java.io.Serializable {

    private static final long serialVersionUID = 7177259705045233719L;

    /**
     * id.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "authorities",
            joinColumns = {@JoinColumn(name = "authority", referencedColumnName = "role")},
            inverseJoinColumns = {@JoinColumn(name = "username", referencedColumnName = "username")})
    private List<User> users;

    /**
     * role.
     */
    @Column(name = "role", length = 100)
    private String role;

    @Column(name = "removed")
    private boolean removed;

    /**
     * sequence.
     */
    @Column(name = "sequence")
    private Integer sequence;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

}