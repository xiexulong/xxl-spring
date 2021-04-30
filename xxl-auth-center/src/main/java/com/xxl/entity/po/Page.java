package com.xxl.entity.po;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "pages")
public class Page implements java.io.Serializable {

    private static final long serialVersionUID = 7177259705045233786L;

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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "page")
    @OrderBy("sequence ASC")
    private Set<UrlGroup> urlGroups = new HashSet<>(0);

    public Page() {
    }

    public Page(String name, Set<UrlGroup> urlGroups) {
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
        return this.name;
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
        return this.urlGroups;
    }

    public void setUrlGroups(Set<UrlGroup> urlGroups) {
        this.urlGroups = urlGroups;
    }
}
