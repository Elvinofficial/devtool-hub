
package com.devtoolshub.service;

import com.devtoolshub.entity.DevelopmentTool;
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

class DevelopmentToolServiceTest {

    private DevelopmentToolRepository toolRepository;
    private ToolCategoryRepository categoryRepository;
    private DevelopmentToolService service;

    @BeforeEach
    void setUp() {
        toolRepository = mock(DevelopmentToolRepository.class);
        categoryRepository = mock(ToolCategoryRepository.class);

        service = new DevelopmentToolService(
                toolRepository,
                categoryRepository
        );
    }

    // Проверяет успешное создание инструмента
    @Test
    void shouldCreateTool() {
        ToolCategory category = new ToolCategory("IDE");

        when(toolRepository.findByName("IntelliJ IDEA"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(toolRepository.save(any(DevelopmentTool.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DevelopmentTool result = service.create(
                "  IntelliJ IDEA  ",
                1L,
                "  Среда разработки Java  ",
                "  https://www.jetbrains.com/idea/  ",
                "  2026.2  "
        );

        assertEquals("IntelliJ IDEA", result.getName());
        assertEquals(category, result.getCategory());
        assertEquals("Среда разработки Java", result.getDescription());
        assertEquals(
                "https://www.jetbrains.com/idea/",
                result.getWebsiteUrl()
        );
        assertEquals("2026.2", result.getVersion());

        verify(toolRepository).save(any(DevelopmentTool.class));
    }

    // Проверяет запрет дублирования названия
    @Test
    void shouldRejectDuplicateTool() {
        DevelopmentTool existing = new DevelopmentTool(
                "IntelliJ IDEA",
                null
        );

        when(toolRepository.findByName("IntelliJ IDEA"))
                .thenReturn(Optional.of(existing));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "IntelliJ IDEA",
                        1L,
                        "IDE",
                        null,
                        null
                )
        );

        assertEquals(
                "Инструмент с таким названием уже существует",
                exception.getMessage()
        );

        verify(toolRepository, never())
                .save(any(DevelopmentTool.class));
    }

    // Проверяет получение всех инструментов
    @Test
    void shouldFindAllTools() {
        DevelopmentTool first = new DevelopmentTool(
                "IntelliJ IDEA",
                null
        );

        DevelopmentTool second = new DevelopmentTool(
                "Eclipse",
                null
        );

        when(toolRepository.findAll())
                .thenReturn(List.of(first, second));

        List<DevelopmentTool> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("IntelliJ IDEA", result.get(0).getName());
        assertEquals("Eclipse", result.get(1).getName());

        verify(toolRepository).findAll();
    }

    // Проверяет успешное обновление инструмента
    @Test
    void shouldUpdateTool() {
        Long id = 1L;

        DevelopmentTool existing = new DevelopmentTool(
                "IntelliJ IDEA",
                null
        );

        ToolCategory category = new ToolCategory("IDE");

        when(toolRepository.findById(id))
                .thenReturn(Optional.of(existing));

        when(toolRepository.findByName("Eclipse"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));

        when(toolRepository.save(any(DevelopmentTool.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DevelopmentTool result = service.update(
                id,
                "  Eclipse  ",
                2L,
                "  Среда разработки  ",
                "  https://eclipse.org  ",
                "  2026  "
        );

        assertEquals("Eclipse", result.getName());
        assertEquals(category, result.getCategory());
        assertEquals("Среда разработки", result.getDescription());
        assertEquals("https://eclipse.org", result.getWebsiteUrl());
        assertEquals("2026", result.getVersion());

        verify(toolRepository).save(existing);
    }

    // Проверяет успешное удаление инструмента
    @Test
    void shouldDeleteTool() {
        Long id = 1L;

        when(toolRepository.existsById(id))
                .thenReturn(true);

        service.delete(id);

        verify(toolRepository).existsById(id);
        verify(toolRepository).deleteById(id);
    }

    // Проверяет запрет пустого названия
    @Test
    void shouldRejectBlankName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "   ",
                        1L,
                        null,
                        null,
                        null
                )
        );

        verifyNoInteractions(toolRepository, categoryRepository);
    }

    // Проверяет запрет null вместо названия
    @Test
    void shouldRejectNullName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        null,
                        1L,
                        null,
                        null,
                        null
                )
        );

        verifyNoInteractions(toolRepository, categoryRepository);
    }

    // Проверяет запрет создания без категории
    @Test
    void shouldRejectMissingCategoryId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "IntelliJ IDEA",
                        null,
                        null,
                        null,
                        null
                )
        );

        verify(toolRepository, never())
                .save(any(DevelopmentTool.class));
    }

    // Проверяет запрет несуществующей категории
    @Test
    void shouldRejectUnknownCategory() {
        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        "IntelliJ IDEA",
                        999L,
                        null,
                        null,
                        null
                )
        );

        assertEquals("Категория не найдена", exception.getMessage());

        verify(toolRepository, never())
                .save(any(DevelopmentTool.class));
    }

    // Проверяет обновление несуществующего инструмента
    @Test
    void shouldRejectUpdateOfMissingTool() {
        when(toolRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.update(
                        999L,
                        "Eclipse",
                        1L,
                        null,
                        null,
                        null
                )
        );

        assertEquals("Инструмент не найден", exception.getMessage());

        verify(toolRepository, never())
                .save(any(DevelopmentTool.class));
    }

    // Проверяет удаление несуществующего инструмента
    @Test
    void shouldRejectDeleteOfMissingTool() {
        when(toolRepository.existsById(999L))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.delete(999L)
        );

        assertEquals("Инструмент не найден", exception.getMessage());

        verify(toolRepository, never()).deleteById(any());
    }

    // Проверяет допустимость необязательных полей null
    @Test
    void shouldAllowNullOptionalFields() {
        ToolCategory category = new ToolCategory("IDE");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(toolRepository.save(any(DevelopmentTool.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DevelopmentTool result = service.create(
                "IntelliJ IDEA",
                1L,
                null,
                null,
                null
        );

        assertNull(result.getDescription());
        assertNull(result.getWebsiteUrl());
        assertNull(result.getVersion());
    }

    // Проверяет ограничение длины названия
    @Test
    void shouldRejectTooLongName() {
        String longName = "A".repeat(101);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(
                        longName,
                        1L,
                        null,
                        null,
                        null
                )
        );

        verifyNoInteractions(toolRepository, categoryRepository);
    }
}
