package com.devtoolshub.view;

import com.devtoolshub.entity.ProgrammingLanguage;
import com.devtoolshub.service.ProgrammingLanguageService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import org.jetbrains.kotlin.it.unimi.dsi.fastutil.bytes.B;

@CssImport("./styles/main.css")
@Route("languages")
public class ProgrammingLanguageView extends VerticalLayout {

    private final ProgrammingLanguageService service;
    private final Grid<ProgrammingLanguage> grid =
            new Grid<>(ProgrammingLanguage.class, false);
    private final TextField searchField = new TextField();

    public  ProgrammingLanguageView(ProgrammingLanguageService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        Icon icon = new Icon(VaadinIcon.CODE);
        icon.setSize("30px");
        icon.getStyle().set("color", "#eb8f25");

        H2 titleText = new H2("Языки программирования");

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

        grid.addColumn(ProgrammingLanguage::getId)
                .setHeader("ID")
                .setWidth("120px");

        grid.addColumn(ProgrammingLanguage::getName)
                .setHeader("Name")
                .setAutoWidth(true);

        grid.addComponentColumn(language ->{
            Button editButton = new Button(
                    "Изменить",
                    VaadinIcon.EDIT.create(),
                    event -> openEditDialog(language)
            );

            Button deleteButton = new Button(
                    "Удалить",
                    VaadinIcon.TRASH.create(),
                    event -> openDeleteDialog(language)
            );

            editButton.addClassName("action-button");
            deleteButton.addClassName("action-button");

            HorizontalLayout actions = new HorizontalLayout(
                    editButton,
                    deleteButton
            );
            actions.setSpacing(true);
            actions.setPadding(false);
            actions.setAlignItems(Alignment.CENTER);

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



            return  actions;
        }).setWidth("340px").setFlexGrow(0);

        grid.setWidthFull();

        Button addButton = new Button(
                "Добавить язык",
                event -> openCreateDialog()
        );
        addButton.addClassName("action-button");
        addButton.getStyle()
                .set("background", "#1f9f4c")
                .set("color", "#ffffff")
                .set("border-radius", "12px")
                .set("font-weight", "600")
                .set("cursor", "pointer");

        searchField.setPlaceholder("Поиск языка...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("320px");

        searchField.setValueChangeMode(ValueChangeMode.LAZY);

        searchField.addValueChangeListener(event -> refreshGrid());

        HorizontalLayout toolbar = new HorizontalLayout(
                addButton,
                searchField
        );

        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        refreshGrid();

        add(header, toolbar ,grid);
    }

    private void refreshGrid() {
        String query = searchField.getValue()
                .trim()
                .toLowerCase();

        grid.setItems(
                service.findAll().stream()
                        .filter(language ->
                                language.getName()
                                        .toLowerCase()
                                        .contains(query)
                        )
                        .toList()
        );
    }

    private void openCreateDialog(){
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Новый язык программирвования");
        dialog.setWidth("420px");
        dialog.setMaxWidth("95vw");
        dialog.setHeight("200px");


        TextField nameField = new TextField("Название");
        nameField.setPlaceholder("Например, Java");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();

        Button saveButton = new Button("Сохранить", event ->{
            try {
                service.create(nameField.getValue());

                refreshGrid();
                dialog.close();

                Notification.show(
                        "Язык программирования добавлен",
                        3000,
                        Notification.Position.BOTTOM_END
                );
            } catch (IllegalArgumentException exception){
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

    private void openEditDialog(ProgrammingLanguage language) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Редактирование языка");
        dialog.setWidth("420px");
        dialog.setHeight("200px");

        TextField nameField = new TextField("Название");
        nameField.setValue(language.getName());
        nameField.setWidthFull();
        nameField.setRequiredIndicatorVisible(true);

        Button saveButton = new Button("Сохранить", event -> {
            try {
                service.update(language.getId(), nameField.getValue());

                refreshGrid();
                dialog.close();

                Notification.show(
                        "Язык программирования обновлён",
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

    private void openDeleteDialog(ProgrammingLanguage language) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Удаление языка");
        dialog.setWidth("420px");
        dialog.setHeight("200px");

        Paragraph message = new Paragraph(
                "Вы действительно хотите удалить язык «"
                        + language.getName() + "»?"
        );

        Button cancelButton = new Button(
                "Отмена",
                event -> dialog.close()
        );

        Button confirmButton = new Button(
                "Удалить",
                event -> {
                    try {
                        service.delete(language.getId());

                        refreshGrid();
                        dialog.close();

                        Notification.show(
                                "Язык программирования удалён",
                                3000,
                                Notification.Position.BOTTOM_END
                        );

                    } catch (RuntimeException exception) {
                        Notification.show(
                                "Не удалось удалить язык: "
                                        + exception.getMessage(),
                                5000,
                                Notification.Position.MIDDLE
                        );
                    }
                }
        );

        confirmButton.addClassName("delete-button");

        dialog.add(message);
        dialog.getFooter().add(cancelButton, confirmButton);

        dialog.open();
    }
}
