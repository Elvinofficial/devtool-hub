package com.devtoolshub.repository;

import com.devtoolshub.entity.OperatingSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatingSystemRepository
        extends JpaRepository<OperatingSystem, Long> {

    Optional<OperatingSystem> findByName(String name);
}