
package com.devtoolshub.view;

import com.devtoolshub.entity.DevelopmentTool;
import com.devtoolshub.entity.ToolCategory;
import com.devtoolshub.service.DevelopmentToolService;
import com.devtoolshub.service.ToolCategoryService;

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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;

@CssImport("./styles/main.css")
@Route("development-tools")
public class DevelopmentToolView extends VerticalLayout {

    private final DevelopmentToolService service;
    private final ToolCategoryService categoryService;

    private final Grid<DevelopmentTool> grid =
            new Grid<>(DevelopmentTool.class, false);

    private final TextField searchField = new TextField();

    private final ComboBox<ToolCategory> categoryFilter =
            new ComboBox<>();

    public DevelopmentToolView(
            DevelopmentToolService service,
            ToolCategoryService categoryService
    ) {
        this.service = service;
        this.categoryService = categoryService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        Icon icon = new Icon(VaadinIcon.TOOLS);
        icon.setSize("30px");
        icon.getStyle().set("color", "#eb8f25");

        H2 titleText = new H2("Инструменты разработки");

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

        // Настраивает столбцы таблицы
        grid.addColumn(DevelopmentTool::getId)
                .setHeader("ID")
                .setWidth("120px")
                .setFlexGrow(0);

        grid.addColumn(DevelopmentTool::getName)
                .setHeader("Название")
                .setAutoWidth(true);

        grid.addColumn(tool ->
                        tool.getCategory() == null
                                ? "Без категории"
                                : tool.getCategory().getName()
                )
                .setHeader("Категория")
                .setAutoWidth(true);

        grid.addColumn(tool ->
                        tool.getVersion() == null
                                ? ""
                                : tool.getVersion()
                )
                .setHeader("Версия")
                .setAutoWidth(true);

        // Отображает описание инструмента
        grid.addColumn(tool ->
                        tool.getDescription() == null
                                ? ""
                                : tool.getDescription()
                )
                .setHeader("Описание")
                .setWidth("300px")
                .setFlexGrow(0);

        // Отображает сайт инструмента
        grid.addColumn(tool ->
                        tool.getWebsiteUrl() == null
                                ? ""
                                : tool.getWebsiteUrl()
                )
                .setHeader("Сайт")
                .setWidth("250px")
                .setFlexGrow(0);

        grid.addComponentColumn(tool -> {

            Button editButton = new Button(
                    "Изменить",
                    VaadinIcon.EDIT.create(),
                    event -> openEditDialog(tool)
            );

            Button deleteButton = new Button(
                    "Удалить",
                    VaadinIcon.TRASH.create(),
                    event -> openDeleteDialog(tool)
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

        // Создаёт кнопку добавления инструмента
        Button addButton = new Button(
                "Добавить инструмент",
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
        searchField.setPlaceholder("Поиск инструмента...");
        searchField.setPrefixComponent(
                VaadinIcon.SEARCH.create()
        );
        searchField.setClearButtonVisible(true);
        searchField.setWidth("320px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);

        searchField.addValueChangeListener(
                event -> refreshGrid()
        );

        // Настраивает фильтрацию по категориям
        categoryFilter.setPlaceholder("Все категории");
        categoryFilter.setItems(categoryService.findAll());
        categoryFilter.setItemLabelGenerator(ToolCategory::getName);

        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setWidth("240px");

        categoryFilter.addValueChangeListener(event ->
                refreshGrid()
        );

        HorizontalLayout filters = new HorizontalLayout(
                categoryFilter,
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

    // Обновляет таблицу с учётом поискового запроса

    // Обновляет таблицу с учётом поиска и категории
    private void refreshGrid() {
        String query = searchField.getValue()
                .trim()
                .toLowerCase(Locale.ROOT);

        ToolCategory selectedCategory = categoryFilter.getValue();

        grid.setItems(
                service.findAll().stream()
                        .filter(tool -> {
                            String name = tool.getName()
                                    .toLowerCase(Locale.ROOT);

                            String categoryName =
                                    tool.getCategory() == null
                                            ? ""
                                            : tool.getCategory()
                                            .getName()
                                            .toLowerCase(Locale.ROOT);

                            return name.contains(query)
                                    || categoryName.contains(query);
                        })
                        .filter(tool ->
                                selectedCategory == null
                                        || (tool.getCategory() != null
                                        && tool.getCategory().getId()
                                        .equals(selectedCategory.getId()))
                        )
                        .toList()
        );
    }


    // Открывает диалог создания инструмента разработки
    private void openCreateDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Новый инструмент разработки");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        TextField nameField = new TextField("Название");
        nameField.setPlaceholder("Например, IntelliJ IDEA");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();

        ComboBox<ToolCategory> categoryField =
                new ComboBox<>("Категория");

        categoryField.setItems(categoryService.findAll());
        categoryField.setItemLabelGenerator(ToolCategory::getName);
        categoryField.setRequiredIndicatorVisible(true);
        categoryField.setWidthFull();

        TextArea descriptionField = new TextArea("Описание");
        descriptionField.setWidthFull();

        TextField websiteField = new TextField("Сайт");
        websiteField.setWidthFull();

        TextField versionField = new TextField("Версия");
        versionField.setWidthFull();

        Button saveButton = new Button("Сохранить", event -> {
            nameField.setInvalid(false);
            categoryField.setInvalid(false);

            if (categoryField.getValue() == null) {
                categoryField.setInvalid(true);
                categoryField.setErrorMessage("Выберите категорию");
                return;
            }

            try {
                service.create(
                        nameField.getValue(),
                        categoryField.getValue().getId(),
                        descriptionField.getValue(),
                        websiteField.getValue(),
                        versionField.getValue()
                );

                refreshGrid();
                dialog.close();

                Notification.show(
                        "Инструмент разработки добавлен",
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

        dialog.add(
                nameField,
                categoryField,
                descriptionField,
                websiteField,
                versionField
        );

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    // Открывает диалог редактирования инструмента разработки
    private void openEditDialog(DevelopmentTool tool) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Редактирование инструмента");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        TextField nameField = new TextField("Название");
        nameField.setValue(tool.getName());
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();

        ComboBox<ToolCategory> categoryField =
                new ComboBox<>("Категория");

        categoryField.setItemLabelGenerator(ToolCategory::getName);
        categoryField.setRequiredIndicatorVisible(true);
        categoryField.setWidthFull();

        List<ToolCategory> categories = categoryService.findAll();
        categoryField.setItems(categories);

        categories.stream()
                .filter(category ->
                        tool.getCategory() != null &&
                                category.getId().equals(
                                        tool.getCategory().getId()
                                )
                )
                .findFirst()
                .ifPresent(categoryField::setValue);

        TextArea descriptionField = new TextArea("Описание");
        descriptionField.setValue(
                tool.getDescription() == null
                        ? ""
                        : tool.getDescription()
        );
        descriptionField.setWidthFull();

        TextField websiteField = new TextField("Сайт");
        websiteField.setValue(
                tool.getWebsiteUrl() == null
                        ? ""
                        : tool.getWebsiteUrl()
        );
        websiteField.setWidthFull();

        TextField versionField = new TextField("Версия");
        versionField.setValue(
                tool.getVersion() == null
                        ? ""
                        : tool.getVersion()
        );
        versionField.setWidthFull();

        Button saveButton = new Button("Сохранить", event -> {
            nameField.setInvalid(false);
            categoryField.setInvalid(false);

            if (categoryField.getValue() == null) {
                categoryField.setInvalid(true);
                categoryField.setErrorMessage("Выберите категорию");
                return;
            }

            try {
                service.update(
                        tool.getId(),
                        nameField.getValue(),
                        categoryField.getValue().getId(),
                        descriptionField.getValue(),
                        websiteField.getValue(),
                        versionField.getValue()
                );

                refreshGrid();
                dialog.close();

                Notification.show(
                        "Инструмент разработки обновлён",
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

        dialog.add(
                nameField,
                categoryField,
                descriptionField,
                websiteField,
                versionField
        );

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    // Открывает диалог подтверждения удаления
    private void openDeleteDialog(DevelopmentTool tool) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Удаление инструмента");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");

        Paragraph message = new Paragraph(
                "Вы действительно хотите удалить инструмент «"
                        + tool.getName() + "»?"
        );

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        Button confirmButton = new Button(
                "Удалить",
                event -> {
                    try {
                        service.delete(tool.getId());

                        refreshGrid();
                        dialog.close();

                        Notification.show(
                                "Инструмент разработки удалён",
                                3000,
                                Notification.Position.BOTTOM_END
                        );

                    } catch (RuntimeException exception) {
                        Notification.show(
                                "Не удалось удалить инструмент: "
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
