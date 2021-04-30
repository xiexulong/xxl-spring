package com.xxl.entity.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "roles_urlgroups_mapping")
public class RolesUrlGroupsMapping implements java.io.Serializable {

    private static final long serialVersionUID = 7177299708045233719L;

    /**
     * id.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * urlgroup_id.
     */
    @Column(name = "urlgroup_id")
    private Long urlgroupid;


    /**
     * role_id.
     */
    @Column(name = "role_id")
    private Integer roleId;

    /**
     * granting.
     */
    @Column(name = "granting")
    private Boolean granting;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrlgroupid() {
        return urlgroupid;
    }

    public void setUrlgroupid(Long urlgroupid) {
        this.urlgroupid = urlgroupid;
    }

    public Integer getRoleid() {
        return roleId;
    }

    public void setRoleid(Integer roleid) {
        this.roleId = roleid;
    }

    public Boolean getGranting() {
        return granting;
    }

    public void setGranting(Boolean granting) {
        this.granting = granting;
    }

    @Override
    public String toString() {
        return "RolesUrlGroupsMapping{"
                + "urlgroupid=" + urlgroupid
                + ", roleid=" + roleId
                + ", granting=" + granting
                + '}';
    }
}
