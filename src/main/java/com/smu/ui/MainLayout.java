package com.smu.ui;

import com.smu.ui.league.LeagueView;
import com.smu.ui.season.SeasonView;
import com.smu.ui.team.TeamView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("League Management System - Group 8");
        logo.addClassNames("text-l", "m-m");

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);

    }

    private void createDrawer() {
        RouterLink leagueLink = new RouterLink("League Management", LeagueView.class);
        leagueLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink teamLink = new RouterLink("Team Management", TeamView.class);
        teamLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink seasonLink = new RouterLink("Season Management", SeasonView.class);
        teamLink.setHighlightCondition(HighlightConditions.sameLocation());


        addToDrawer(new VerticalLayout(
                leagueLink,
                teamLink,
                seasonLink,
                new RouterLink("Dashboard", DashboardView.class)
        ));
    }
}