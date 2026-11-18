package com.devtoolshub.service;

import com.devtoolshub.entity.ProgrammingLanguage;
import com.devtoolshub.repository.ProgrammingLanguageRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgrammingLanguageServiceTest {

    @Mock
    private ProgrammingLanguageRepository repository;

    @InjectMocks
    private ProgrammingLanguageService service;

    @Test
    void shouldRejectDuplicateLanguage() {

        when(repository.findByName("Kotlin"))
                .thenReturn(Optional.of(
                        new ProgrammingLanguage("Kotlin")
                ));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create("   Kotlin   ")
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldCreateLanguage(){
        when(repository.findByName("Python"))
                .thenReturn(Optional.empty());

        when(repository.save(any(ProgrammingLanguage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProgrammingLanguage result = service.create("Python     ");

        assertEquals("Python", result.getName());

        verify(repository).save(any(ProgrammingLanguage.class));
    }

    @Test
    void shouldUpdateLanguage(){
        ProgrammingLanguage language =
                new ProgrammingLanguage("Java");

        when(repository.findById(1L))
                .thenReturn(Optional.of(language));

        when(repository.findByName("Kotlin"))
                .thenReturn(Optional.empty());

        when(repository.save(language))
                .thenReturn(language);

        ProgrammingLanguage result = service.update(1L, "Kotlin");
        assertEquals("Kotlin", result.getName());
    }

    @Test
    void shouldDeleteLanguage(){
        when(repository.existsById(1L))
                .thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldFindLanguageById(){
        ProgrammingLanguage language = new ProgrammingLanguage("Java");

        when(repository.findById(1L))
                .thenReturn(Optional.of(language));

        ProgrammingLanguage result = service.findById(1L);

        assertEquals(language, result);
        verify(repository).findById(1L);
    }

    @Test
    void shouldFindAllLanguages() {
        ProgrammingLanguage java =
                new ProgrammingLanguage("Java");

        ProgrammingLanguage python =
                new ProgrammingLanguage("Python");

        when(repository.findAll())
                .thenReturn(List.of(java, python));

        List<ProgrammingLanguage> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("Python", result.get(1).getName());

        verify(repository).findAll();
    }
}