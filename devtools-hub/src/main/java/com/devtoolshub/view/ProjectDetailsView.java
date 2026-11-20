
package com.devtoolshub.view;

import com.devtoolshub.entity.SoftwareProject;
import com.devtoolshub.service.SoftwareProjectService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.devtoolshub.entity.ProgrammingLanguage;
import com.devtoolshub.service.ProgrammingLanguageService;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Span;
import com.devtoolshub.entity.OperatingSystem;
import com.devtoolshub.entity.DevelopmentTool;
import com.devtoolshub.entity.Developer;

import com.devtoolshub.service.OperatingSystemService;
import com.devtoolshub.service.DevelopmentToolService;
import com.devtoolshub.service.DeveloperService;

import java.util.Comparator;
import java.util.Set;

@Route("projects")
@PageTitle("Информация о проекте | DevTools Hub")
public class ProjectDetailsView extends VerticalLayout
        implements HasUrlParameter<Long> {

    private final SoftwareProjectService service;

    private SoftwareProject project;

    private final VerticalLayout content = new VerticalLayout();

    private final ProgrammingLanguageService languageService;
    private final OperatingSystemService operatingSystemService;
    private final DevelopmentToolService toolService;
    private final DeveloperService developerService;

    public ProjectDetailsView(
            SoftwareProjectService service,
            ProgrammingLanguageService languageService,
            OperatingSystemService operatingSystemService,
            DevelopmentToolService toolService,
            DeveloperService developerService
    ) {
        this.service = service;
        this.languageService = languageService;
        this.operatingSystemService = operatingSystemService;
        this.toolService = toolService;
        this.developerService = developerService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        getStyle()
                .set("max-width", "1100px")
                .set("margin", "0 auto");

        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        add(content);
    }

    // Загружает проект по идентификатору из адреса
    @Override
    public void setParameter(BeforeEvent event, Long projectId) {
        try {
            project = service.findById(projectId);
            refreshContent();
        } catch (IllegalArgumentException exception) {
            Notification.show(exception.getMessage());
            event.forwardTo(ProjectView.class);
        }
    }

    // Обновляет содержимое страницы
    // Обновляет содержимое страницы проекта
    // Обновляет содержимое страницы проекта
    private void refreshContent() {
        content.removeAll();

        content.add(
                createHeader(),
                createProjectInfo(),
                createLanguagesSection(),
                createOperatingSystemsSection(),
                createDevelopmentToolsSection(),
                createDevelopersSection()
        );
    }

    private Div createOperatingSystemsSection() {
        Div section = new Div();
        section.addClassName("project-section-card");

        Icon icon = new Icon(VaadinIcon.DESKTOP);
        icon.setColor("#eb8f25");

        H3 title = new H3("Операционные системы");
        title.getStyle().set("margin", "0");

        HorizontalLayout heading = new HorizontalLayout(icon, title);
        heading.setAlignItems(Alignment.CENTER);

        Button addButton = new Button(
                "Добавить",
                new Icon(VaadinIcon.PLUS),
                event -> openAddOperatingSystemsDialog()
        );
        addButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        HorizontalLayout header = new HorizontalLayout(heading, addButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Div list = new Div();
        list.addClassName("project-tag-list");

        Set<OperatingSystem> systems =
                service.getOperatingSystems(project.getId());

        if (systems.isEmpty()) {
            Span empty = new Span("Операционные системы пока не добавлены");
            empty.getStyle().set("color", "#6b7280");
            list.add(empty);
        } else {
            systems.stream()
                    .sorted(Comparator.comparing(
                            OperatingSystem::getName,
                            String.CASE_INSENSITIVE_ORDER
                    ))
                    .forEach(os -> list.add(createOperatingSystemTag(os)));
        }

        section.add(header, list);
        return section;
    }

    // Создаёт элемент операционной системы
    private Div createOperatingSystemTag(OperatingSystem os) {
        Div tag = new Div();
        tag.addClassName("project-tag");

        Span name = new Span(os.getName());

        Button removeButton = new Button(
                new Icon(VaadinIcon.CLOSE_SMALL)
        );
        removeButton.addClassName("project-tag-remove");
        removeButton.getElement().setAttribute(
                "aria-label", "Отвязать ОС " + os.getName()
        );

        removeButton.addClickListener(event -> {
            try {
                service.removeOperatingSystem(project.getId(), os.getId());
                refreshContent();
                Notification.show("Операционная система отвязана");
            } catch (RuntimeException exception) {
                Notification.show(exception.getMessage());
            }
        });

        tag.add(name, removeButton);
        return tag;
    }

    // Открывает диалог выбора операционных систем
    private void openAddOperatingSystemsDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Добавить операционные системы");
        dialog.setWidth("480px");

        MultiSelectComboBox<OperatingSystem> box =
                new MultiSelectComboBox<>("Операционные системы");

        box.setWidthFull();
        box.setItemLabelGenerator(OperatingSystem::getName);

        Set<Long> linkedIds = service.getOperatingSystems(project.getId())
                .stream()
                .map(OperatingSystem::getId)
                .collect(java.util.stream.Collectors.toSet());

        box.setItems(
                operatingSystemService.findAll().stream()
                        .filter(os -> !linkedIds.contains(os.getId()))
                        .sorted(Comparator.comparing(
                                OperatingSystem::getName,
                                String.CASE_INSENSITIVE_ORDER
                        ))
                        .toList()
        );

        dialog.add(box);

        Button cancel = new Button("Отмена", event -> dialog.close());

        Button save = new Button("Добавить", event -> {
            if (box.getSelectedItems().isEmpty()) {
                Notification.show("Выберите хотя бы одну ОС");
                return;
            }

            try {
                for (OperatingSystem os : box.getSelectedItems()) {
                    service.addOperatingSystem(project.getId(), os.getId());
                }

                dialog.close();
                refreshContent();
                Notification.show("Операционные системы добавлены");
            } catch (RuntimeException exception) {
                Notification.show(exception.getMessage());
            }
        });

        save.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private Div createDevelopmentToolsSection() {
        Div section = new Div();
        section.addClassName("project-section-card");

        Icon icon = new Icon(VaadinIcon.TOOLS);
        icon.setColor("#eb8f25");

        H3 title = new H3("Инструменты разработки");
        title.getStyle().set("margin", "0");

        HorizontalLayout heading = new HorizontalLayout(icon, title);
        heading.setAlignItems(Alignment.CENTER);

        Button addButton = new Button(
                "Добавить",
                new Icon(VaadinIcon.PLUS),
                event -> openAddDevelopmentToolsDialog()
        );
        addButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        HorizontalLayout header = new HorizontalLayout(heading, addButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Div list = new Div();
        list.addClassName("project-tag-list");

        Set<DevelopmentTool> tools =
                service.getDevelopmentTools(project.getId());

        if (tools.isEmpty()) {
            Span empty = new Span("Инструменты пока не добавлены");
            empty.getStyle().set("color", "#6b7280");
            list.add(empty);
        } else {
            tools.stream()
                    .sorted(Comparator.comparing(
                            DevelopmentTool::getName,
                            String.CASE_INSENSITIVE_ORDER
                    ))
                    .forEach(tool -> list.add(createDevelopmentToolTag(tool)));
        }

        section.add(header, list);
        return section;
    }

    // Создаёт элемент инструмента разработки
    private Div createDevelopmentToolTag(DevelopmentTool tool) {
        Div tag = new Div();
        tag.addClassName("project-tag");

        Span name = new Span(tool.getName());

        Button removeButton = new Button(
                new Icon(VaadinIcon.CLOSE_SMALL)
        );
        removeButton.addClassName("project-tag-remove");
        removeButton.getElement().setAttribute(
                "aria-label", "Отвязать инструмент " + tool.getName()
        );

        removeButton.addClickListener(event -> {
            try {
                service.removeDevelopmentTool(project.getId(), tool.getId());
                refreshContent();
                Notification.show("Инструмент отвязан");
            } catch (RuntimeException exception) {
                Notification.show(exception.getMessage());
            }
        });

        tag.add(name, removeButton);
        return tag;
    }

    // Открывает диалог выбора инструментов разработки
    private void openAddDevelopmentToolsDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Добавить инструменты разработки");
        dialog.setWidth("480px");

        MultiSelectComboBox<DevelopmentTool> box =
                new MultiSelectComboBox<>("Инструменты разработки");

        box.setWidthFull();
        box.setItemLabelGenerator(DevelopmentTool::getName);

        Set<Long> linkedIds = service.getDevelopmentTools(project.getId())
                .stream()
                .map(DevelopmentTool::getId)
                .collect(java.util.stream.Collectors.toSet());

        box.setItems(
                toolService.findAll().stream()
                        .filter(tool -> !linkedIds.contains(tool.getId()))
                        .sorted(Comparator.comparing(
                                DevelopmentTool::getName,
                                String.CASE_INSENSITIVE_ORDER
                        ))
                        .toList()
        );

        dialog.add(box);

        Button cancel = new Button("Отмена", event -> dialog.close());

        Button save = new Button("Добавить", event -> {
            if (box.getSelectedItems().isEmpty()) {
                Notification.show("Выберите хотя бы один инструмент");
                return;
            }

            try {
                for (DevelopmentTool tool : box.getSelectedItems()) {
                    service.addDevelopmentTool(project.getId(), tool.getId());
                }

                dialog.close();
                refreshContent();
                Notification.show("Инструменты добавлены");
            } catch (RuntimeException exception) {
                Notification.show(exception.getMessage());
            }
        });

        save.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
    // Формирует отображаемое имя разработчика
    private String getDeveloperDisplayName(Developer developer) {
        return java.util.stream.Stream.of(
                        developer.getLastName(),
                        developer.getFirstName(),
                        developer.getMiddleName()
                )
                .filter(value -> value != null && !value.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    private Div createDevelopersSection() {
        Div section = new Div();
        section.addClassName("project-section-card");

        Icon icon = new Icon(VaadinIcon.USERS);
        icon.setColor("#eb8f25");

        H3 title = new H3("Разработчики");
        title.getStyle().set("margin", "0");

        HorizontalLayout heading = new HorizontalLayout(icon, title);
        heading.setAlignItems(Alignment.CENTER);

        Button addButton = new Button(
                "Добавить",
                new Icon(VaadinIcon.PLUS),
                event -> openAddDevelopersDialog()
        );
        addButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        HorizontalLayout header = new HorizontalLayout(heading, addButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Div list = new Div();
        list.addClassName("project-tag-list");

        Set<Developer> developers =
                service.getDevelopers(project.getId());

        if (developers.isEmpty()) {
            Span empty = new Span("Разработчики пока не добавлены");
            empty.getStyle().set("color", "#6b7280");
            list.add(empty);
        } else {
            developers.stream()
                    .sorted(Comparator.comparing(
                            this::getDeveloperDisplayName,
                            String.CASE_INSENSITIVE_ORDER
                    ))
                    .forEach(developer ->
                            list.add(createDeveloperTag(developer))
                    );
        }

        section.add(header, list);
        return section;
    }

    // Создаёт элемент разработчика
    private Div createDeveloperTag(Developer developer) {
        Div tag = new Div();
        tag.addClassName("project-tag");

        Span name = new Span(getDeveloperDisplayName(developer));

        Button removeButton = new Button(
                new Icon(VaadinIcon.CLOSE_SMALL)
        );
        removeButton.addClassName("project-tag-remove");
        removeButton.getElement().setAttribute(
                "aria-label",
                "Отвязать разработчика " + getDeveloperDisplayName(developer)
        );

        removeButton.addClickListener(event -> {
            try {
                service.removeDeveloper(
                        project.getId(),
                        developer.getId()
                );

                refreshContent();
                Notification.show("Разработчик отвязан");
            } catch (RuntimeException exception) {
                Notification.show(exception.getMessage());
            }
        });

        tag.add(name, removeButton);
        return tag;
    }

    // Открывает диалог выбора разработчиков
    private void openAddDevelopersDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Добавить разработчиков");
        dialog.setWidth("480px");

        MultiSelectComboBox<Developer> box =
                new MultiSelectComboBox<>("Разработчики");

        box.setWidthFull();
        box.setItemLabelGenerator(this::getDeveloperDisplayName);

        Set<Long> linkedIds = service.getDevelopers(project.getId())
                .stream()
                .map(Developer::getId)
                .collect(java.util.stream.Collectors.toSet());

        box.setItems(
                developerService.findAll().stream()
                        .filter(developer ->
                                !linkedIds.contains(developer.getId())
                        )
                        .sorted(Comparator.comparing(
                                this::getDeveloperDisplayName,
                                String.CASE_INSENSITIVE_ORDER
                        ))
                        .toList()
        );

        dialog.add(box);

        Button cancel = new Button("Отмена", event -> dialog.close());

        Button save = new Button("Добавить", event -> {
            if (box.getSelectedItems().isEmpty()) {
                Notification.show("Выберите хотя бы одного разработчика");
                return;
            }

            try {
                for (Developer developer : box.getSelectedItems()) {
                    service.addDeveloper(
                            project.getId(),
                            developer.getId()
                    );
                }

                dialog.close();
                refreshContent();
                Notification.show("Разработчики добавлены");
            } catch (RuntimeException exception) {
                Notification.show(exception.getMessage());
            }
        });

        save.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        dialog.getFooter().add(cancel, save);
        dialog.open();
    }





    private Div createLanguagesSection() {
        Div section = new Div();
        section.addClassName("project-section-card");

        Icon icon = new Icon(VaadinIcon.CODE);
        icon.setColor("#eb8f25");

        H3 title = new H3("Языки программирования");
        title.getStyle().set("margin", "0");

        HorizontalLayout heading = new HorizontalLayout(icon, title);
        heading.setAlignItems(Alignment.CENTER);

        Button addButton = new Button(
                "Добавить",
                new Icon(VaadinIcon.PLUS),
                event -> openAddLanguagesDialog()
        );

        addButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        HorizontalLayout header = new HorizontalLayout(
                heading, addButton
        );
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(
                JustifyContentMode.BETWEEN
        );

        Div languageList = new Div();
        languageList.addClassName("project-tag-list");

        Set<ProgrammingLanguage> languages =
                service.getProgrammingLanguages(project.getId());

        if (languages.isEmpty()) {
            Span empty = new Span(
                    "Языки программирования пока не добавлены"
            );
            empty.getStyle().set("color", "#6b7280");
            languageList.add(empty);
        } else {
            languages.stream()
                    .sorted(Comparator.comparing(
                            ProgrammingLanguage::getName,
                            String.CASE_INSENSITIVE_ORDER
                    ))
                    .forEach(language ->
                            languageList.add(createLanguageTag(language))
                    );
        }

        section.add(header, languageList);

        return section;
    }

    // Создаёт элемент языка с кнопкой отвязки
    private Div createLanguageTag(ProgrammingLanguage language) {
        Div tag = new Div();
        tag.addClassName("project-tag");

        Span name = new Span(language.getName());

        Button removeButton = new Button(
                new Icon(VaadinIcon.CLOSE_SMALL)
        );

        removeButton.addClassName("project-tag-remove");
        removeButton.getElement().setAttribute(
                "aria-label",
                "Отвязать язык " + language.getName()
        );

        removeButton.addClickListener(event -> {
            try {
                service.removeProgrammingLanguage(
                        project.getId(),
                        language.getId()
                );

                refreshContent();
                Notification.show("Язык отвязан от проекта");
            } catch (RuntimeException exception) {
                Notification.show(exception.getMessage());
            }
        });

        tag.add(name, removeButton);

        return tag;
    }

    // Открывает диалог добавления языков программирования
    private void openAddLanguagesDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Добавить языки программирования");
        dialog.setWidth("480px");

        MultiSelectComboBox<ProgrammingLanguage> languageBox =
                new MultiSelectComboBox<>("Языки программирования");

        languageBox.setWidthFull();
        languageBox.setItemLabelGenerator(
                ProgrammingLanguage::getName
        );

        Set<Long> linkedIds = service
                .getProgrammingLanguages(project.getId())
                .stream()
                .map(ProgrammingLanguage::getId)
                .collect(java.util.stream.Collectors.toSet());

        // Показывает только языки, ещё не привязанные к проекту
        languageBox.setItems(
                languageService.findAll()
                        .stream()
                        .filter(language ->
                                !linkedIds.contains(language.getId())
                        )
                        .sorted(Comparator.comparing(
                                ProgrammingLanguage::getName,
                                String.CASE_INSENSITIVE_ORDER
                        ))
                        .toList()
        );

        dialog.add(languageBox);

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        Button saveButton = new Button("Добавить", event -> {
            if (languageBox.getSelectedItems().isEmpty()) {
                Notification.show("Выберите хотя бы один язык");
                return;
            }

            try {
                for (ProgrammingLanguage language
                        : languageBox.getSelectedItems()) {
                    service.addProgrammingLanguage(
                            project.getId(),
                            language.getId()
                    );
                }

                dialog.close();
                refreshContent();

                Notification.show("Языки добавлены в проект");
            } catch (RuntimeException exception) {
                Notification.show(
                        "Не удалось добавить языки: "
                                + exception.getMessage()
                );
            }
        });

        saveButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }




    // Создаёт заголовок страницы
    private HorizontalLayout createHeader() {
        Button backButton = new Button(
                new Icon(VaadinIcon.ARROW_LEFT),
                event -> UI.getCurrent().navigate(ProjectView.class)
        );
        backButton.addClassName("back-button");

        H2 title = new H2("Информация о проекте");
        title.getStyle().set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(
                backButton, title
        );

        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);

        return header;
    }

    // Создаёт блок основной информации о проекте
    private Div createProjectInfo() {
        Div info = new Div();
        info.addClassName("project-info-card");

        H2 name = new H2(project.getName());
        name.getStyle().set("margin", "0");

        Paragraph description = new Paragraph(
                project.getDescription() == null
                        || project.getDescription().isBlank()
                        ? "Описание отсутствует"
                        : project.getDescription()
        );
        description.addClassName("project-info-description");

        Button editButton = new Button(
                "Редактировать",
                new Icon(VaadinIcon.EDIT),
                event -> openEditDialog()
        );
        editButton.getStyle()
                .set("background", "#2563eb")
                .set("color", "white");

        Button deleteButton = new Button(
                "Удалить",
                new Icon(VaadinIcon.TRASH),
                event -> openDeleteDialog()
        );
        deleteButton.getStyle()
                .set("background", "#dc2626")
                .set("color", "white");

        HorizontalLayout actions = new HorizontalLayout(
                editButton, deleteButton
        );
        actions.getStyle().set("flex-wrap", "wrap");

        info.add(name, description, actions);

        return info;
    }

    // Открывает диалог редактирования проекта
    private void openEditDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Редактировать проект");
        dialog.setWidth("480px");

        TextField nameField = new TextField("Название");
        nameField.setWidthFull();
        nameField.setRequiredIndicatorVisible(true);
        nameField.setMaxLength(200);
        nameField.setValue(project.getName());

        TextArea descriptionField = new TextArea("Описание");
        descriptionField.setWidthFull();
        descriptionField.setMinHeight("130px");
        descriptionField.setValue(
                project.getDescription() == null
                        ? ""
                        : project.getDescription()
        );

        VerticalLayout fields = new VerticalLayout(
                nameField, descriptionField
        );
        fields.setPadding(false);

        dialog.add(fields);

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        Button saveButton = new Button("Сохранить", event -> {
            if (nameField.getValue().isBlank()) {
                nameField.setInvalid(true);
                nameField.setErrorMessage(
                        "Укажите название проекта"
                );
                return;
            }

            try {
                project = service.update(
                        project.getId(),
                        nameField.getValue(),
                        descriptionField.getValue()
                );

                dialog.close();
                refreshContent();

                Notification.show("Проект обновлён");
            } catch (IllegalArgumentException exception) {
                Notification.show(exception.getMessage());
            }
        });

        saveButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white");

        dialog.getFooter().add(
                cancelButton, saveButton
        );

        dialog.open();
    }

    // Открывает подтверждение удаления проекта
    private void openDeleteDialog() {
        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader("Удаление проекта");
        dialog.setText(
                "Вы действительно хотите удалить проект «"
                        + project.getName() + "»?"
        );

        dialog.setCancelable(true);
        dialog.setCancelText("Отмена");

        dialog.setConfirmText("Удалить");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> {
            try {
                service.delete(project.getId());

                Notification.show("Проект удалён");

                UI.getCurrent().navigate(ProjectView.class);
            } catch (RuntimeException exception) {
                Notification.show(
                        "Не удалось удалить проект: "
                                + exception.getMessage()
                );
            }
        });

        dialog.open();
    }
}
