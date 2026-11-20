
package com.devtoolshub.view;

import com.devtoolshub.entity.SoftwareProject;
import com.devtoolshub.service.SoftwareProjectService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;

@Route("projects")
@PageTitle("Проекты | DevTools Hub")
public class ProjectView extends VerticalLayout {

    private final SoftwareProjectService service;

    private final Div projectGrid = new Div();
    private final TextField searchField = new TextField();

    public ProjectView(SoftwareProjectService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle()
                .set("max-width", "1400px")
                .set("margin", "0 auto");

        add(createHeader(), createToolbar(), projectGrid);

        configureProjectGrid();
        refreshProjects();
    }

    // Создаёт заголовок раздела
    private HorizontalLayout createHeader() {
        Button backButton = new Button(
                new Icon(VaadinIcon.ARROW_LEFT),
                event -> UI.getCurrent().navigate("")
        );
        backButton.addClassName("back-button");

        Icon icon = new Icon(VaadinIcon.FOLDER_OPEN);
        icon.setColor("#eb8f25");

        H2 title = new H2("Проекты");
        title.getStyle().set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(
                backButton, icon, title
        );
        header.setAlignItems(Alignment.CENTER);
        header.setWidthFull();

        return header;
    }

    // Создаёт панель добавления и поиска проектов
    private HorizontalLayout createToolbar() {
        Button addButton = new Button(
                "Добавить проект",
                new Icon(VaadinIcon.PLUS),
                event -> openCreateDialog()
        );

        addButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "white")
                .set("border-radius", "8px");

        searchField.setPlaceholder("Поиск проекта...");
        searchField.setPrefixComponent(
                new Icon(VaadinIcon.SEARCH)
        );
        searchField.setClearButtonVisible(true);
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.addValueChangeListener(
                event -> refreshProjects()
        );

        HorizontalLayout toolbar = new HorizontalLayout(
                addButton, searchField
        );

        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(
                FlexComponent.JustifyContentMode.BETWEEN
        );
        toolbar.getStyle().set("flex-wrap", "wrap");

        return toolbar;
    }

    // Настраивает адаптивную сетку карточек
    private void configureProjectGrid() {
        projectGrid.setWidthFull();
        projectGrid.addClassName("project-grid");
    }

    // Обновляет список проектов с учётом поиска
    private void refreshProjects() {
        projectGrid.removeAll();

        String search = searchField.getValue()
                .trim()
                .toLowerCase(Locale.ROOT);

        List<SoftwareProject> projects = service.findAll()
                .stream()
                .filter(project -> {
                    String name = project.getName()
                            .toLowerCase(Locale.ROOT);

                    String description = project.getDescription() == null
                            ? ""
                            : project.getDescription()
                            .toLowerCase(Locale.ROOT);

                    return name.contains(search)
                            || description.contains(search);
                })
                .toList();

        if (projects.isEmpty()) {
            Paragraph emptyMessage = new Paragraph(
                    "Проекты не найдены"
            );
            emptyMessage.getStyle()
                    .set("color", "#6b7280");

            projectGrid.add(emptyMessage);
            return;
        }

        for (SoftwareProject project : projects) {
            projectGrid.add(createProjectCard(project));
        }
    }

    // Создаёт карточку программного проекта
    private Div createProjectCard(SoftwareProject project) {
        Div card = new Div();
        card.addClassName("project-card");

        Icon icon = new Icon(VaadinIcon.FOLDER_OPEN);
        icon.setColor("#eb8f25");

        H3 name = new H3(project.getName());
        name.getStyle().set("margin", "0");

        HorizontalLayout cardHeader = new HorizontalLayout(
                icon, name
        );
        cardHeader.setAlignItems(Alignment.CENTER);

        Paragraph description = new Paragraph(
                project.getDescription() == null
                        || project.getDescription().isBlank()
                        ? "Описание отсутствует"
                        : project.getDescription()
        );
        description.addClassName("project-description");

        Div details = new Div();
        details.setText("Подробнее о проекте →");
        details.addClassName("project-details-link");

        card.add(cardHeader, description, details);

        card.addClickListener(event ->
                UI.getCurrent().navigate(
                        "projects/" + project.getId()
                )
        );

        return card;
    }

    // Открывает диалог создания проекта
    private void openCreateDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Добавить проект");
        dialog.setWidth("480px");

        TextField nameField = new TextField("Название");
        nameField.setWidthFull();
        nameField.setRequiredIndicatorVisible(true);
        nameField.setMaxLength(200);

        TextArea descriptionField = new TextArea("Описание");
        descriptionField.setWidthFull();
        descriptionField.setMinHeight("130px");

        VerticalLayout content = new VerticalLayout(
                nameField, descriptionField
        );
        content.setPadding(false);

        dialog.add(content);

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        Button saveButton = new Button("Создать", event -> {
            if (nameField.isEmpty()
                    || nameField.getValue().isBlank()) {
                nameField.setInvalid(true);
                nameField.setErrorMessage(
                        "Укажите название проекта"
                );
                return;
            }

            try {
                SoftwareProject created = service.create(
                        nameField.getValue(),
                        descriptionField.getValue()
                );

                dialog.close();
                refreshProjects();

                Notification.show("Проект создан");

                UI.getCurrent().navigate(
                        "projects/" + created.getId()
                );
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
}
