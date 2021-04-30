package com.xxl.entity.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "portals")
public class Portal implements java.io.Serializable {
    private static final long serialVersionUID = -8439950764672009883L;

    /**
     * id.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * name.
     */
    @Column(name = "name", length = 100)
    private String name;

    /**
     * sequence.
     */
    @Column(name = "sequence")
    private Integer sequence;

    /**
     * permission groups.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "portal")
    @OrderBy("sequence ASC")
    private Set<UrlGroup> urlGroups = new HashSet<>(0);


    public Portal() {
    }

    public Portal(String name, Set<UrlGroup> urlGroups) {
        this.name = name;
        this.urlGroups = urlGroups;

    }


    public Long getId() {
        return this.id;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Set<UrlGroup> getUrlGroups() {
        return urlGroups;
    }

    public void setUrlGroups(Set<UrlGroup> urlGroups) {
        this.urlGroups = urlGroups;
    }
}

