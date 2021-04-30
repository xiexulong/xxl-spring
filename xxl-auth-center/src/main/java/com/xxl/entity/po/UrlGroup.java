package com.xxl.entity.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "urlgroups")
public class UrlGroup implements java.io.Serializable {

    private static final long serialVersionUID = -6252430015980388971L;

    /**
     * id.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * uid.
     */
    @Column(name = "uid", unique = true, nullable = false)
    private Long uid;

    /**
     * page.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;

    /**
     * portal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portal_id")
    private Portal portal;

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
     * api permissions.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "urlgroups_urls_mapping", joinColumns = {
            @JoinColumn(name = "urlgroup_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "url_id", nullable = false, updatable = false)})
    private Set<Url> urls = new HashSet<>(0);


    public UrlGroup() {
    }

    /**
     * constructor.
     * @param id id.
     * @param uid uid.
     * @param page page.
     * @param portal portal.
     * @param name name.
     * @param sequence sequence.
     * @param urls api permissions.
     */
    public UrlGroup(Long id, Long uid, Page page, Portal portal,
                    String name, Integer sequence,
                    Set<Url> urls) {
        this.id = id;
        this.uid = uid;
        this.page = page;
        this.portal = portal;
        this.name = name;
        this.sequence = sequence;
        this.urls = urls;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Page getPages() {
        return this.page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Portal getPortal() {
        return this.portal;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
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

    public Set<Url> getUrls() {
        return this.urls;
    }

    public void setUrls(Set<Url> urls) {
        this.urls = urls;
    }
}
