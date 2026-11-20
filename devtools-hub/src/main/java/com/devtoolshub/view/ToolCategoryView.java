
package com.devtoolshub.view;

import com.devtoolshub.entity.ToolCategory;
import com.devtoolshub.service.ToolCategoryService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
@Route("tool-categories")
public class ToolCategoryView extends VerticalLayout {

    private final ToolCategoryService service;

    private final Grid<ToolCategory> grid =
            new Grid<>(ToolCategory.class, false);

    private final TextField searchField = new TextField();

    public ToolCategoryView(ToolCategoryService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Создаёт заголовок страницы
        Icon icon = new Icon(VaadinIcon.TAGS);
        icon.setSize("30px");
        icon.getStyle().set("color", "#eb8f25");

        H2 titleText = new H2("Категории инструментов");

        Button backButton = new Button(
                "На главную",
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

        // Настраивает таблицу категорий
        grid.addColumn(ToolCategory::getId)
                .setHeader("ID")
                .setWidth("120px")
                .setFlexGrow(0);

        grid.addColumn(ToolCategory::getName)
                .setHeader("Название")
                .setAutoWidth(true);

        grid.addComponentColumn(category -> {

            Button editButton = new Button(
                    "Изменить",
                    VaadinIcon.EDIT.create(),
                    event -> openEditDialog(category)
            );

            Button deleteButton = new Button(
                    "Удалить",
                    VaadinIcon.TRASH.create(),
                    event -> openDeleteDialog(category)
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

        // Создаёт кнопку добавления категории
        Button addButton = new Button(
                "Добавить категорию",
                event -> openCreateDialog()
        );

        addButton.addClassName("action-button");

        addButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "#ffffff")
                .set("border-radius", "12px")
                .set("font-weight", "600")
                .set("cursor", "pointer");

        // Настраивает поиск категорий
        searchField.setPlaceholder("Поиск категории...");
        searchField.setPrefixComponent(
                VaadinIcon.SEARCH.create()
        );
        searchField.setClearButtonVisible(true);
        searchField.setWidth("320px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);

        searchField.addValueChangeListener(
                event -> refreshGrid()
        );

        HorizontalLayout toolbar = new HorizontalLayout(
                addButton,
                searchField
        );

        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(
                JustifyContentMode.BETWEEN
        );

        refreshGrid();

        add(header, toolbar, grid);
    }

    // Обновляет таблицу с учётом поискового запроса
    private void refreshGrid() {
        String query = searchField.getValue()
                .trim()
                .toLowerCase(Locale.ROOT);

        grid.setItems(
                service.findAll().stream()
                        .filter(category ->
                                category.getName()
                                        .toLowerCase(Locale.ROOT)
                                        .contains(query)
                        )
                        .toList()
        );
    }

    // Открывает диалог создания категории
    private void openCreateDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Новая категория инструментов");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        TextField nameField = new TextField("Название");
        nameField.setPlaceholder("Например, IDE");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();

        Button saveButton = new Button("Сохранить", event -> {
            try {
                service.create(nameField.getValue());

                refreshGrid();
                dialog.close();

                Notification.show(
                        "Категория добавлена",
                        3000,
                        Notification.Position.BOTTOM_END
                );

            } catch (IllegalArgumentException exception) {
                nameField.setInvalid(true);
                nameField.setErrorMessage(exception.getMessage());
            }
        });

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        dialog.add(nameField);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    // Открывает диалог редактирования категории
    private void openEditDialog(ToolCategory category) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Редактирование категории");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        TextField nameField = new TextField("Название");
        nameField.setValue(category.getName());
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();

        Button saveButton = new Button("Сохранить", event -> {
            try {
                service.update(
                        category.getId(),
                        nameField.getValue()
                );

                refreshGrid();
                dialog.close();

                Notification.show(
                        "Категория обновлена",
                        3000,
                        Notification.Position.BOTTOM_END
                );

            } catch (IllegalArgumentException exception) {
                nameField.setInvalid(true);
                nameField.setErrorMessage(exception.getMessage());
            }
        });

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        dialog.add(nameField);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    // Открывает диалог подтверждения удаления
    private void openDeleteDialog(ToolCategory category) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Удаление категории");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        Paragraph message = new Paragraph(
                "Вы действительно хотите удалить категорию «"
                        + category.getName() + "»?"
        );

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        Button confirmButton = new Button(
                "Удалить",
                event -> {
                    try {
                        service.delete(category.getId());

                        refreshGrid();
                        dialog.close();

                        Notification.show(
                                "Категория удалена",
                                3000,
                                Notification.Position.BOTTOM_END
                        );

                    } catch (RuntimeException exception) {
                        Notification.show(
                                "Не удалось удалить категорию: "
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
