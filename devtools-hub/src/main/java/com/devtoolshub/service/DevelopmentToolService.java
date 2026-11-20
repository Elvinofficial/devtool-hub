
package com.devtoolshub.service;

import com.devtoolshub.entity.DevelopmentTool;
import com.devtoolshub.entity.ToolCategory;
import com.devtoolshub.repository.DevelopmentToolRepository;
import com.devtoolshub.repository.ToolCategoryRepository;
import com.devtoolshub.util.NameNormalizer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DevelopmentToolService {

    private final DevelopmentToolRepository toolRepository;
    private final ToolCategoryRepository categoryRepository;

    public DevelopmentToolService(
            DevelopmentToolRepository toolRepository,
            ToolCategoryRepository categoryRepository
    ) {
        this.toolRepository = toolRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<DevelopmentTool> findAll() {
        return toolRepository.findAll();
    }

    @Transactional
    public DevelopmentTool create(
            String name,
            Long categoryId,
            String description,
            String websiteUrl,
            String version
    ) {
        String normalizedName = NameNormalizer.normalize(name, 100);

        validateUniqueName(normalizedName, null);

        ToolCategory category = findCategory(categoryId);

        DevelopmentTool tool = new DevelopmentTool(normalizedName, category);
        tool.setDescription(normalizeOptional(description));
        tool.setWebsiteUrl(normalizeOptional(websiteUrl));
        tool.setVersion(normalizeOptional(version));

        return toolRepository.save(tool);
    }

    @Transactional
    public DevelopmentTool update(
            Long id,
            String name,
            Long categoryId,
            String description,
            String websiteUrl,
            String version
    ) {
        String normalizedName = NameNormalizer.normalize(name, 100);

        DevelopmentTool tool = toolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Инструмент не найден"
                ));

        validateUniqueName(normalizedName, id);

        ToolCategory category = findCategory(categoryId);

        tool.setName(normalizedName);
        tool.setCategory(category);
        tool.setDescription(normalizeOptional(description));
        tool.setWebsiteUrl(normalizeOptional(websiteUrl));
        tool.setVersion(normalizeOptional(version));

        return toolRepository.save(tool);
    }

    @Transactional
    public void delete(Long id) {
        if (!toolRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Инструмент не найден"
            );
        }

        toolRepository.deleteById(id);
    }

    // Проверяет уникальность названия инструмента
    private void validateUniqueName(String name, Long currentId) {
        toolRepository.findByName(name)
                .filter(existing ->
                        currentId == null ||
                                !existing.getId().equals(currentId)
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Инструмент с таким названием уже существует"
                    );
                });
    }

    // Находит категорию по идентификатору
    private ToolCategory findCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException(
                    "Необходимо выбрать категорию"
            );
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Категория не найдена"
                ));
    }

    // Удаляет лишние пробелы из необязательных полей
    private String normalizeOptional(String value) {
        return value == null ? null : value.trim();
    }
}
