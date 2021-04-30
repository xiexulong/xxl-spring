package com.xxl.entity.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "urls")
public class Url implements java.io.Serializable {

    private static final long serialVersionUID = -5940775853827981341L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "url_pattern", length = 1000)
    private String urlPattern;

    @Column(name = "http_method", length = 6)
    private String httpMethod;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "urlgroups_urls_mapping", joinColumns = {
            @JoinColumn(name = "urlgroup_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "url_id", nullable = false, updatable = false)})
    private Set<UrlGroup> urlGroups = new HashSet<>(0);

    public Url() {
    }

    public Url(String urlPattern, String httpMethod, Set<UrlGroup> urlGroups) {
        this.urlPattern = urlPattern;
        this.httpMethod = httpMethod;
        this.urlGroups = urlGroups;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlPattern() {
        return this.urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Set<UrlGroup> getUrlGroups() {
        return this.urlGroups;
    }

    public void setUrlGroups(Set<UrlGroup> urlGroups) {
        this.urlGroups = urlGroups;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Url url = (Url) o;
        return Objects.equals(id, url.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

