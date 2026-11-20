package com.devtoolshub.service;

import com.devtoolshub.entity.OperatingSystem;
import com.devtoolshub.util.NameNormalizer;
import com.devtoolshub.repository.OperatingSystemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperatingSystemService {

    private final OperatingSystemRepository repository;

    public OperatingSystemService(OperatingSystemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<OperatingSystem> findAll() {
        return repository.findAll();
    }

    @Transactional
    public OperatingSystem create(String name) {
        String normalizedName = NameNormalizer.normalize(name, 50);

        repository.findByName(normalizedName)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Такая операционная система уже существует"
                    );
                });

        return repository.save(new OperatingSystem(normalizedName));
    }

    @Transactional
    public OperatingSystem update(Long id, String name) {
        String normalizedName = NameNormalizer.normalize(name, 50);

        OperatingSystem operatingSystem = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Операционная система не найдена"
                ));

        repository.findByName(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Такая операционная система уже существует"
                    );
                });

        operatingSystem.setName(normalizedName);

        return repository.save(operatingSystem);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Операционная система не найдена"
            );
        }

        repository.deleteById(id);
    }

}