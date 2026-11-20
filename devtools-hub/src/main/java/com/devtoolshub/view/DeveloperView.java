
package com.devtoolshub.view;

import com.devtoolshub.entity.Developer;
import com.devtoolshub.service.DeveloperService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.util.Locale;

@CssImport("./styles/main.css")
@Route("developers")
public class DeveloperView extends VerticalLayout {

    private final DeveloperService service;

    private final Grid<Developer> grid =
            new Grid<>(Developer.class, false);

    private final TextField searchField = new TextField();

    private final ComboBox<String> specializationFilter =
            new ComboBox<>();

    public DeveloperView(DeveloperService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        Icon icon = new Icon(VaadinIcon.USERS);
        icon.setSize("30px");
        icon.getStyle().set("color", "#eb8f25");

        H2 titleText = new H2("Разработчики");

        Button backButton = new Button(
                VaadinIcon.ARROW_LEFT.create(),
                event -> UI.getCurrent().navigate("")
        );

        backButton.addClassName("back-button");

        HorizontalLayout header = new HorizontalLayout(
                backButton,
                icon,
                titleText
        );

        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);

        // Настраивает столбцы таблицы
        grid.addColumn(Developer::getId)
                .setHeader("ID")
                .setWidth("120px")
                .setFlexGrow(0);

        grid.addColumn(developer ->
                developer.getLastName() == null
                        ? "<Не указано>"
                        : developer.getLastName()
                )
                .setHeader("Фамилия")
                .setAutoWidth(true);

        grid.addColumn(Developer::getFirstName)
                .setHeader("Имя")
                .setAutoWidth(true);

        grid.addColumn(developer ->
                        developer.getMiddleName() == null
                                ? "<Не указано>"
                                : developer.getMiddleName()
                )
                .setHeader("Отчество")
                .setAutoWidth(true);

        grid.addColumn(Developer::getEmail)
                .setHeader("Email")
                .setAutoWidth(true);

        grid.addColumn(developer ->
                        developer.getSpecialization() == null
                                ? "<Не указано>"
                                : developer.getSpecialization()
                )
                .setHeader("Специализация")
                .setAutoWidth(true);

        grid.addComponentColumn(developer -> {

            Button editButton = new Button(
                    "Изменить",
                    VaadinIcon.EDIT.create(),
                    event -> openEditDialog(developer)
            );

            Button deleteButton = new Button(
                    "Удалить",
                    VaadinIcon.TRASH.create(),
                    event -> openDeleteDialog(developer)
            );

            editButton.addClassName("action-button");
            deleteButton.addClassName("action-button");

            editButton.getStyle()
                    .set("background", "#2563eb")
                    .set("color", "#ffffff")
                    .set("border-radius", "12px")
                    .set("font-weight", "600")
                    .set("cursor", "pointer");

            deleteButton.getStyle()
                    .set("background", "#dc2626")
                    .set("color", "#ffffff")
                    .set("border-radius", "12px")
                    .set("font-weight", "600")
                    .set("cursor", "pointer");

            HorizontalLayout actions = new HorizontalLayout(
                    editButton,
                    deleteButton
            );

            actions.setSpacing(true);
            actions.setPadding(false);
            actions.setWidthFull();
            actions.setAlignItems(Alignment.CENTER);
            actions.setJustifyContentMode(
                    JustifyContentMode.END
            );

            return actions;

        }).setWidth("340px").setFlexGrow(0);

        grid.setWidthFull();

        // Создаёт кнопку добавления разработчика
        Button addButton = new Button(
                "Добавить разработчика",
                event -> openCreateDialog()
        );

        addButton.addClassName("action-button");

        addButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "#ffffff")
                .set("border-radius", "12px")
                .set("font-weight", "600")
                .set("cursor", "pointer");

        // Настраивает поле поиска
        searchField.setPlaceholder("Поиск разработчика...");
        searchField.setPrefixComponent(
                VaadinIcon.SEARCH.create()
        );
        searchField.setClearButtonVisible(true);
        searchField.setWidth("320px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);

        searchField.addValueChangeListener(event ->
                refreshGrid()
        );

        // Настраивает фильтрацию по специализации
        specializationFilter.setPlaceholder("Все специализации");
        specializationFilter.setClearButtonVisible(true);
        specializationFilter.setWidth("240px");

        specializationFilter.addValueChangeListener(event ->
                refreshGrid()
        );

        HorizontalLayout filters = new HorizontalLayout(
                specializationFilter,
                searchField
        );

        filters.setAlignItems(Alignment.CENTER);
        filters.setSpacing(true);

        HorizontalLayout toolbar = new HorizontalLayout(
                addButton,
                filters
        );

        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(
                JustifyContentMode.BETWEEN
        );

        refreshGrid();

        add(header, toolbar, grid);
    }


    // Обновляет таблицу с учётом поиска и специализации
    private void refreshGrid() {
        String query = searchField.getValue()
                .trim()
                .toLowerCase(Locale.ROOT);

        String selectedSpecialization =
                specializationFilter.getValue();

        var developers = service.findAll();

        // Обновляет список доступных специализаций
        String previousSelection = selectedSpecialization;

        var specializations = developers.stream()
                .map(Developer::getSpecialization)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .sorted()
                .toList();

        specializationFilter.setItems(specializations);

        if (previousSelection != null
                && specializations.contains(previousSelection)) {
            specializationFilter.setValue(previousSelection);
        }

        grid.setItems(
                developers.stream()
                        .filter(developer -> {
                            String fullName = String.join(" ",
                                    developer.getLastName() == null
                                            ? "" : developer.getLastName(),
                                    developer.getFirstName(),
                                    developer.getMiddleName() == null
                                            ? "" : developer.getMiddleName()
                            ).toLowerCase(Locale.ROOT);

                            String email = developer.getEmail()
                                    .toLowerCase(Locale.ROOT);

                            String specialization =
                                    developer.getSpecialization() == null
                                            ? ""
                                            : developer.getSpecialization()
                                            .toLowerCase(Locale.ROOT);

                            return fullName.contains(query)
                                    || email.contains(query)
                                    || specialization.contains(query);
                        })
                        .filter(developer ->
                                selectedSpecialization == null
                                        || selectedSpecialization.equals(
                                        developer.getSpecialization()
                                )
                        )
                        .toList()
        );
    }


    // Открывает диалог создания разработчика
    private void openCreateDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Новый разработчик");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        TextField lastNameField = new TextField("Фамилия");
        lastNameField.setWidthFull();

        TextField firstNameField = new TextField("Имя");
        firstNameField.setRequiredIndicatorVisible(true);
        firstNameField.setWidthFull();

        TextField middleNameField = new TextField("Отчество");
        middleNameField.setWidthFull();

        TextField emailField = new TextField("Email");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidthFull();

        TextField specializationField =
                new TextField("Специализация");
        specializationField.setWidthFull();

        Button saveButton = new Button("Сохранить", event -> {
            firstNameField.setInvalid(false);
            emailField.setInvalid(false);

            if (firstNameField.getValue().isBlank()) {
                firstNameField.setInvalid(true);
                firstNameField.setErrorMessage("Укажите имя");
                return;
            }

            if (emailField.getValue().isBlank()) {
                emailField.setInvalid(true);
                emailField.setErrorMessage("Укажите email");
                return;
            }

            try {
                service.create(
                        firstNameField.getValue(),
                        middleNameField.getValue(),
                        lastNameField.getValue(),
                        emailField.getValue(),
                        specializationField.getValue()
                );

                refreshGrid();
                dialog.close();

                Notification.show(
                        "Разработчик добавлен",
                        3000,
                        Notification.Position.BOTTOM_END
                );

            } catch (IllegalArgumentException exception) {
                emailField.setInvalid(true);
                emailField.setErrorMessage(exception.getMessage());
            }
        });

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        dialog.add(
                lastNameField,
                firstNameField,
                middleNameField,
                emailField,
                specializationField
        );

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    // Открывает диалог редактирования разработчика
    private void openEditDialog(Developer developer) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Редактирование разработчика");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        TextField lastNameField = new TextField("Фамилия");
        lastNameField.setValue(
                developer.getLastName() == null
                        ? "" : developer.getLastName()
        );
        lastNameField.setWidthFull();

        TextField firstNameField = new TextField("Имя");
        firstNameField.setValue(developer.getFirstName());
        firstNameField.setRequiredIndicatorVisible(true);
        firstNameField.setWidthFull();

        TextField middleNameField = new TextField("Отчество");
        middleNameField.setValue(
                developer.getMiddleName() == null
                        ? "" : developer.getMiddleName()
        );
        middleNameField.setWidthFull();

        TextField emailField = new TextField("Email");
        emailField.setValue(developer.getEmail());
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidthFull();

        TextField specializationField =
                new TextField("Специализация");
        specializationField.setValue(
                developer.getSpecialization() == null
                        ? "" : developer.getSpecialization()
        );
        specializationField.setWidthFull();

        Button saveButton = new Button("Сохранить", event -> {
            firstNameField.setInvalid(false);
            emailField.setInvalid(false);

            if (firstNameField.getValue().isBlank()) {
                firstNameField.setInvalid(true);
                firstNameField.setErrorMessage("Укажите имя");
                return;
            }

            if (emailField.getValue().isBlank()) {
                emailField.setInvalid(true);
                emailField.setErrorMessage("Укажите email");
                return;
            }

            try {
                service.update(
                        developer.getId(),
                        firstNameField.getValue(),
                        middleNameField.getValue(),
                        lastNameField.getValue(),
                        emailField.getValue(),
                        specializationField.getValue()
                );

                refreshGrid();
                dialog.close();

                Notification.show(
                        "Данные разработчика обновлены",
                        3000,
                        Notification.Position.BOTTOM_END
                );

            } catch (IllegalArgumentException exception) {
                emailField.setInvalid(true);
                emailField.setErrorMessage(exception.getMessage());
            }
        });

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        dialog.add(
                lastNameField,
                firstNameField,
                middleNameField,
                emailField,
                specializationField
        );

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    // Открывает диалог подтверждения удаления
    private void openDeleteDialog(Developer developer) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Удаление разработчика");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        String fullName = (
                (developer.getLastName() == null
                        ? "" : developer.getLastName() + " ")
                        + developer.getFirstName()
        ).trim();

        Paragraph message = new Paragraph(
                "Вы действительно хотите удалить разработчика «"
                        + fullName + "»?"
        );

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        Button confirmButton = new Button(
                "Удалить",
                event -> {
                    try {
                        service.delete(developer.getId());

                        refreshGrid();
                        dialog.close();

                        Notification.show(
                                "Разработчик удалён",
                                3000,
                                Notification.Position.BOTTOM_END
                        );

                    } catch (RuntimeException exception) {
                        Notification.show(
                                "Не удалось удалить разработчика: "
                                        + exception.getMessage(),
                                5000,
                                Notification.Position.MIDDLE
                        );
                    }
                }
        );

        confirmButton.addClassName("delete-button");

        dialog.add(message);
        dialog.getFooter().add(
                cancelButton,
                confirmButton
        );

        dialog.open();
    }
}
