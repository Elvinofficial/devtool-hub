package com.devtoolshub.service;

import com.devtoolshub.entity.ProgrammingLanguage;
import com.devtoolshub.repository.ProgrammingLanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgrammingLanguageService {

    private final ProgrammingLanguageRepository repository;

    public ProgrammingLanguageService(
            ProgrammingLanguageRepository repository) {
        this.repository = repository;
    }

    public List<ProgrammingLanguage> findAll() {
        return repository.findAll();
    }

    public ProgrammingLanguage create(String name){
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException(
                    "Название языка программирования не может быть пустым"
            );
        }

        String normalizedName = name.trim();

        if(repository.findByName(normalizedName).isPresent()){
            throw  new IllegalArgumentException(
                    "Такой язык программирования уже существует"
            );
        }

        ProgrammingLanguage language =
                new ProgrammingLanguage(normalizedName);

        return repository.save(language);
    }

    public ProgrammingLanguage update(Long id, String name){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException(
                    "Название языка программирования не может быть пустым"
            );
        }
        ProgrammingLanguage language = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Язык программирвоания не найден"
                ));

        String normalizedName = name.trim();

        repository.findByName(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Такой язык программирования уже существует"
                    );
                });
        language.setName(normalizedName);

        return  repository.save(language);
    }
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new IllegalArgumentException(
                    "Язык программирования не найден"
            );
        }
        repository.deleteById(id);
    }
    public ProgrammingLanguage findById(Long id){
        return  repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Язык прогарммирования не найде"
                ));
    }
}
