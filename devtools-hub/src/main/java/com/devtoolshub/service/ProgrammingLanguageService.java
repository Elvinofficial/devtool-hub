package com.devtoolshub.service;

import com.devtoolshub.entity.ProgrammingLanguage;
import com.devtoolshub.repository.ProgrammingLanguageRepository;
import com.devtoolshub.util.NameNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgrammingLanguageService {

    private final ProgrammingLanguageRepository repository;

    public ProgrammingLanguageService(
            ProgrammingLanguageRepository repository) {
        this.repository = repository;
    }
    @Transactional(readOnly = true)
    public List<ProgrammingLanguage> findAll() {
        return repository.findAll();
    }
    @Transactional
    public ProgrammingLanguage create(String name){

        String normalizedName = NameNormalizer.normalize(name, 100);

        if(repository.findByName(normalizedName).isPresent()){
            throw  new IllegalArgumentException(
                    "Такой язык программирования уже существует"
            );
        }

        ProgrammingLanguage language =
                new ProgrammingLanguage(normalizedName);

        return repository.save(language);
    }
    @Transactional
    public ProgrammingLanguage update(Long id, String name){

        ProgrammingLanguage language = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Язык программирвоания не найден"
                ));

        String normalizedName = NameNormalizer.normalize(name, 100);


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
    @Transactional
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new IllegalArgumentException(
                    "Язык программирования не найден"
            );
        }
        repository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public ProgrammingLanguage findById(Long id){
        return  repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Язык прогарммирования не найде"
                ));
    }
}
