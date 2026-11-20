
package com.devtoolshub.service;

import com.devtoolshub.entity.*;
import com.devtoolshub.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SoftwareProjectServiceTest {

    private SoftwareProjectRepository projectRepository;
    private ProgrammingLanguageRepository languageRepository;
    private OperatingSystemRepository operatingSystemRepository;
    private DevelopmentToolRepository toolRepository;
    private DeveloperRepository developerRepository;
    private SoftwareProjectService service;

    @BeforeEach
    void setUp() {
        projectRepository = mock(SoftwareProjectRepository.class);
        languageRepository = mock(ProgrammingLanguageRepository.class);
        operatingSystemRepository = mock(OperatingSystemRepository.class);
        toolRepository = mock(DevelopmentToolRepository.class);
        developerRepository = mock(DeveloperRepository.class);

        service = new SoftwareProjectService(
                projectRepository,
                languageRepository,
                operatingSystemRepository,
                toolRepository,
                developerRepository
        );
    }

    @Test
    void shouldGetProgrammingLanguages() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        ProgrammingLanguage java = new ProgrammingLanguage("Java");

        project.addProgrammingLanguage(java);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        Set<ProgrammingLanguage> result =
                service.getProgrammingLanguages(1L);

        assertEquals(1, result.size());
        assertTrue(result.contains(java));
    }

    @Test
    void shouldAddProgrammingLanguage() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        ProgrammingLanguage java = new ProgrammingLanguage("Java");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(languageRepository.findById(2L))
                .thenReturn(Optional.of(java));

        service.addProgrammingLanguage(1L, 2L);

        assertTrue(
                project.getProgrammingLanguages().contains(java)
        );

        verify(projectRepository).findById(1L);
        verify(languageRepository).findById(2L);
    }

    @Test
    void shouldNotAddDuplicateProgrammingLanguage() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        ProgrammingLanguage java = new ProgrammingLanguage("Java");

        project.addProgrammingLanguage(java);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(languageRepository.findById(2L))
                .thenReturn(Optional.of(java));

        service.addProgrammingLanguage(1L, 2L);

        assertEquals(
                1,
                project.getProgrammingLanguages().size()
        );
    }

    @Test
    void shouldRemoveProgrammingLanguage() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        ProgrammingLanguage java = mock(ProgrammingLanguage.class);
        when(java.getId()).thenReturn(2L);
        project.addProgrammingLanguage(java);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        service.removeProgrammingLanguage(1L, 2L);

        assertTrue(project.getProgrammingLanguages().isEmpty());
    }

    @Test
    void shouldRejectMissingProject() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addProgrammingLanguage(1L, 2L)
        );

        assertEquals("Проект не найден", exception.getMessage());

        verifyNoInteractions(languageRepository);
    }

    @Test
    void shouldRejectMissingLanguage() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(languageRepository.findById(2L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addProgrammingLanguage(1L, 2L)
        );

        assertEquals(
                "Язык программирования не найден",
                exception.getMessage()
        );

        assertTrue(project.getProgrammingLanguages().isEmpty());
    }

    @Test
    void shouldRejectRemovingUnlinkedLanguage() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.removeProgrammingLanguage(1L, 2L)
        );

        assertEquals(
                "Язык программирования не привязан к проекту",
                exception.getMessage()
        );
    }

    @Test
    void shouldAddOperatingSystem() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        OperatingSystem os = mock(OperatingSystem.class);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(operatingSystemRepository.findById(2L))
                .thenReturn(Optional.of(os));

        service.addOperatingSystem(1L, 2L);

        assertTrue(project.getOperatingSystems().contains(os));
    }

    @Test
    void shouldRemoveOperatingSystem() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        OperatingSystem os = mock(OperatingSystem.class);

        when(os.getId()).thenReturn(2L);
        project.addOperatingSystem(os);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        service.removeOperatingSystem(1L, 2L);

        assertTrue(project.getOperatingSystems().isEmpty());
    }

    @Test
    void shouldGetOperatingSystems() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        OperatingSystem os = mock(OperatingSystem.class);
        project.addOperatingSystem(os);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        Set<OperatingSystem> result = service.getOperatingSystems(1L);

        assertEquals(1, result.size());
        assertTrue(result.contains(os));
    }


    @Test
    void shouldAddDevelopmentTool() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        DevelopmentTool tool = mock(DevelopmentTool.class);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(toolRepository.findById(2L))
                .thenReturn(Optional.of(tool));

        service.addDevelopmentTool(1L, 2L);

        assertTrue(project.getDevelopmentTools().contains(tool));
    }

    @Test
    void shouldRemoveDevelopmentTool() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        DevelopmentTool tool = mock(DevelopmentTool.class);

        when(tool.getId()).thenReturn(2L);
        project.addDevelopmentTool(tool);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        service.removeDevelopmentTool(1L, 2L);

        assertTrue(project.getDevelopmentTools().isEmpty());
    }

    @Test
    void shouldGetDevelopmentTools() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        DevelopmentTool tool = mock(DevelopmentTool.class);
        project.addDevelopmentTool(tool);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        Set<DevelopmentTool> result = service.getDevelopmentTools(1L);

        assertEquals(1, result.size());
        assertTrue(result.contains(tool));
    }


    @Test
    void shouldAddDeveloper() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        Developer developer = mock(Developer.class);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(developerRepository.findById(2L))
                .thenReturn(Optional.of(developer));

        service.addDeveloper(1L, 2L);

        assertTrue(project.getDevelopers().contains(developer));
    }

    @Test
    void shouldRemoveDeveloper() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        Developer developer = mock(Developer.class);

        when(developer.getId()).thenReturn(2L);
        project.addDeveloper(developer);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        service.removeDeveloper(1L, 2L);

        assertTrue(project.getDevelopers().isEmpty());
    }

    @Test
    void shouldGetDevelopers() {
        SoftwareProject project = new SoftwareProject("DevTools Hub");
        Developer developer = mock(Developer.class);
        project.addDeveloper(developer);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        Set<Developer> result = service.getDevelopers(1L);

        assertEquals(1, result.size());
        assertTrue(result.contains(developer));
    }

}
