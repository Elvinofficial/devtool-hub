package com.devtoolshub.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "software_projects")
public class SoftwareProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_programming_languages",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "programming_language_id")
    )
    private Set<ProgrammingLanguage> programmingLanguages = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_developers",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id")
    )
    private  Set<Developer> developers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_operating_systems",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "operating_system_id")
    )
    private  Set<OperatingSystem> operatingSystems = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_development_tools",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "development_tool_id")
    )
    private   Set<DevelopmentTool> developmentTools = new HashSet<>();

    protected SoftwareProject() {
    }

    public SoftwareProject(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ProgrammingLanguage> getProgrammingLanguages(){
        return  this.programmingLanguages;
    }

    public void addProgrammingLanguage(ProgrammingLanguage programmingLanguage){
        this.programmingLanguages.add(programmingLanguage);
    }

    public Set<Developer> getDevelopers(){
        return  this.developers;
    }

    public void addDeveloper(Developer developer)
    {
        this.developers.add(developer);
    }

    public Set<OperatingSystem> getOperatingSystems(){
        return  this.operatingSystems;
    }

    public void addOperatingSystem(OperatingSystem operatingSystem){
        this.operatingSystems.add(operatingSystem);
    }

    public Set<DevelopmentTool> getDevelopmentTools(){
        return   this.developmentTools;
    }

    public  void addDevelopmentTool(DevelopmentTool developmentTool){
        this.developmentTools.add(developmentTool);
    }
}
