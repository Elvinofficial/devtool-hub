package com.devtoolshub.repository;

import com.devtoolshub.dto.ToolPopularity;
import com.devtoolshub.entity.DevelopmentTool;
import com.devtoolshub.entity.ToolCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DevelopmentToolRepository
        extends JpaRepository<DevelopmentTool, Long> {

    Optional<DevelopmentTool> findByName(String name);

    boolean existsByCategory(ToolCategory category);

    @Override
    @EntityGraph(attributePaths = {"category"})
    List<DevelopmentTool> findAll();


    @Query("""
    SELECT new com.devtoolshub.dto.ToolPopularity(
        t.name,
        COUNT(DISTINCT p.id)
    )
    FROM SoftwareProject p
    JOIN p.developmentTools t
    GROUP BY t.id, t.name
    ORDER BY COUNT(DISTINCT p.id) DESC, t.name ASC
    """)
    List<ToolPopularity> findToolPopularity();

}