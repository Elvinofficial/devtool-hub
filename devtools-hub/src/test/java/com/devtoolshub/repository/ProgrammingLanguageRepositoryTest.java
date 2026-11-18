package com.devtoolshub.repository;

import com.devtoolshub.entity.ProgrammingLanguage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ProgrammingLanguageRepositoryTest {

    @Autowired
    private ProgrammingLanguageRepository repository;

    @Test
    void shouldSaveAndFindLanguage() {
        ProgrammingLanguage language =
                new ProgrammingLanguage("Kotlin");

        repository.save(language);

        var result = repository.findByName("Kotlin");

        assertTrue(result.isPresent());
        assertEquals("Kotlin", result.get().getName());
        assertNotNull(result.get().getId());
    }
}
