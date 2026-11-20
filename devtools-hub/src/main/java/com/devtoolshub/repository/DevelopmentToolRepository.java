package com.devtoolshub.repository;

import com.devtoolshub.entity.DevelopmentTool;
import com.devtoolshub.entity.ToolCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevelopmentToolRepository
        extends JpaRepository<DevelopmentTool, Long> {

    Optional<DevelopmentTool> findByName(String name);

    boolean existsByCategory(ToolCategory category);

    @Override
    @EntityGraph(attributePaths = {"category"})
    List<DevelopmentTool> findAll();
}