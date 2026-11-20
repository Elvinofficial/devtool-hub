package com.devtoolshub.service;

import com.devtoolshub.entity.ToolCategory;
import com.devtoolshub.repository.DevelopmentToolRepository;
import com.devtoolshub.repository.ToolCategoryRepository;
import com.devtoolshub.util.NameNormalizer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ToolCategoryService {

    private final ToolCategoryRepository repository;
    private final DevelopmentToolRepository toolRepository;

    public ToolCategoryService(ToolCategoryRepository repository, DevelopmentToolRepository toolRepository) {
        this.repository = repository;
        this.toolRepository = toolRepository;
    }

    @Transactional(readOnly = true)
    public List<ToolCategory> findAll() {
        return repository.findAll();
    }

    @Transactional
    public ToolCategory create(String name) {
        String normalizedName = NameNormalizer.normalize(name, 100);

        repository.findByName(normalizedName)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Такая категория уже существует"
                    );
                });

        return repository.save(new ToolCategory(normalizedName));
    }

    @Transactional
    public ToolCategory update(Long id, String name) {
        String normalizedName = NameNormalizer.normalize(name, 100);

        ToolCategory category = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Категория не найдена"
                ));

        repository.findByName(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Такая категория уже существует"
                    );
                });

        category.setName(normalizedName);

        return repository.save(category);
    }


    @Transactional
    public void delete(Long id) {
        ToolCategory category = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Категория не найдена"
                ));

        // Проверяет наличие инструментов в категории
        if (toolRepository.existsByCategory(category)) {
            throw new IllegalArgumentException(
                    "Нельзя удалить категорию, поскольку к ней привязаны инструменты"
            );
        }

        repository.deleteById(id);
    }

}
