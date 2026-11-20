
package com.devtoolshub.view;

import com.devtoolshub.dto.DashboardStats;
import com.devtoolshub.service.DashboardService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("dashboard")
@PageTitle("Дашборд | DevTools Hub")
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;

    public DashboardView(DashboardService dashboardService) {
        this.dashboardService = dashboardService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        add(createHeader(), createStatsGrid());
    }

    // Создаёт заголовок страницы с кнопкой возврата
    private HorizontalLayout createHeader() {
        Button backButton = new Button(
                new Icon(VaadinIcon.ARROW_LEFT),
                event -> UI.getCurrent().navigate("")
        );
        backButton.addClassName("back-button");

        Icon icon = new Icon(VaadinIcon.DASHBOARD);
        icon.setColor("#eb8f25");

        H1 title = new H1("Дашборд");

        HorizontalLayout header = new HorizontalLayout(
                backButton, icon, title
        );

        header.setWidthFull();
        header.setMaxWidth("1200px");
        header.setAlignItems(Alignment.CENTER);

        return header;
    }

    // Создаёт шесть карточек с актуальной статистикой
    private Div createStatsGrid() {
        DashboardStats stats = dashboardService.getStats();

        Div grid = new Div();
        grid.addClassName("dashboard-stats-grid");

        grid.add(
                createStatCard("Проекты",
                        stats.projects(), VaadinIcon.FOLDER_OPEN),
                createStatCard("Разработчики",
                        stats.developers(), VaadinIcon.USERS),
                createStatCard("Инструменты разработки",
                        stats.tools(), VaadinIcon.TOOLS),
                createStatCard("Языки программирования",
                        stats.languages(), VaadinIcon.CODE),
                createStatCard("Операционные системы",
                        stats.operatingSystems(), VaadinIcon.DESKTOP),
                createStatCard("Категории инструментов",
                        stats.categories(), VaadinIcon.TAGS)
        );

        return grid;
    }

    // Создаёт карточку с названием и числовым показателем
    private Div createStatCard(
            String title,
            long value,
            VaadinIcon iconType
    ) {
        Div card = new Div();
        card.addClassName("dashboard-stat-card");

        Icon icon = new Icon(iconType);
        icon.addClassName("dashboard-stat-icon");

        H3 cardTitle = new H3(title);
        cardTitle.addClassName("dashboard-stat-title");

        Span cardValue = new Span(String.valueOf(value));
        cardValue.addClassName("dashboard-stat-value");

        card.add(icon, cardValue, cardTitle);

        return card;
    }
}
