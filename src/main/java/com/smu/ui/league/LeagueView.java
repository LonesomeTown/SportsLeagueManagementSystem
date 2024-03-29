package com.smu.ui.league;

import com.smu.dto.*;
import com.smu.service.GameService;
import com.smu.service.LeagueService;
import com.smu.service.SeasonService;
import com.smu.service.TeamService;
import com.smu.ui.MainLayout;
import com.smu.ui.NotificationError;
import com.smu.ui.game.GameRecordsDialog;
import com.smu.ui.game.GamesDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "", layout = MainLayout.class)
@PageTitle("League | Project Group8")
public class LeagueView extends VerticalLayout {
    Grid<LeagueVo> upperGrid = new Grid<>(LeagueVo.class);
    Grid<ChampionVo> lowerGrid = new Grid<>(ChampionVo.class, false);
    TextField filterText = new TextField();
    ComboBox<String> leagueNameComboBox = new ComboBox<>();
    LeagueForm form;
    InitializeDialog dialog;
    GameRecordsDialog gameDialog;
    LeagueService leagueService;
    TeamService teamService;
    SeasonService seasonService;
    GameService gameService;

    public LeagueView(LeagueService leagueService, TeamService teamService, SeasonService seasonService, GameService gameService) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.seasonService = seasonService;
        this.gameService = gameService;
        addClassName("league-view");
        setSizeFull();
        configureUpperGrid();
        configureForm();
        closeEditor();
        add(getUpperToolbar(), getContent(), getLowerToolbar(), configureLowerGrid());

    }

    private void configureUpperGrid() {
        upperGrid.addClassNames("league-grid");
        upperGrid.setSizeFull();
        upperGrid.setColumns("name");
        upperGrid.addColumn(LeagueVo::getCommissionerName).setHeader("Commissioner Name");
        upperGrid.addColumn(LeagueVo::getCommissionerSsn).setHeader("Commissioner SSN");
        upperGrid.addColumn(LeagueVo::getSeasonsNum).setHeader("Number of Seasons");
        upperGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList();

        upperGrid.asSingleSelect().addValueChangeListener(event -> {
                    League league = new League();
                    if (null != event.getValue()) {
                        BeanUtils.copyProperties(event.getValue(), league);
                    }
                    editLeague(league);
                }
        );
    }

    private HorizontalLayout configureLowerGrid() {
        lowerGrid.addClassNames("league-lower-grid");
        lowerGrid.setSizeFull();
        lowerGrid.addColumn(ChampionVo::getTeamName).setHeader("Champion Team");
        lowerGrid.addColumn(ChampionVo::getSeasonDuration).setHeader("Season Duration");
        lowerGrid.addColumn(ChampionVo::getPoints).setHeader("Total Points");
        lowerGrid.addComponentColumn(t -> createInlineButtonComponent(t.getSeasonId(), t.getTeamName()));
        lowerGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        lowerGrid.setDetailsVisibleOnClick(false);
        this.updateLowerGridList();

        HorizontalLayout layout = new HorizontalLayout(lowerGrid);
        layout.addClassNames("lower-content");
        layout.setSizeFull();
        return layout;
    }

    private Button createInlineButtonComponent(ObjectId seasonId, String teamName) {
        Button tertiaryInlineButton = new Button("Game Records");
        tertiaryInlineButton
                .addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        tertiaryInlineButton.addClickListener(e -> configureGameDialog(seasonId, teamName));
        return tertiaryInlineButton;
    }

    private void configureGameDialog(ObjectId seasonId, String teamName) {
        gameDialog = new GameRecordsDialog(gameService, seasonId, teamName);
        gameDialog.addListener(GameRecordsDialog.CloseEvent.class, e -> gameDialog.close());
        gameDialog.open();
    }


    private HorizontalLayout getUpperToolbar() {
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

    private HorizontalLayout getLowerToolbar() {
        List<League> allLeagues = leagueService.findAllLeagues("");
        leagueNameComboBox.setItems(allLeagues.stream().map(League::getName).collect(Collectors.toList()));
        leagueNameComboBox.setPlaceholder("Search champions...");
        leagueNameComboBox.setClearButtonVisible(true);
        leagueNameComboBox.addValueChangeListener(e -> this.updateLowerGridList());

        HorizontalLayout toolbar = new HorizontalLayout(leagueNameComboBox);
        toolbar.addClassName("lower-toolbar");
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(upperGrid, form);
        content.setFlexGrow(2, upperGrid);
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
        upperGrid.asSingleSelect().clear();
        League league = new League();
        league.setName(filterText.getValue());
        editLeague(league);
    }

    private void saveLeague(LeagueForm.SaveEvent event) {
        String msg = leagueService.saveLeague(event.getLeague());
        if (StringUtils.isNotBlank(msg)) {
            new NotificationError(msg);
            return;
        }
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
            season.setLeagueName(form.league.getName());
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
        upperGrid.setItems(leagueVos);
    }

    private void updateLowerGridList() {
        lowerGrid.setItems(leagueService.findChampions(leagueNameComboBox.getValue()));
    }
}