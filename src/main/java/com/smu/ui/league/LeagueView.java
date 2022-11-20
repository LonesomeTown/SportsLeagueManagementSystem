package com.smu.ui.league;

import com.smu.dto.*;
import com.smu.service.LeagueService;
import com.smu.service.SeasonService;
import com.smu.service.TeamService;
import com.smu.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("League | Project Group8")
public class LeagueView extends VerticalLayout {
    Grid<LeagueVo> grid = new Grid<>(LeagueVo.class);
    TextField filterText = new TextField();
    LeagueForm form;
    InitializeDialog dialog;
    LeagueService leagueService;
    TeamService teamService;
    SeasonService seasonService;

    public LeagueView(LeagueService leagueService, TeamService teamService, SeasonService seasonService) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.seasonService = seasonService;
        addClassName("league-view");
        setSizeFull();
        configureGrid();
        configureForm();
        closeEditor();
        add(getToolbar(), getContent());

    }

    private void configureGrid() {
        grid.addClassNames("league-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.addColumn(LeagueVo::getCommissionerName).setHeader("Commissioner Name");
        grid.addColumn(LeagueVo::getCommissionerSsn).setHeader("Commissioner SSN");
        grid.addColumn(LeagueVo::getSeasonsNum).setHeader("Number of Seasons");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList();

        grid.asSingleSelect().addValueChangeListener(event -> {
                    League league = new League();
                    BeanUtils.copyProperties(event.getValue(), league);
                    editLeague(league);
                }
        );
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add league");
        addContactButton.addClickListener(click -> addLeague());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new LeagueForm();
        form.setWidth("25em");
        form.addListener(LeagueForm.SaveEvent.class, this::saveLeague);
        form.addListener(LeagueForm.DeleteEvent.class, this::deleteLeague);
        form.addListener(LeagueForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureDialog() {
        dialog = new InitializeDialog();
        dialog.addListener(InitializeDialog.SaveEvent.class, this::saveInitializeVo);
        dialog.addListener(InitializeDialog.CloseEvent.class, e -> closeInitializeDialog());
    }

    private void closeEditor() {
        form.setLeague(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void editLeague(League league) {
        if (league == null) {
            closeEditor();
        } else {
            form.setLeague(league);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addLeague() {
        grid.asSingleSelect().clear();
        League league = new League();
        league.setName(filterText.getValue());
        editLeague(league);
    }

    private void saveLeague(LeagueForm.SaveEvent event) {
        leagueService.saveLeague(event.getLeague());
        Long teamsNum = teamService.countTeamsByLeague(event.getLeague().getName());
        if (null == teamsNum || teamsNum <= 0) {
            //open initialize team and season dialog
            openInitializeDialog();
        } else {
            closeEditor();
            updateList();
        }
    }

    private void deleteLeague(LeagueForm.DeleteEvent event) {
        leagueService.deleteLeague(event.getLeague());
        updateList();
        closeEditor();
    }

    private void saveInitializeVo(InitializeDialog.SaveEvent event) {
        dialog.setInitializeVo(event.getInitializeVo());
        InitializeVo initializeVo = event.getInitializeVo();
        if (null != initializeVo) {
            Team team = new Team();
            team.setName(initializeVo.getName());
            team.setCity(initializeVo.getCity());
            team.setField(initializeVo.getField());
            team.setLeagueName(form.league.getName());
            teamService.saveTeam(team);
            Season season = new Season();
            season.setStartDate(initializeVo.getStartDate());
            season.setEndDate(initializeVo.getEndDate());
            season.setGamesNum(initializeVo.getGamesNum());
            seasonService.saveSeason(season);
        }
        closeInitializeDialog();
        closeEditor();
        updateList();
    }

    private void openInitializeDialog() {
        configureDialog();
        dialog.open();
    }

    private void closeInitializeDialog() {
        dialog.close();
    }

    private void updateList() {
        List<LeagueVo> leagueVos = new ArrayList<>();
        List<League> allLeagues = leagueService.findAllLeagues(filterText.getValue());
        for (League allLeague : allLeagues) {
            LeagueVo leagueVo = new LeagueVo();
            BeanUtils.copyProperties(allLeague, leagueVo);
            List<Season> seasonsByLeagueName = seasonService.findSeasonsByLeagueName(allLeague.getName());
            if (CollectionUtils.isEmpty(seasonsByLeagueName)) {
                leagueVo.setSeasonsNum(0);
            } else {
                leagueVo.setSeasonsNum(seasonsByLeagueName.size());
            }
            leagueVos.add(leagueVo);
        }
        grid.setItems(leagueVos);
    }
}