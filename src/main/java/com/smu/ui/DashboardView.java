package com.smu.ui;

import com.smu.dto.League;
import com.smu.dto.Season;
import com.smu.dto.TeamRatingVo;
import com.smu.service.GameService;
import com.smu.service.LeagueService;
import com.smu.service.SeasonService;
import com.smu.service.TeamService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | League Management System - Group8")
public class DashboardView extends VerticalLayout {
    Grid<TeamRatingVo> grid = new Grid<>(TeamRatingVo.class, false);
    ComboBox<String> leagueBox = new ComboBox<>();
    ComboBox<String> teamBox = new ComboBox<>();
    private final TeamService teamService;
    private final LeagueService leagueService;
    private final GameService gameService;
    private final SeasonService seasonService;

    public DashboardView(TeamService teamService, LeagueService leagueService, GameService gameService, SeasonService seasonService) {
        this.teamService = teamService;
        this.leagueService = leagueService;
        this.gameService = gameService;
        this.seasonService = seasonService;
        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(getContactStats(), getCompaniesChart(), configureGrid());
    }

    private Component getContactStats() {
        Span stats = new Span(teamService.findAllTeamsName().size() + " Teams");
        stats.addClassNames("text-xl", "mt-m");
        return stats;
    }

    private Chart getCompaniesChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        teamService.findAllTeams("").forEach(team ->
                dataSeries.add(new DataSeriesItem(team.getLeagueName(), 0)));
        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }

    private VerticalLayout configureGrid() {
        List<League> allLeagues = leagueService.findAllLeagues("");
        leagueBox.setItems(allLeagues.stream().map(League::getName).collect(Collectors.toList()));
        leagueBox.setPlaceholder("League name...");
        leagueBox.setClearButtonVisible(true);
        leagueBox.addValueChangeListener(e -> teamBox.setItems(teamService.findTeamNamesByLeagueName(e.getValue())));

        teamBox.setPlaceholder("Team name");
        teamBox.setClearButtonVisible(true);
        teamBox.addValueChangeListener(e -> this.updateGridList());

        grid.addClassNames("league-lower-grid");
        grid.setSizeFull();
        grid.addColumn(TeamRatingVo::getTeamName).setHeader("Team");
        grid.addColumn(TeamRatingVo::getRating).setHeader("Rating");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateGridList();

        VerticalLayout layout = new VerticalLayout(leagueBox, teamBox, grid);
        layout.addClassNames("grid-content");
        layout.setSizeFull();
        return layout;
    }

    private void updateGridList() {
        List<String> teamNamesByLeagueName = teamService.findTeamNamesByLeagueName(leagueBox.getValue());
        Season seasonByCurrentDateAndLeague = seasonService.findSeasonByCurrentDateAndLeague(LocalDate.now(), leagueBox.getValue());
//        gameService.findGamesBySeasonAndTeam()
    }
}