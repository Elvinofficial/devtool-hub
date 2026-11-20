
package com.devtoolshub.service;

import com.devtoolshub.entity.ToolCategory;
import com.devtoolshub.repository.DevelopmentToolRepository;
import com.devtoolshub.repository.ToolCategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ToolCategoryServiceTest {

    private ToolCategoryRepository repository;
    private DevelopmentToolRepository toolRepository;
    private ToolCategoryService service;

    @BeforeEach
    void setUp() {
        repository = mock(ToolCategoryRepository.class);
        toolRepository = mock(DevelopmentToolRepository.class);

        service = new ToolCategoryService(
                repository,
                toolRepository
        );
    }


    @Test
    void shouldCreateCategory() {
        when(repository.findByName("IDE"))
                .thenReturn(Optional.empty());

        when(repository.save(any(ToolCategory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ToolCategory result = service.create("  IDE  ");

        assertEquals("IDE", result.getName());

        verify(repository).findByName("IDE");
        verify(repository).save(any(ToolCategory.class));
    }

    @Test
    void shouldRejectDuplicateCategory() {
        ToolCategory existing = new ToolCategory("IDE");

        when(repository.findByName("IDE"))
                .thenReturn(Optional.of(existing));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create("IDE")
        );

        assertEquals(
                "Такая категория уже существует",
                exception.getMessage()
        );

        verify(repository, never()).save(any(ToolCategory.class));
    }

    @Test
    void shouldFindAllCategories() {
        ToolCategory ide = new ToolCategory("IDE");
        ToolCategory database = new ToolCategory("СУБД");

        when(repository.findAll())
                .thenReturn(List.of(ide, database));

        List<ToolCategory> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("IDE", result.get(0).getName());
        assertEquals("СУБД", result.get(1).getName());

        verify(repository).findAll();
    }

    @Test
    void shouldUpdateCategory() {
        Long id = 1L;
        ToolCategory existing = new ToolCategory("IDE");

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(repository.findByName("Среды разработки"))
                .thenReturn(Optional.empty());

        when(repository.save(any(ToolCategory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ToolCategory result = service.update(
                id,
                "  Среды разработки  "
        );

        assertEquals("Среды разработки", result.getName());

        verify(repository).findById(id);
        verify(repository).findByName("Среды разработки");
        verify(repository).save(existing);
    }


    @Test
    void shouldDeleteCategory() {
        Long id = 1L;
        ToolCategory category = new ToolCategory("IDE");

        when(repository.findById(id))
                .thenReturn(Optional.of(category));

        when(toolRepository.existsByCategory(category))
                .thenReturn(false);

        service.delete(id);

        verify(repository).findById(id);
        verify(toolRepository).existsByCategory(category);
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

    @Test
    void shouldRejectNullName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(null)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectUpdateOfMissingCategory() {
        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.update(id, "IDE")
        );

        assertEquals(
                "Категория не найдена",
                exception.getMessage()
        );

        verify(repository, never()).save(any(ToolCategory.class));
    }

    @Test
    void shouldRejectDeleteOfMissingCategory() {
        Long id = 999L;

        when(repository.existsById(id))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.delete(id)
        );

        assertEquals(
                "Категория не найдена",
                exception.getMessage()
        );

        verify(repository, never()).deleteById(any());
    }


    // Проверяет запрет удаления категории с инструментами
    @Test
    void shouldRejectDeletingUsedCategory() {
        Long id = 1L;
        ToolCategory category = new ToolCategory("IDE");

        when(repository.findById(id))
                .thenReturn(Optional.of(category));

        when(toolRepository.existsByCategory(category))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.delete(id)
        );

        assertEquals(
                "Нельзя удалить категорию, поскольку к ней привязаны инструменты",
                exception.getMessage()
        );

        verify(repository, never()).deleteById(id);
    }


    // Проверяет удаление категории без инструментов
    @Test
    void shouldDeleteUnusedCategory() {
        Long id = 1L;
        ToolCategory category = new ToolCategory("IDE");

        when(repository.findById(id))
                .thenReturn(Optional.of(category));

        when(toolRepository.existsByCategory(category))
                .thenReturn(false);

        service.delete(id);

        verify(repository).deleteById(id);
    }


}
