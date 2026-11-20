package com.devtoolshub.service;

import com.devtoolshub.entity.OperatingSystem;
import com.devtoolshub.repository.OperatingSystemRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OperatingSystemServiceTest {

    private OperatingSystemRepository repository;
    private OperatingSystemService service;

    @BeforeEach
    void setUp() {
        repository = mock(OperatingSystemRepository.class);
        service = new OperatingSystemService(repository);
    }

    @Test
    void shouldCreateOperatingSystem() {
        // Настраивает поведение имитации репозитория
        when(repository.findByName("Windows"))
                .thenReturn(Optional.empty());

        when(repository.save(any(OperatingSystem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Вызывает тестируемый метод
        OperatingSystem result = service.create("  Windows  ");

        // Проверяет результат
        assertEquals("Windows", result.getName());

        // Проверяет взаимодействие с репозиторием
        verify(repository).save(any(OperatingSystem.class));
    }

    @Test
    void shouldRejectDuplicateOperatingSystem() {
        // Создаёт существующую операционную систему
        OperatingSystem existing = new OperatingSystem("Windows");

        // Имитирует обнаружение дубликата в репозитории
        when(repository.findByName("Windows"))
                .thenReturn(Optional.of(existing));

        // Проверяет возникновение исключения
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create("Windows")
        );

        // Проверяет сообщение об ошибке
        assertEquals(
                "Такая операционная система уже существует",
                exception.getMessage()
        );

        // Проверяет отсутствие попытки сохранения
        verify(repository, never()).save(any(OperatingSystem.class));
    }

    @Test
    void shouldFindAllOperatingSystems() {
        OperatingSystem windows = new OperatingSystem("Windows");
        OperatingSystem linux = new OperatingSystem("Linux");

        when(repository.findAll())
                .thenReturn(List.of(windows, linux));

        List<OperatingSystem> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Windows", result.get(0).getName());
        assertEquals("Linux", result.get(1).getName());

        verify(repository).findAll();
    }

    @Test
    void shouldUpdateOperatingSystem() {
        Long id = 1L;

        OperatingSystem existing = new OperatingSystem("Windows");

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(repository.findByName("Windows 11"))
                .thenReturn(Optional.empty());

        when(repository.save(any(OperatingSystem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OperatingSystem result = service.update(id, "  Windows 11  ");

        assertEquals("Windows 11", result.getName());

        verify(repository).findById(id);
        verify(repository).findByName("Windows 11");
        verify(repository).save(existing);
    }

    @Test
    void shouldDeleteOperatingSystem() {
        Long id = 1L;

        when(repository.existsById(id))
                .thenReturn(true);

        service.delete(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void shouldRejectEmptyName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create("   ")
        );

        verifyNoInteractions(repository);
    }
}
