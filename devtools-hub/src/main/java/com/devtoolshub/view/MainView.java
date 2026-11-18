package com.devtoolshub.view;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

@CssImport("./styles/main.css")
@Route("")
public class MainView extends  VerticalLayout {
    public  MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        Div header = new Div();
        header.addClassName("main-header");

        Div accent = new Div();
        //accent.addClassName("header-accent");

        H1 title = new H1();
        title.add("Добро пожаловать в ");

        Span brand = new Span("DevTools");
        brand.addClassName("brand-gradient");

        title.add(brand, new Span(" Hub"));
        title.addClassName("main-title");

        Paragraph subtitle = new Paragraph(
                "Управление инструментами и проектами разработки"
        );
        subtitle.addClassName("main-subtitle");

        header.add(accent, title, subtitle);

        add(header);

        Div grid = new Div();

        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(3, minmax(0,1fr))")
                .set("gap", "28px")
                .set("width", "100%")
                .set("max-width", "1450px")
                .set("margin-top", "40px");

        grid.add(
                createNavigationCard(
                        "Проекты",
                        "Управление программными проектами",
                        VaadinIcon.FOLDER_OPEN
                ),
                createNavigationCard(
                        "Языки программирвоания",
                        "Справочник языков программирования",
                        VaadinIcon.CODE
                ),
                createNavigationCard(
                        "Инструменты разработки",
                        "Управление инструментами разработки",
                        VaadinIcon.TOOLS
                ),
                createNavigationCard(
                        "Разработчики",
                        "Управление разработчиками",
                        VaadinIcon.USERS
                ),
                createNavigationCard(
                        "Операционные системы",
                        "Справочник операционных систем",
                        VaadinIcon.DESKTOP
                ),
                createNavigationCard(
                        "Категории инструментов",
                        "Управление категориями инструментов",
                        VaadinIcon.TAGS
                )
        );

        add(grid);

        Div dashboardButton = new Div();
        dashboardButton.addClassName("dashboard-button");

        Icon dashboardIcon = VaadinIcon.CHART.create();
        dashboardIcon.setSize("26px");

        Span dashboardText = new Span("Перейти к дашборду");

        Icon arrowIcon = VaadinIcon.ARROW_RIGHT.create();
        arrowIcon.setSize("24px");

        dashboardButton.add(
                dashboardIcon,
                dashboardText,
                arrowIcon
        );

        add(dashboardButton);

    }
    private VerticalLayout createNavigationCard(
            String title,
            String description,
            VaadinIcon iconName
    ){
        Icon icon = new Icon(iconName);
        icon.setSize("44px");
        icon.getStyle().set("color", "#6366f1");

        H3 cardTitle = new H3(title);
        Paragraph cardDescription = new Paragraph(description);

        VerticalLayout card = new VerticalLayout(
                icon,
                cardTitle,
                cardDescription
        );

        card.setPadding(true);
        card.setSpacing(true);
        card.setWidthFull();

        card.getStyle()
                .set("background", "#ffffff")
                .set("border-radius", "20px")
                .set("padding", "36px")
                .set("box-shadow", "0 6px 24px rgba(0,0,0,0.07)")
                .set("border", "1px solid #e5e7eb")
                .set("min-height", "230px")
                .set("box-sizing", "border-box");

        card.addClassName("navigation-card");
        return  card;
    }
}
