
package com.devtoolshub.service;

import com.devtoolshub.dto.DashboardStats;
import com.devtoolshub.dto.ToolPopularity;

import com.devtoolshub.repository.SoftwareProjectRepository;
import com.devtoolshub.repository.DeveloperRepository;
import com.devtoolshub.repository.DevelopmentToolRepository;
import com.devtoolshub.repository.ProgrammingLanguageRepository;
import com.devtoolshub.repository.OperatingSystemRepository;
import com.devtoolshub.repository.ToolCategoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final SoftwareProjectRepository projectRepository;
    private final DeveloperRepository developerRepository;
    private final DevelopmentToolRepository toolRepository;
    private final ProgrammingLanguageRepository languageRepository;
    private final OperatingSystemRepository operatingSystemRepository;
    private final ToolCategoryRepository categoryRepository;

    public DashboardService(
            SoftwareProjectRepository projectRepository,
            DeveloperRepository developerRepository,
            DevelopmentToolRepository toolRepository,
            ProgrammingLanguageRepository languageRepository,
            OperatingSystemRepository operatingSystemRepository,
            ToolCategoryRepository categoryRepository
    ) {
        this.projectRepository = projectRepository;
        this.developerRepository = developerRepository;
        this.toolRepository = toolRepository;
        this.languageRepository = languageRepository;
        this.operatingSystemRepository = operatingSystemRepository;
        this.categoryRepository = categoryRepository;
    }

    // Возвращает основные показатели приложения
    public DashboardStats getStats() {
        return new DashboardStats(
                projectRepository.count(),
                developerRepository.count(),
                toolRepository.count(),
                languageRepository.count(),
                operatingSystemRepository.count(),
                categoryRepository.count()
        );
    }

    // Возвращает популярность инструментов по количеству проектов
    public List<ToolPopularity> getToolPopularity() {
        return toolRepository.findToolPopularity();
    }
}
