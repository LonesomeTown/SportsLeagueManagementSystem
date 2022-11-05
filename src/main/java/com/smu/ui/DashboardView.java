package com.smu.ui;

import com.smu.service.LeagueService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "dashboard", layout = MainLayout.class) 
@PageTitle("Dashboard | League Management System - Group8")
public class DashboardView extends VerticalLayout {
    private final LeagueService service;

    public DashboardView(LeagueService service) {
        this.service = service;
        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER); 
        add(getContactStats(), getCompaniesChart());
    }

    private Component getContactStats() {
        Span stats = new Span(service.countLeagues() + " Leagues");
        stats.addClassNames("text-xl", "mt-m");
        return stats;
    }

    private Chart getCompaniesChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
//        service.findAllLeagues().forEach(company ->
//            dataSeries.add(new DataSeriesItem(company.getName(),0)));
        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }
}