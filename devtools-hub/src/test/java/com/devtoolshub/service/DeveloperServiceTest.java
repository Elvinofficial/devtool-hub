
package com.devtoolshub.service;

import com.devtoolshub.entity.Developer;
import com.devtoolshub.repository.DeveloperRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeveloperServiceTest {

    private DeveloperRepository repository;
    private DeveloperService service;

    @BeforeEach
    void setUp() {
        repository = mock(DeveloperRepository.class);
        service = new DeveloperService(repository);
    }

    // Проверяет успешное создание разработчика
    @Test
    void shouldCreateDeveloper() {
        when(repository.findByEmail("ivan@example.com"))
                .thenReturn(Optional.empty());

        when(repository.save(any(Developer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Developer result = service.create(
                "  Иван  ",
                "  Иванович  ",
                "  Иванов  ",
                "  IVAN@Example.COM  ",
                "  Backend Developer  "
        );

        assertEquals("Иван", result.getFirstName());
        assertEquals("Иванович", result.getMiddleName());
        assertEquals("Иванов", result.getLastName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals("Backend Developer", result.getSpecialization());

        verify(repository).save(any(Developer.class));
    }

    // Проверяет получение списка разработчиков
    @Test
    void shouldFindAllDevelopers() {
        Developer first = new Developer(
                "Иван",  "ivan@example.com"
        );

        Developer second = new Developer(
                "Пётр",  "petr@example.com"
        );

        when(repository.findAll())
                .thenReturn(List.of(first, second));

        List<Developer> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Иван", result.get(0).getFirstName());
        assertEquals("Пётр", result.get(1).getFirstName());

        verify(repository).findAll();
    }

    // Проверяет успешное обновление разработчика
    @Test
    void shouldUpdateDeveloper() {
        Long id = 1L;

        Developer existing = new Developer(
                "Иван",  "ivan@example.com"
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(repository.findByEmail("petr@example.com"))
                .thenReturn(Optional.empty());

        when(repository.save(any(Developer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Developer result = service.update(
                id,
                "  Пётр  ",
                "  Петрович  ",
                "  Петров  ",
                "  PETR@Example.COM  ",
                "  Java Developer  "
        );

        assertEquals("Пётр", result.getFirstName());
        assertEquals("Петрович", result.getMiddleName());
        assertEquals("Петров", result.getLastName());
        assertEquals("petr@example.com", result.getEmail());
        assertEquals("Java Developer", result.getSpecialization());

        verify(repository).save(existing);
    }

    // Проверяет успешное удаление разработчика
    @Test
    void shouldDeleteDeveloper() {
        Long id = 1L;

        when(repository.existsById(id))
                .thenReturn(true);

        service.delete(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    // Проверяет запрет дублирования email
    @Test
    void shouldRejectDuplicateEmail() {
        Developer existing = new Developer(
                "Иван",  "ivan@example.com"
        );

        when(repository.findByEmail("ivan@example.com"))
                .thenReturn(Optional.of(existing));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "Пётр",
                        null,
                        "Петров",
                        "IVAN@EXAMPLE.COM",
                        null
                )
        );

        assertEquals(
                "Разработчик с таким email уже существует",
                exception.getMessage()
        );

        verify(repository, never()).save(any(Developer.class));
    }

    // Проверяет запрет пустого имени
    @Test
    void shouldRejectBlankFirstName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "   ",
                        null,
                        "Иванов",
                        "ivan@example.com",
                        null
                )
        );

        verifyNoInteractions(repository);
    }

    // Проверяет запрет null вместо имени
    @Test
    void shouldRejectNullFirstName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        null,
                        null,
                        "Иванов",
                        "ivan@example.com",
                        null
                )
        );

        verifyNoInteractions(repository);
    }

    // Проверяет запрет пустого email
    @Test
    void shouldRejectBlankEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "Иван",
                        null,
                        "Иванов",
                        "   ",
                        null
                )
        );

        verifyNoInteractions(repository);
    }

    // Проверяет запрет некорректного email
    @Test
    void shouldRejectInvalidEmail() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "Иван",
                        null,
                        "Иванов",
                        "invalid-email",
                        null
                )
        );

        assertEquals(
                "Некорректный формат email",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }

    // Проверяет обновление несуществующего разработчика
    @Test
    void shouldRejectUpdateOfMissingDeveloper() {
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.update(
                        999L,
                        "Иван",
                        null,
                        "Иванов",
                        "ivan@example.com",
                        null
                )
        );

        assertEquals(
                "Разработчик не найден",
                exception.getMessage()
        );

        verify(repository, never()).save(any(Developer.class));
    }

    // Проверяет удаление несуществующего разработчика
    @Test
    void shouldRejectDeleteOfMissingDeveloper() {
        when(repository.existsById(999L))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.delete(999L)
        );

        assertEquals(
                "Разработчик не найден",
                exception.getMessage()
        );

        verify(repository, never()).deleteById(any());
    }

    // Проверяет необязательные поля
    @Test
    void shouldAllowNullOptionalFields() {
        when(repository.save(any(Developer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Developer result = service.create(
                "Иван",
                null,
                "Иванов",
                "ivan@example.com",
                null
        );

        assertNull(result.getMiddleName());
        assertNull(result.getSpecialization());
    }

    // Проверяет ограничение длины имени
    @Test
    void shouldRejectTooLongFirstName() {
        String longName = "А".repeat(101);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        longName,
                        null,
                        "Иванов",
                        "ivan@example.com",
                        null
                )
        );

        verifyNoInteractions(repository);
    }

    // Проверяет ограничение длины специализации
    @Test
    void shouldRejectTooLongSpecialization() {
        String longSpecialization = "A".repeat(200);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "Иван",
                        null,
                        "Иванов",
                        "ivan@example.com",
                        longSpecialization
                )
        );

        verify(repository, never()).save(any(Developer.class));
    }

    // Проверяет ограничение длины email
    @Test
    void shouldRejectTooLongEmail() {
        String longEmail = "a".repeat(250) + "@example.com";

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "Иван",
                        null,
                        "Иванов",
                        longEmail,
                        null
                )
        );

        verifyNoInteractions(repository);
    }
}
