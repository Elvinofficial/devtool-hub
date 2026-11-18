package com.devtoolshub.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "development_tools")
public class DevelopmentTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ToolCategory category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "version", length = 50)
    private String version;

    protected  DevelopmentTool() {}

    public DevelopmentTool(String name, ToolCategory category)
    {
        this.name = name;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ToolCategory getCategory() {
        return category;
    }

    public void setCategory(ToolCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
