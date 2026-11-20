
package com.devtoolshub.service;

import com.devtoolshub.entity.SoftwareProject;
import com.devtoolshub.repository.SoftwareProjectRepository;
import com.devtoolshub.util.NameNormalizer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devtoolshub.entity.ProgrammingLanguage;
import com.devtoolshub.repository.ProgrammingLanguageRepository;
import com.devtoolshub.entity.OperatingSystem;
import com.devtoolshub.entity.DevelopmentTool;
import com.devtoolshub.entity.Developer;

import com.devtoolshub.repository.OperatingSystemRepository;
import com.devtoolshub.repository.DevelopmentToolRepository;
import com.devtoolshub.repository.DeveloperRepository;

import java.util.Set;
import java.util.HashSet;

import java.util.List;

@Service
public class SoftwareProjectService {

    private final SoftwareProjectRepository repository;
    private final ProgrammingLanguageRepository languageRepository;
    private final OperatingSystemRepository operatingSystemRepository;
    private final DevelopmentToolRepository toolRepository;
    private final DeveloperRepository developerRepository;

    public SoftwareProjectService(
            SoftwareProjectRepository repository,
            ProgrammingLanguageRepository languageRepository,
            OperatingSystemRepository operatingSystemRepository,
            DevelopmentToolRepository toolRepository,
            DeveloperRepository developerRepository
    ) {
        this.repository = repository;
        this.languageRepository = languageRepository;
        this.operatingSystemRepository = operatingSystemRepository;
        this.toolRepository = toolRepository;
        this.developerRepository = developerRepository;
    }

    // Возвращает список программных проектов
    @Transactional(readOnly = true)
    public List<SoftwareProject> findAll() {
        return repository.findAll();
    }

    // Возвращает проект по идентификатору
    @Transactional(readOnly = true)
    public SoftwareProject findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Проект не найден"
                ));
    }

    // Создаёт новый программный проект
    @Transactional
    public SoftwareProject create(
            String name,
            String description
    ) {
        String normalizedName =
                NameNormalizer.normalize(name, 200);

        SoftwareProject project =
                new SoftwareProject(normalizedName);

        project.setDescription(
                normalizeDescription(description)
        );

        return repository.save(project);
    }

    // Обновляет название и описание проекта
    @Transactional
    public SoftwareProject update(
            Long id,
            String name,
            String description
    ) {
        SoftwareProject project = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Проект не найден"
                ));

        String normalizedName =
                NameNormalizer.normalize(name, 200);

        project.setName(normalizedName);
        project.setDescription(
                normalizeDescription(description)
        );

        return repository.save(project);
    }

    // Удаляет программный проект
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Проект не найден"
            );
        }

        repository.deleteById(id);
    }

    // Возвращает языки программирования проекта
    @Transactional(readOnly = true)
    public Set<ProgrammingLanguage> getProgrammingLanguages(Long projectId) {
        SoftwareProject project = findById(projectId);

        return new HashSet<>(project.getProgrammingLanguages());
    }

    // Привязывает язык программирования к проекту
    @Transactional
    public void addProgrammingLanguage(
            Long projectId,
            Long languageId
    ) {
        SoftwareProject project = findById(projectId);

        ProgrammingLanguage language = languageRepository.findById(languageId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Язык программирования не найден"
                ));

        project.addProgrammingLanguage(language);
    }

    // Отвязывает язык программирования от проекта
    @Transactional
    public void removeProgrammingLanguage(
            Long projectId,
            Long languageId
    ) {
        SoftwareProject project = findById(projectId);

        boolean removed = project.getProgrammingLanguages()
                .removeIf(language -> language.getId().equals(languageId));

        if (!removed) {
            throw new IllegalArgumentException(
                    "Язык программирования не привязан к проекту"
            );
        }
    }


    // Возвращает операционные системы проекта
    @Transactional(readOnly = true)
    public Set<OperatingSystem> getOperatingSystems(Long projectId) {
        SoftwareProject project = findById(projectId);

        return new HashSet<>(project.getOperatingSystems());
    }

    // Привязывает операционную систему к проекту
    @Transactional
    public void addOperatingSystem(Long projectId, Long osId) {
        SoftwareProject project = findById(projectId);

        OperatingSystem operatingSystem =
                operatingSystemRepository.findById(osId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Операционная система не найдена"
                        ));

        project.addOperatingSystem(operatingSystem);
    }

    // Отвязывает операционную систему от проекта
    @Transactional
    public void removeOperatingSystem(Long projectId, Long osId) {
        SoftwareProject project = findById(projectId);

        boolean removed = project.getOperatingSystems()
                .removeIf(os -> os.getId().equals(osId));

        if (!removed) {
            throw new IllegalArgumentException(
                    "Операционная система не привязана к проекту"
            );
        }
    }


    // Возвращает инструменты разработки проекта
    @Transactional(readOnly = true)
    public Set<DevelopmentTool> getDevelopmentTools(Long projectId) {
        SoftwareProject project = findById(projectId);

        return new HashSet<>(project.getDevelopmentTools());
    }

    // Привязывает инструмент разработки к проекту
    @Transactional
    public void addDevelopmentTool(Long projectId, Long toolId) {
        SoftwareProject project = findById(projectId);

        DevelopmentTool tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Инструмент разработки не найден"
                ));

        project.addDevelopmentTool(tool);
    }

    // Отвязывает инструмент разработки от проекта
    @Transactional
    public void removeDevelopmentTool(Long projectId, Long toolId) {
        SoftwareProject project = findById(projectId);

        boolean removed = project.getDevelopmentTools()
                .removeIf(tool -> tool.getId().equals(toolId));

        if (!removed) {
            throw new IllegalArgumentException(
                    "Инструмент разработки не привязан к проекту"
            );
        }
    }


    // Возвращает разработчиков проекта
    @Transactional(readOnly = true)
    public Set<Developer> getDevelopers(Long projectId) {
        SoftwareProject project = findById(projectId);

        return new HashSet<>(project.getDevelopers());
    }

    // Привязывает разработчика к проекту
    @Transactional
    public void addDeveloper(Long projectId, Long developerId) {
        SoftwareProject project = findById(projectId);

        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Разработчик не найден"
                ));

        project.addDeveloper(developer);
    }

    // Отвязывает разработчика от проекта
    @Transactional
    public void removeDeveloper(Long projectId, Long developerId) {
        SoftwareProject project = findById(projectId);

        boolean removed = project.getDevelopers()
                .removeIf(developer -> developer.getId().equals(developerId));

        if (!removed) {
            throw new IllegalArgumentException(
                    "Разработчик не привязан к проекту"
            );
        }
    }


    // Нормализует необязательное описание проекта
    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}
