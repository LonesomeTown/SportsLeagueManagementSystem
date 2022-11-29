package com.smu.ui;

import com.smu.dto.*;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | League Management System - Group8")
public class DashboardView extends VerticalLayout {
    Grid<TeamRatingVo> grid = new Grid<>(TeamRatingVo.class, false);
    ComboBox<String> leagueBox = new ComboBox<>();
    ComboBox<String> seasonBox = new ComboBox<>();
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

        add(configureGrid());
    }

    private Component getLeagueStats() {
        List<League> allLeagues = leagueService.findAllLeagues("");
        Span stats = new Span(allLeagues.size() + " Leagues");
        stats.addClassNames("text-xl", "mt-m");
        return stats;
    }

    private Chart getLeagueChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        leagueService.findAllLeagues("").forEach(league ->
                dataSeries.add(new DataSeriesItem(league.getName(), teamService.findTeamNamesByLeagueName(league.getName()).size())));
        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }

    private VerticalLayout configureGrid() {
        List<League> allLeagues = leagueService.findAllLeagues("");
        leagueBox.setItems(allLeagues.stream().map(League::getName).collect(Collectors.toList()));
        leagueBox.setPlaceholder("League name...");
        leagueBox.setClearButtonVisible(true);
        leagueBox.addValueChangeListener(e -> {
            this.updateGridList();
//            List<Season> seasonsByLeagueName = seasonService.findSeasonsByLeagueName(e.getValue());
//            List<String> seasonDuration = new ArrayList<>();
//            for (Season season : seasonsByLeagueName) {
//                String startDate = season.getStartDate().format(DateTimeFormatter.BASIC_ISO_DATE);
//                String endDate = season.getEndDate().format(DateTimeFormatter.BASIC_ISO_DATE);
//                String duration = startDate + "-" + endDate;
//                seasonDuration.add(duration);
//            }
//            seasonBox.setItems(seasonDuration);
        });

//        seasonBox.setPlaceholder("Season");
//        seasonBox.setClearButtonVisible(true);
//        seasonBox.addValueChangeListener(e -> this.updateGridList());

        grid.addClassNames("league-lower-grid");
        grid.addColumn(TeamRatingVo::getTeamName).setHeader("Team");
        grid.addColumn(TeamRatingVo::getRating).setHeader("Rating");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateGridList();

        HorizontalLayout horizontalLayout = new HorizontalLayout(leagueBox);

        VerticalLayout layout = new VerticalLayout(horizontalLayout, grid);
        layout.addClassNames("grid-content");
        layout.setSizeFull();
        return layout;
    }

    private void updateGridList() {

        Season season = seasonService.findSeasonByCurrentDateAndLeague(LocalDate.now(), leagueBox.getValue());
        if (null == season) {
            return;
        }
        Map<String, String> winnerAndLoserMap = new HashMap<>();
        Map<String, Double> teamAndRatingMap = new HashMap<>();
        List<Game> games = gameService.findGamesBySeason(season.getId());
        for (Game game : games) {
            Team homeTeam = teamService.findByTeamName(game.getHomeTeamName());
            Team visitingTeam = teamService.findByTeamName(game.getVisitingTeamName());
            if (null == homeTeam || null == visitingTeam || null == game.getHomeScore() || null == game.getVisitingScore()) {
                continue;
            }
            if (game.getHomeScore() > game.getVisitingScore() && homeTeam.getRating() <= visitingTeam.getRating()) {
                winnerAndLoserMap.put(game.getHomeTeamName(), game.getVisitingTeamName());

            } else if (game.getHomeScore() < game.getVisitingScore() && homeTeam.getRating() >= visitingTeam.getRating()) {
                winnerAndLoserMap.put(game.getVisitingTeamName(), game.getHomeTeamName());
            }
            teamAndRatingMap.put(game.getHomeTeamName(), homeTeam.getRating());
            teamAndRatingMap.put(game.getVisitingTeamName(), visitingTeam.getRating());
        }


        long winnerNums = winnerAndLoserMap.keySet().stream().distinct().count();
        List<List<TeamRatingVo>> allVos = new ArrayList<>();
        for (int i = 0; i < winnerNums; i++) {
            //start with different winner, find the longest queue
            List<TeamRatingVo> teamRatingVos = new ArrayList<>();
            String winner = (String) winnerAndLoserMap.keySet().toArray()[i];
            this.buildTeamListByRecursive(teamRatingVos, winnerAndLoserMap, teamAndRatingMap, winner);
            allVos.add(teamRatingVos);
        }

        allVos.stream().max(Comparator.comparing(List::size)).ifPresent(teamRatingVos -> grid.setItems(teamRatingVos));

    }

    private void buildTeamListByRecursive(List<TeamRatingVo> teamRatingVos, Map<String, String> winnerAndLoserMap, Map<String, Double> teamAndRatingMap, String winner) {
        TeamRatingVo teamRatingVo = new TeamRatingVo();
        teamRatingVo.setTeamName(winner);
        teamRatingVo.setRating(teamAndRatingMap.get(winner));
        teamRatingVos.add(teamRatingVo);
        String loser = winnerAndLoserMap.get(winner);
        if (null != loser) {
            this.buildTeamListByRecursive(teamRatingVos, winnerAndLoserMap, teamAndRatingMap, loser);
        }
    }
}